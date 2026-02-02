package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PEDIDO_DECISAO")
public class PedidoDecisaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_PEDIDO_DECISAO_id_gen")
  @SequenceGenerator(name = "RH_T_PEDIDO_DECISAO_id_gen", sequenceName = "SEQ_DECISAO_PEDIDO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "PEDIDO_ID", nullable = false)
  private PedidoEntity pedido;

  @Size(max = 100)
  @NotNull
  @Column(name = "DECISAO", nullable = false, length = 100)
  private String decisao;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @Size(max = 100)
  @NotNull
  @Column(name = "ETAPA", nullable = false, length = 100)
  private String etapa;

  @Size(max = 100)
  @NotNull
  @Column(name = "REFERENCIA", nullable = false, length = 100)
  private String referencia;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
