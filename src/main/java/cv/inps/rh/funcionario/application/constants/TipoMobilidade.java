package cv.inps.rh.funcionario.application.constants;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Tipos de mobilidade selecionáveis no ecrã "Nova Mobilidade" (multi-select), correspondentes ao
 * domínio {@code TIPO_MOV_LABORAL} / {@code REFERENCIA = MOBILIDADE}:
 * <ul>
 *   <li>{@code DIRECAO} — "Direcção"</li>
 *   <li>{@code SECAO} — "Unidade"</li>
 *   <li>{@code LOCAL_TRABALHO} — "Local de trabalho"</li>
 * </ul>
 *
 * <p>O frontend envia os CÓDIGOS separados por vírgula em {@link MobilidadeDTO#getTipoMobilidade()}
 * (ex.: {@code "DIRECAO,SECAO"}). Regras que este enum suporta:
 * <ul>
 *   <li>para cada tipo ESCOLHIDO o respetivo campo "(depois)" é OBRIGATÓRIO;</li>
 *   <li>os tipos NÃO escolhidos não mudam — copia-se o valor "antes" (mobilidade anterior) para o
 *       novo registo, sem o frontend ter de o reenviar.</li>
 * </ul>
 *
 * <p>Cada constante concentra: rótulo PT (mensagens), extrator do id "(depois)" do DTO, tipo da
 * entidade-alvo (para resolver o id numa referência) e get/set do campo correspondente na
 * {@link MobilidadeEntity}.
 */
public enum TipoMobilidade {

  DIRECAO("Direcção", MobilidadeDTO::getDirecaoDestino, DirecaoEntity.class,
      MobilidadeEntity::getInstidId, (m, v) -> m.setInstidId((DirecaoEntity) v)),
  SECAO("Unidade", MobilidadeDTO::getSeccaoDestino, SecaoEntity.class,
      MobilidadeEntity::getSecaoId, (m, v) -> m.setSecaoId((SecaoEntity) v)),
  LOCAL_TRABALHO("Local de trabalho", MobilidadeDTO::getLocalTrabalhoDestino, ParamLocalTrabEntity.class,
      MobilidadeEntity::getLocalTrabId, (m, v) -> m.setLocalTrabId((ParamLocalTrabEntity) v));

  private final String label;
  private final Function<MobilidadeDTO, Long> depoisId;
  private final Class<?> entityType;
  private final Function<MobilidadeEntity, Object> antes;
  private final BiConsumer<MobilidadeEntity, Object> setter;

  TipoMobilidade(String label, Function<MobilidadeDTO, Long> depoisId, Class<?> entityType,
                 Function<MobilidadeEntity, Object> antes, BiConsumer<MobilidadeEntity, Object> setter) {
    this.label = label;
    this.depoisId = depoisId;
    this.entityType = entityType;
    this.antes = antes;
    this.setter = setter;
  }

  public String getLabel() {
    return label;
  }

  public Class<?> getEntityType() {
    return entityType;
  }

  /** Id do campo "(depois)" que o frontend enviou para este tipo (null se não veio). */
  public Long depoisId(MobilidadeDTO dto) {
    return depoisId.apply(dto);
  }

  /** Valor "antes" deste campo, lido da mobilidade anterior (null se não houver anterior). */
  public Object antes(MobilidadeEntity anterior) {
    return anterior == null ? null : antes.apply(anterior);
  }

  /** Aplica o valor (referência resolvida ou cópia do "antes") ao campo desta entidade. */
  public void set(MobilidadeEntity mobilidade, Object valor) {
    setter.accept(mobilidade, valor);
  }

  /** Resolve um código do domínio (ex.: {@code "DIRECAO"}) para a constante, se for selecionável. */
  public static Optional<TipoMobilidade> fromCodigo(String codigo) {
    if (codigo == null) {
      return Optional.empty();
    }
    var normalizado = codigo.trim().toUpperCase();
    for (var tipo : values()) {
      if (tipo.name().equals(normalizado)) {
        return Optional.of(tipo);
      }
    }
    return Optional.empty();
  }
}
