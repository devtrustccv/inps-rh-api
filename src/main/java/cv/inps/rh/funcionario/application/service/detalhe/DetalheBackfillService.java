package cv.inps.rh.funcionario.application.service.detalhe;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DetalheBackfillResultadoDTO;
import cv.inps.rh.shared.application.service.JaversValidacaoDetalheReadService;
import cv.inps.rh.shared.application.service.JaversValidacaoDetalheReadService.LinhaJavers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Copia para {@code RH_T_VALIDACAO_DETALHE} o detalhe das validações que ainda só existe no histórico
 * do JaVers, para as tabelas {@code JV_*} poderem ser largadas sem as grelhas antigas ficarem vazias.
 *
 * <p><b>Temporário.</b> Corre uma vez, antes da remoção do JaVers, e sai com ele.
 *
 * <p>Regras:
 * <ul>
 *   <li><b>Idempotente</b> — só toca em validações sem nenhuma linha de detalhe; correr duas vezes não
 *       duplica nada.</li>
 *   <li><b>Uma transacção por validação</b> — uma falha fica registada no resultado e não desfaz as
 *       restantes.</li>
 *   <li><b>Autor e data originais</b> — as linhas levam o autor e a data do commit do JaVers, não os
 *       de quem corre o backfill. Grava-se por SQL nativo porque {@code DATA_REGISTO} e
 *       {@code USER_REGISTO_NAME} são {@code updatable=false} e seriam carimbados pelo auditing.</li>
 *   <li><b>Fidelidade, não correcção</b> — copia a grelha tal como o JaVers a mostra hoje, com os
 *       defeitos conhecidos (ex.: linha fantasma por snapshot dessincronizado). O estado anterior real
 *       já não existe na base, pelo que não há como os corrigir.</li>
 *   <li><b>Fundível</b> — {@code CAMPO}, {@code TIPO_ALTERACAO} e, nos módulos em que o motor novo o usa,
 *       {@code TABELA_ID} são preenchidos com os mesmos valores que o motor novo gravaria. Assim uma
 *       validação antiga ainda pendente, se for corrigida, funde-se com estas linhas em vez de as
 *       duplicar.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class DetalheBackfillService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetalheBackfillService.class);

  private static final int MAX_VALOR = 2000;
  private static final String AUTOR_DESCONHECIDO = "backfill-javers";

  /** Referências cuja grelha era servida pelo histórico do JaVers. */
  private static final Set<String> REFERENCIAS_JAVERS = Set.of(
      "MOBILIDADE", "CARREIRA", "DADOS_BANCARIOS", "PROCESSO_DISCIPLINAR", "REGISTO_COLABORADOR",
      "RENDIMENTO", "DESCONTO", "RENOVACAO_CONTRATO", "ESTADO_COLABORADOR", "SUBSTITUICAO");

  /**
   * Referências em que o motor novo congela por LINHA ({@code TABELA_ID}). Nas restantes grava
   * {@code TABELA_ID} a null; copiar o id do JaVers nelas impedia a fusão numa correcção posterior.
   */
  private static final Set<String> COM_TABELA_ID = Set.of("DADOS_BANCARIOS", "REGISTO_COLABORADOR");

  /**
   * No registo de colaborador o núcleo do funcionário e do contrato era auditado por POJOs próprios,
   * com nomes de propriedade diferentes dos da entidade. O motor novo usa os da entidade.
   */
  private static final Map<String, String> CAMPO_POJO_PARA_ENTIDADE = Map.of(
      "nomeMae", "nmMae",
      "nomePai", "nmPai",
      "genero", "sexo",
      "naturalidade", "locNascId",
      "numSegurado", "nuSegInps",
      "tipoContrato", "tpContratoId",
      "vinculo", "vinculoId");

  @PersistenceContext
  private EntityManager entityManager;

  private final JaversValidacaoDetalheReadService javersLeitor;
  private final PlatformTransactionManager transactionManager;

  /** Validação a migrar. */
  private record Alvo(Long id, UUID uuid, String referencia, LocalDateTime data, String autor) {}

  public DetalheBackfillResultadoDTO executar(boolean dryRun) {
    List<Alvo> alvos = alvosSemDetalhe();
    var tx = new TransactionTemplate(transactionManager);

    Map<String, int[]> porReferencia = new LinkedHashMap<>(); // [validacoes, vazias, linhas]
    List<String> erros = new ArrayList<>();
    int comLinhas = 0;
    int vazias = 0;
    int totalLinhas = 0;

    for (Alvo alvo : alvos) {
      int[] contagem = porReferencia.computeIfAbsent(alvo.referencia(), k -> new int[3]);
      contagem[0]++;
      try {
        Integer gravadas = tx.execute(status -> migrar(alvo, dryRun));
        int n = gravadas == null ? 0 : gravadas;
        if (n == 0) {
          vazias++;
          contagem[1]++;
        } else {
          comLinhas++;
          contagem[2] += n;
          totalLinhas += n;
        }
      } catch (RuntimeException e) {
        LOGGER.warn("Backfill falhou para a validacao {} ({})", alvo.uuid(), alvo.referencia(), e);
        erros.add(alvo.referencia() + " " + alvo.uuid() + ": " + e.getMessage());
      }
    }

    List<DetalheBackfillResultadoDTO.PorReferencia> resumo = porReferencia.entrySet().stream()
        .map(e -> new DetalheBackfillResultadoDTO.PorReferencia(
            e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]))
        .toList();

    LOGGER.info("Backfill do detalhe (dryRun={}): {} validacoes, {} com linhas, {} vazias, {} linhas, {} erros",
        dryRun, alvos.size(), comLinhas, vazias, totalLinhas, erros.size());
    return new DetalheBackfillResultadoDTO(dryRun, alvos.size(), comLinhas, vazias, totalLinhas, resumo, erros);
  }

  // ------------------------------------------------------------------------------------------------

  private List<Alvo> alvosSemDetalhe() {
    @SuppressWarnings("unchecked")
    List<Object[]> linhas = entityManager.createNativeQuery("""
        SELECT v.id, v.uuid, v.referencia_name, v.data_registo, v.user_registo_name
          FROM RH_T_VALIDACAO v
         WHERE v.referencia_name IN (:referencias)
           AND NOT EXISTS (SELECT 1 FROM RH_T_VALIDACAO_DETALHE d WHERE d.validacao_id = v.id)
         ORDER BY v.id
        """)
        .setParameter("referencias", REFERENCIAS_JAVERS)
        .getResultList();

    return linhas.stream()
        .map(r -> new Alvo(
            ((Number) r[0]).longValue(),
            UUID.fromString(String.valueOf(r[1])),
            String.valueOf(r[2]),
            r[3] instanceof Timestamp ts ? ts.toLocalDateTime() : null,
            r[4] == null ? null : String.valueOf(r[4])))
        .toList();
  }

  /** @return número de linhas gravadas (ou a gravar, em dry-run) */
  private int migrar(Alvo alvo, boolean dryRun) {
    List<LinhaJavers> linhas = javersLeitor.linhas(alvo.uuid());
    if (linhas.isEmpty() || dryRun) {
      return linhas.size();
    }
    boolean comTabelaId = COM_TABELA_ID.contains(alvo.referencia());
    int ordem = 0;
    for (LinhaJavers l : linhas) {
      ordem++;
      entityManager.createNativeQuery("""
          INSERT INTO RH_T_VALIDACAO_DETALHE
            (ID, VALIDACAO_ID, CAMPO_ALTERADO, VALOR_ANTERIOR, VALOR_NOVO, TABELA_NAME, TABELA_ID,
             DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME, UUID, CAMPO, ORDEM, TIPO_ALTERACAO)
          VALUES
            (SEQ_VALIDACAO_DETALHE.NEXTVAL, :validacao, :rotulo, :antes, :novo, :tabela, :tabelaId,
             :data, 1, :autor, :uuid, :campo, :ordem, :tipo)
          """)
          .setParameter("validacao", alvo.id())
          .setParameter("rotulo", l.rotulo())
          .setParameter("antes", truncar(l.valorAnterior()))
          .setParameter("novo", truncar(l.valorNovo()))
          .setParameter("tabela", l.tabela())
          .setParameter("tabelaId", comTabelaId ? l.tabelaId() : null)
          .setParameter("data", Timestamp.valueOf(dataDe(l, alvo)))
          .setParameter("autor", autorDe(l, alvo))
          .setParameter("uuid", UuidCreator.getTimeOrderedEpoch().toString())
          .setParameter("campo", campoDe(l))
          .setParameter("ordem", ordem)
          .setParameter("tipo", l.tipoAlteracao())
          .executeUpdate();
    }
    return linhas.size();
  }

  private String campoDe(LinhaJavers l) {
    boolean pojoDoRegisto = "RH_T_FUNCIONARIOS".equals(l.tabela()) || "RH_T_CONTRATO_VINCULO".equals(l.tabela());
    return pojoDoRegisto ? CAMPO_POJO_PARA_ENTIDADE.getOrDefault(l.campo(), l.campo()) : l.campo();
  }

  private LocalDateTime dataDe(LinhaJavers l, Alvo alvo) {
    if (l.data() != null) return l.data();
    if (alvo.data() != null) return alvo.data();
    return LocalDateTime.now();
  }

  private String autorDe(LinhaJavers l, Alvo alvo) {
    if (l.autor() != null && !l.autor().isBlank()) return l.autor();
    if (alvo.autor() != null && !alvo.autor().isBlank()) return alvo.autor();
    return AUTOR_DESCONHECIDO;
  }

  private String truncar(String v) {
    return (v == null || v.length() <= MAX_VALOR) ? v : v.substring(0, MAX_VALOR - 1) + "…";
  }
}
