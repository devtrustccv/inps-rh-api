package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_ABONOS_BENEFICIOS_DET")
public class AbonosBeneficiosDetalheEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_ABONOS_BENEFICIOS_DET_id_gen")
  @SequenceGenerator(name = "RH_T_ABONOS_BENEFICIOS_DET_id_gen", sequenceName = "SEQ_ABONO_BENEFICIO_DET", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "DATA_INICIO", nullable = false)
  private LocalDate dataInicio;

  @NotNull
  @Column(name = "DATA_FIM", nullable = false)
  private LocalDate dataFim;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ABONO_BENEF_ID", nullable = false)
  private AbonosBeneficiosEntity abonoBenef;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
