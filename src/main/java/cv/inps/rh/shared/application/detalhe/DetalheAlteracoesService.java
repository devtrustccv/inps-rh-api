package cv.inps.rh.shared.application.detalhe;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.service.ReferenciaNomeResolver;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoDetalheEntityRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementação do {@link DetalheAlteracoes}: compara dois estados capturados e congela o resultado
 * em {@code RH_T_VALIDACAO_DETALHE}.
 *
 * <p>O motor de comparação é um ciclo sobre os campos declarados. Não há biblioteca de diff: com
 * valores já formatados, comparar é {@code Objects.equals}, e ter a comparação aqui dá-nos duas
 * coisas que uma biblioteca genérica não dava — a ordem é garantidamente a de declaração, e as FKs
 * comparam-se por id em vez de por nome.
 */
@Service
@RequiredArgsConstructor
public class DetalheAlteracoesService implements DetalheAlteracoes {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetalheAlteracoesService.class);

  /** Largura das colunas VALOR_* depois do alargamento (era 500). */
  private static final int MAX_VALOR = 2000;

  private final EntityManagerFactory entityManagerFactory;
  private final ReferenciaNomeResolver referenciaNomeResolver;
  private final ValidacaoDetalheEntityRepository validacaoDetalheEntityRepository;

  @Override
  public <T> Estado capturar(Campos<T> campos, T alvo) {
    PersistenceUnitUtil util = entityManagerFactory.getPersistenceUnitUtil();
    List<Campos.Campo> lista = campos.lista();
    List<Valor> valores = new ArrayList<>(lista.size());

    for (Campos.Campo c : lista) {
      Object bruto = (alvo == null) ? null : c.acessor().apply(alvo);
      if (bruto == null) {
        valores.add(Valor.VAZIO);
      } else if (c.referencia()) {
        // O acessor devolveu a FK tal como está — possivelmente um proxy não inicializado.
        // getIdentifier() lê o id SEM disparar SELECT (é o contrato do JPA: o valor já está na
        // coluna FK do pai). Nunca chamamos getNome() no proxy — era isso que dava lazy init com
        // open-in-view=false. O nome vem de um find() por chave primária, barato e seguro.
        Long id = comoLong(util.getIdentifier(bruto));
        valores.add(new Valor(referenciaNomeResolver.resolver(c.tipoFk(), id), id));
      } else {
        valores.add(new Valor(c.formatador().apply(bruto), null));
      }
    }
    return new Estado(List.copyOf(valores));
  }

  @Override
  public <T> List<CampoAlterado> comparar(Campos<T> campos, Estado antes, Estado depois) {
    List<Campos.Campo> lista = campos.lista();
    if (antes.valores().size() != lista.size() || depois.valores().size() != lista.size()) {
      throw new IllegalArgumentException(
          "Estado capturado com outra declaração de campos (esperado " + lista.size() + ")");
    }

    List<CampoAlterado> alterados = new ArrayList<>();
    for (int i = 0; i < lista.size(); i++) {
      Campos.Campo c = lista.get(i);
      Valor esquerda = antes.valores().get(i);
      Valor direita = depois.valores().get(i);

      // FK compara-se pelo ID: renomear uma direção na parametrização não é uma alteração da
      // mobilidade, e não deve aparecer na grelha do aprovador.
      boolean igual = c.referencia()
          ? Objects.equals(esquerda.id(), direita.id())
          : Objects.equals(esquerda.display(), direita.display());
      if (igual) {
        continue;
      }

      alterados.add(new CampoAlterado(
          c.nome(), c.rotulo(),
          esquerda.display(), esquerda.id(),
          direita.display(), direita.id(),
          i + 1,
          tipo(c, esquerda)));
    }
    return alterados;
  }

  @Override
  @Transactional
  public <T> int congelar(ValidacaoEntity validacao, String tabela,
      Campos<T> campos, Estado antes, Estado depois) {
    return congelar(validacao, tabela, null, campos, antes, depois);
  }

  @Override
  @Transactional
  public <T> int congelar(ValidacaoEntity validacao, String tabela, Long tabelaId,
      Campos<T> campos, Estado antes, Estado depois) {

    // Linhas já congeladas para esta validação, nesta tabela E nesta linha. Uma validação pode
    // atravessar várias tabelas (registo de colaborador) e, dentro da mesma tabela, várias linhas
    // (dados bancários) — cada combinação congela-se de forma independente.
    List<ValidacaoDetalheEntity> existentes = validacaoDetalheEntityRepository
        .findByValidacaoId_UuidOrderByTabelaNameAscIdAsc(validacao.getUuid())
        .stream()
        .filter(e -> tabela.equals(e.getTabelaName()) && Objects.equals(tabelaId, e.getTabelaId()))
        .toList();

    List<CampoAlterado> resultado = fundir(campos, existentes, comparar(campos, antes, depois));

    // Insert-once: o detalhe é regravado do zero a partir do resultado da fusão.
    existentes.forEach(validacaoDetalheEntityRepository::delete);
    List<ValidacaoDetalheEntity> linhas = resultado.stream()
        .map(c -> paraEntidade(validacao, tabela, tabelaId, c))
        .toList();

    validacaoDetalheEntityRepository.saveAll(linhas);
    LOGGER.debug("Detalhe congelado: validacao={} tabela={} tabelaId={} linhas={} (existentes={})",
        validacao.getUuid(), tabela, tabelaId, linhas.size(), existentes.size());
    return linhas.size();
  }

  /**
   * Funde o diff desta edição com o detalhe já congelado da mesma validação.
   *
   * <p><b>Porquê:</b> num reenvio de correção o "antes" desta edição é o estado <em>pendente</em>, não
   * o último <em>aprovado</em> — os registos do dossiê são editados in place, pelo que a proposta
   * anterior já está na linha. Congelar só o diff da última edição faria a grelha dizer
   * "DGFI → DCC" quando o que está em cima da mesa para aprovação é "DARH → DCC", e faria
   * desaparecer campos alterados na 1.ª submissão que a correção não voltou a tocar.
   *
   * <p>Regras:
   * <ul>
   *   <li>campo já congelado → mantém-se o <b>valorAnterior original</b> (o último aprovado) e
   *       adopta-se o valorNovo desta edição;</li>
   *   <li>campo só nesta edição → entra tal e qual;</li>
   *   <li>campo só no congelado → mantém-se (continua por aprovar, a correção não lhe tocou);</li>
   *   <li>se depois da fusão o antes for igual ao novo, a linha cai — o maker reverteu e não há
   *       alteração líquida a aprovar.</li>
   * </ul>
   */
  private <T> List<CampoAlterado> fundir(Campos<T> campos, List<ValidacaoDetalheEntity> existentes,
      List<CampoAlterado> novos) {

    Map<String, Campos.Campo> declaracao = campos.lista().stream()
        .collect(Collectors.toMap(Campos.Campo::nome, c -> c, (a, b) -> a, LinkedHashMap::new));
    Map<String, ValidacaoDetalheEntity> congelados = existentes.stream()
        .filter(e -> e.getCampo() != null)
        .collect(Collectors.toMap(ValidacaoDetalheEntity::getCampo, e -> e, (a, b) -> a, LinkedHashMap::new));
    Map<String, CampoAlterado> desta = novos.stream()
        .collect(Collectors.toMap(CampoAlterado::campo, c -> c, (a, b) -> a, LinkedHashMap::new));

    List<CampoAlterado> fundidos = new ArrayList<>();
    for (String campo : uniao(congelados.keySet(), desta.keySet())) {
      ValidacaoDetalheEntity congelado = congelados.get(campo);
      CampoAlterado novo = desta.get(campo);

      // O "antes" é sempre o mais antigo que conhecemos: o já congelado tem precedência.
      String anteriorDisplay = congelado != null ? congelado.getValorAnterior() : novo.valorAnterior();
      Long anteriorId = congelado != null ? congelado.getValorAnteriorId() : novo.valorAnteriorId();
      // O "depois" é sempre o mais recente.
      String novoDisplay = novo != null ? novo.valorNovo() : congelado.getValorNovo();
      Long novoId = novo != null ? novo.valorNovoId() : congelado.getValorNovoId();

      Campos.Campo decl = declaracao.get(campo);
      boolean referencia = decl != null ? decl.referencia()
          : CampoAlterado.Tipo.REFERENCIA.name().equals(congelado == null ? null : congelado.getTipoAlteracao());

      // Reversão: o maker voltou ao valor aprovado. Não há nada para o checker decidir neste campo.
      boolean semAlteracaoLiquida = referencia
          ? Objects.equals(anteriorId, novoId)
          : Objects.equals(anteriorDisplay, novoDisplay);
      if (semAlteracaoLiquida) {
        continue;
      }

      String rotulo = novo != null ? novo.rotulo() : congelado.getCampoAlterado();
      int ordem = novo != null ? novo.ordem()
          : (congelado.getOrdem() != null ? congelado.getOrdem() : fundidos.size() + 1);
      CampoAlterado.Tipo tipo = (anteriorDisplay == null && anteriorId == null)
          ? CampoAlterado.Tipo.INICIAL
          : referencia ? CampoAlterado.Tipo.REFERENCIA : CampoAlterado.Tipo.VALOR;

      fundidos.add(new CampoAlterado(campo, rotulo, anteriorDisplay, anteriorId,
          novoDisplay, novoId, ordem, tipo));
    }

    fundidos.sort(Comparator.comparingInt(CampoAlterado::ordem));
    return fundidos;
  }

  private Set<String> uniao(Set<String> a, Set<String> b) {
    Set<String> todos = new LinkedHashSet<>(a);
    todos.addAll(b);
    return todos;
  }

  private ValidacaoDetalheEntity paraEntidade(ValidacaoEntity validacao, String tabela, Long tabelaId, CampoAlterado c) {
    var e = new ValidacaoDetalheEntity();
    e.setValidacaoId(validacao);
    e.setCampo(c.campo());
    // CAMPO_ALTERADO continua a ser o RÓTULO — é o que o frontend já lê. O nome técnico vai na
    // coluna CAMPO, nova.
    e.setCampoAlterado(c.rotulo());
    e.setOrdem(c.ordem());
    e.setTipoAlteracao(c.tipo().name());
    e.setValorAnterior(truncar(c.valorAnterior()));
    e.setValorAnteriorId(c.valorAnteriorId());
    e.setValorNovo(truncar(c.valorNovo()));
    e.setValorNovoId(c.valorNovoId());
    e.setTabelaName(tabela);
    e.setTabelaId(tabelaId);
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    return e;
  }

  private CampoAlterado.Tipo tipo(Campos.Campo c, Valor anterior) {
    if (anterior.vazio()) {
      return CampoAlterado.Tipo.INICIAL;
    }
    return c.referencia() ? CampoAlterado.Tipo.REFERENCIA : CampoAlterado.Tipo.VALOR;
  }

  private Long comoLong(Object id) {
    return (id instanceof Number n) ? n.longValue() : null;
  }

  /** {@code obs} é campo livre; truncar explicitamente é melhor do que rebentar no INSERT. */
  private String truncar(String v) {
    return (v == null || v.length() <= MAX_VALOR) ? v : v.substring(0, MAX_VALOR - 1) + "…";
  }
}
