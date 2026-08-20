package cv.inps.rh.shared.application.service;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Traduz uma referência (FK) do JaVers — guardada como {@link GlobalId}, ex.: {@code SecaoEntity/3} —
 * para um nome legível. Resolução <b>read-time</b>: carrega a entidade pelo id e lê o seu rótulo de
 * exibição. (Ver decisão: mostramos o nome ATUAL da referência, não o histórico; se uma secção for
 * renomeada, os detalhes antigos passam a exibir o nome novo.)
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

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferenciaNomeResolver.class);

  /** Getters candidatos a "rótulo de exibição", por ordem de preferência. */
  private static final List<String> GETTERS_CANDIDATOS =
      List.of("getNome", "getDesignacao", "getDescricao", "getTitulo", "getNomeCompleto", "getLabel");

  /**
   * Overrides por tipo, para entidades que não sigam a convenção. Chave = nome do tipo JaVers (FQN da
   * classe da entidade). Só se acrescenta quando uma entidade concreta precisar — tipicamente quando o
   * rótulo visível é COMPOSTO (nenhum getter único o dá).
   */
  private static final Map<String, Function<Object, String>> OVERRIDES = Map.of(
      // Escalão: o rótulo do UI ("16A") é nível + letra; nenhum getter único o devolve. Cai para o
      // código (ex.: "DIR_SERV_16_A") se o nível não estiver preenchido.
      ParamEscalaoEntity.class.getName(), e -> {
        var esc = (ParamEscalaoEntity) e;
        String composto = (esc.getNivelReferencia() != null ? esc.getNivelReferencia() : "")
            + (esc.getEscalao() != null ? esc.getEscalao() : "");
        return composto.isBlank() ? esc.getCodigo() : composto;
      });

  private final EntityManager entityManager;

  /**
   * Devolve o nome legível da referência, ou o id cru como fallback. {@code null} se a referência for
   * {@code null}.
   */
  public String resolver(GlobalId globalId) {
    if (globalId == null) {
      return null;
    }
    if (globalId instanceof InstanceId instanceId) {
      try {
        Class<?> tipo = Class.forName(instanceId.getTypeName());
        Long id = Long.valueOf(String.valueOf(instanceId.getCdoId()));
        Object entidade = entityManager.find(tipo, id);
        if (entidade != null) {
          String nome = nomeDe(entidade, instanceId.getTypeName(), tipo);
          if (preenchido(nome)) {
            return nome;
          }
        }
      } catch (ReflectiveOperationException | NumberFormatException e) {
        LOGGER.debug("Não foi possível resolver nome da referência {}: {}", globalId.value(), e.toString());
      }
      // Resolução falhou: fallback legível (nome simples do tipo + id), nunca a FQN completa.
      String tipo = instanceId.getTypeName();
      return tipo.substring(tipo.lastIndexOf('.') + 1) + " #" + instanceId.getCdoId();
    }
    return globalId.value(); // não-InstanceId: valor cru do JaVers
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
