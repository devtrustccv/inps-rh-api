package cv.inps.rh.shared.application.service;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Traduz uma referência (FK) — tipo da entidade + id — para um nome legível: carrega a entidade por
 * chave primária e lê o seu rótulo de exibição. Usado pelo {@code DetalheAlteracoesService} no momento
 * em que congela o detalhe, pelo que o nome gravado é o que vigorava quando o maker submeteu.
 *
 * <p>Estratégia híbrida, pensada para ser <b>automática</b> em código gerado que não podemos anotar:
 * <ol>
 *   <li><b>Override tipado</b> ({@link #OVERRIDES}) — para os raros casos que fogem à convenção (ex.:
 *       compor nome+apelido). Vazio por omissão; preenche-se só quando preciso.</li>
 *   <li><b>Cascata por convenção</b> ({@link #GETTERS_CANDIDATOS}) — tenta {@code getNome},
 *       {@code getDesignacao}, {@code getDescricao}… e usa o primeiro não-vazio.</li>
 * </ol>
 * Se nada resolver (tipo sem getter de nome, entidade apagada), cai para o valor cru do id — nunca
 * rebenta a grelha.
 */
@Component
@RequiredArgsConstructor
public class ReferenciaNomeResolver {

  /** Getters candidatos a "rótulo de exibição", por ordem de preferência. */
  private static final List<String> GETTERS_CANDIDATOS =
      List.of("getNome", "getDesignacao", "getDescricao", "getTitulo", "getNomeCompleto", "getLabel", "getNmBanco",
          // ParamSituacaoDetalheEntity chama "motivo" ao seu rótulo (coluna MOTIVO). Sem isto a grelha
          // mostrava o fallback cru — "ParamSituacaoDetalheEntity #23" — ao aprovador.
          "getMotivo");

  /**
   * Overrides por tipo, para entidades que não sigam a convenção. Chave = FQN da classe da entidade.
   * Só se acrescenta quando uma entidade concreta precisar — tipicamente quando o rótulo visível é
   * COMPOSTO (nenhum getter único o dá).
   */
  private static final Map<String, Function<Object, String>> OVERRIDES = Map.of(
      // Escalão: o rótulo do UI ("16A") é nível + letra; nenhum getter único o devolve. Cai para o
      // código (ex.: "DIR_SERV_16_A") se o nível não estiver preenchido.
      ParamEscalaoEntity.class.getName(), e -> {
        var esc = (ParamEscalaoEntity) e;
        String composto = (esc.getNivelReferencia() != null ? esc.getNivelReferencia() : "")
            + (esc.getEscalao() != null ? esc.getEscalao() : "");
        return composto.isBlank() ? esc.getCodigo() : composto;
      },
      // Tipo de relacionamento (tiprel): não tem nome próprio; o rótulo legível é o NOME do funcionário
      // a que pertence. Usado ex.: no "Colaborador substituído" da grelha da Substituição, onde o campo
      // é o substituidoTiprelId (FK para o tiprel do substituído).
      TiposRelacionamentoEntity.class.getName(), e -> {
        var tr = (TiposRelacionamentoEntity) e;
        return tr.getFunId() != null ? tr.getFunId().getNome() : null;
      });

  private final EntityManager entityManager;

  /**
   * Nome legível de uma referência a partir do <b>tipo + id</b>. O {@code DetalheAlteracoesService} lê o
   * id do proxy da FK sem o inicializar, e o nome vem daqui — um {@code find()} por chave primária, que
   * é barato e, ao contrário de {@code proxy.getNome()}, nunca lança {@code LazyInitializationException}.
   *
   * <p>Fallback legível ({@code "DirecaoEntity #12"}) se a entidade não existir ou não tiver nenhum
   * dos getters de nome — a grelha nunca rebenta por causa disto.
   */
  public String resolver(Class<?> tipo, Object id) {
    if (tipo == null || id == null) {
      return null;
    }
    Object entidade = entityManager.find(tipo, id);
    if (entidade != null) {
      String nome = nomeDe(entidade, tipo.getName(), tipo);
      if (preenchido(nome)) {
        return nome;
      }
    }
    return tipo.getSimpleName() + " #" + id;
  }

  private String nomeDe(Object entidade, String typeName, Class<?> tipo) {
    // 1) override explícito, se registado para este tipo.
    Function<Object, String> override = OVERRIDES.get(typeName);
    if (override != null) {
      String s = override.apply(entidade);
      if (preenchido(s)) {
        return s;
      }
    }
    // 2) cascata por convenção: primeiro getter não-vazio ganha.
    for (String getter : GETTERS_CANDIDATOS) {
      try {
        Object v = tipo.getMethod(getter).invoke(entidade);
        if (v != null && preenchido(v.toString())) {
          return v.toString();
        }
      } catch (ReflectiveOperationException ignored) {
        // getter não existe nesta entidade — tenta o próximo candidato.
      }
    }
    return null;
  }

  private boolean preenchido(String s) {
    return s != null && !s.isBlank();
  }
}
