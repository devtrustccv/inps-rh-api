package cv.inps.rh.funcionario.application.service.historicolaboral;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.funcionario.application.service.GestaoLaboralValidacaoDetalheDescriptor;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoDetalheEntityRepository;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Motor de "Detalhe de alterações" da <b>Alteração de Escalão/Cargo</b> assente no <b>diff engine</b> do
 * JaVers ({@code javers.compare}) — <b>não</b> no histórico auto-audit (que não serve o tiprel por ser
 * Shallow Reference). Usa-se em dois modos:
 *
 * <ul>
 *   <li>{@link #persistir} — chamado na ESCRITA (registo/correção do movimento): grava uma linha por
 *       campo alterado em {@code RH_T_VALIDACAO_DETALHE}, exatamente como a spec DOSSIÊ manda (a grelha
 *       passa a ser lida pelo {@code ValidacaoDetalheReadService} comum). Snapshot imutável no momento —
 *       robusto a alterações posteriores do predecessor.</li>
 *   <li>{@link #comparar} — usado na LEITURA on-the-fly (fallback para movimentos antigos sem linhas
 *       persistidas). Mesmo diff, devolvido como DTO sem tocar na BD.</li>
 * </ul>
 *
 * <p><b>Como o {@code javers.compare} vê duas linhas de tiprel:</b> mapeamos cada tiprel para um
 * {@link Snapshot} com {@code @Id} <b>constante</b> — assim o JaVers trata "antes" e "depois" como duas
 * versões da MESMA instância e emite {@link ValueChange} campo-a-campo (em vez de "objeto novo/removido",
 * que é o que aconteceria com ids diferentes). Os campos do snapshot já vêm formatados para exibição e
 * os seus nomes coincidem com as chaves de {@link GestaoLaboralValidacaoDetalheDescriptor#rotulos()}.
 */
@Component
@RequiredArgsConstructor
public class EscalaoDetalheDiffWriter {

  private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  private static final String TABELA = "RH_T_TIPOS_RELACIONAMENTO";

  private final Javers javers;
  private final ValidacaoDetalheEntityRepository detalheRepo;
  private final GestaoLaboralValidacaoDetalheDescriptor descriptor;

  /**
   * Snapshot sem identidade real (só um {@code @Id} constante) — ValueObject de comparação. Campos já
   * formatados; nomes iguais às chaves de rótulos do descritor, para o {@code propertyName} do
   * {@link ValueChange} mapear direto.
   */
  public static final class Snapshot {
    @org.javers.core.metamodel.annotation.Id
    private final String id = "escalao"; // constante → antes/depois = mesma instância p/ o JaVers
    private final String escalaoId;
    private final String cargoId;
    private final String salario;
    private final String moeda;
    private final String tipoSituacao;
    private final String dataInicio;
    private final String dataFim;
    private final String obs;

    private Snapshot(String escalaoId, String cargoId, String salario, String moeda,
        String tipoSituacao, String dataInicio, String dataFim, String obs) {
      this.escalaoId = escalaoId;
      this.cargoId = cargoId;
      this.salario = salario;
      this.moeda = moeda;
      this.tipoSituacao = tipoSituacao;
      this.dataInicio = dataInicio;
      this.dataFim = dataFim;
      this.obs = obs;
    }
  }

  /** Monta o snapshot formatado de um tiprel (null → snapshot todo-nulo = estado "antes" inexistente). */
  public Snapshot snapshot(TiposRelacionamentoEntity tr) {
    if (tr == null) {
      return new Snapshot(null, null, null, null, null, null, null, null);
    }
    return new Snapshot(escalao(tr), cargo(tr), valor(tr.getSalario()), tr.getMoeda(),
        tr.getTipoSituacao(), data(tr.getDataInicio()), data(tr.getDataFim()), tr.getObs());
  }

  /** Diff antes→depois (só campos alterados) como DTOs — leitura on-the-fly, sem persistir. */
  public List<ValidacaoDetalheDTO> comparar(TiposRelacionamentoEntity antes, TiposRelacionamentoEntity depois) {
    Map<String, String> rotulos = descriptor.rotulos();
    return javers.compare(snapshot(antes), snapshot(depois))
        .getChangesByType(ValueChange.class)
        .stream()
        .map(vc -> {
          var dto = new ValidacaoDetalheDTO();
          dto.setCampoAlterado(rotulos.getOrDefault(vc.getPropertyName(), vc.getPropertyName()));
          dto.setValorAnterior(str(vc.getLeft()));
          dto.setValorNovo(str(vc.getRight()));
          dto.setTabelaName(TABELA);
          return dto;
        })
        .toList();
  }

  /** Mesmo diff, mas PERSISTE em RH_T_VALIDACAO_DETALHE (uma linha por campo alterado). */
  public void persistir(ValidacaoEntity validacao, TiposRelacionamentoEntity antes, TiposRelacionamentoEntity depois) {
    Map<String, String> rotulos = descriptor.rotulos();
    javers.compare(snapshot(antes), snapshot(depois))
        .getChangesByType(ValueChange.class)
        .forEach(vc -> {
          var e = new ValidacaoDetalheEntity();
          e.setValidacaoId(validacao);
          e.setCampoAlterado(rotulos.getOrDefault(vc.getPropertyName(), vc.getPropertyName()));
          e.setValorAnterior(str(vc.getLeft()));
          e.setValorNovo(str(vc.getRight()));
          e.setTabelaName(TABELA);
          e.setUuid(UuidCreator.getTimeOrderedEpoch());
          detalheRepo.save(e);
        });
  }

  /** Apaga as linhas de detalhe de uma validação (reenvio de correção regrava do zero). */
  public void limpar(UUID validacaoUuid) {
    var existentes = detalheRepo.findByValidacaoId_UuidOrderByTabelaNameAscIdAsc(validacaoUuid);
    if (!existentes.isEmpty()) {
      detalheRepo.deleteAll(existentes);
    }
  }

  // --- formatação de exibição (fonte única; espelha a lista de Gestão Laboral) ---

  private String escalao(TiposRelacionamentoEntity tr) {
    ParamEscalaoEntity e = tr.getEscalaoId();
    if (e == null) {
      return null;
    }
    if (e.getCodigo() != null && !e.getCodigo().isBlank()) {
      return e.getCodigo();
    }
    String nivel = e.getNivelReferencia() == null ? "" : String.valueOf(e.getNivelReferencia());
    String esc = e.getEscalao() == null ? "" : e.getEscalao();
    String display = (nivel + "/" + esc).trim();
    return "/".equals(display) ? String.valueOf(e.getId()) : display;
  }

  private String cargo(TiposRelacionamentoEntity tr) {
    ParamCargoEntity c = tr.getCargoId();
    return c == null ? null : c.getNome();
  }

  private String valor(BigDecimal v) {
    return v == null ? null : v.stripTrailingZeros().toPlainString();
  }

  private String data(LocalDate d) {
    return d == null ? null : d.format(DATA);
  }

  private String str(Object value) {
    return value == null ? null : value.toString();
  }
}
