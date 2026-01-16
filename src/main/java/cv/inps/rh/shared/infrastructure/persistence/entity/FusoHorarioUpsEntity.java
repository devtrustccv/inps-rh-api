package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RH_FUSO_HORARIO_UPS")
public class FusoHorarioUpsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_FUSO_HORARIO_UPS_id_gen")
  @SequenceGenerator(name = "RH_FUSO_HORARIO_UPS_id_gen", sequenceName = "SEQ_FUSO_HORARIO_UPS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "ID_PARAMETRIZACAO")
  private Long idParametrizacao;

  @Column(name = "ID_UPS")
  private Long idUps;

  @Size(max = 150)
  @Column(name = "FUSO", length = 150)
  private String fuso;
}
