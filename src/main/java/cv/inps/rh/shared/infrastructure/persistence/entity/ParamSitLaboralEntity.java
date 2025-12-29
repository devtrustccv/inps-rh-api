package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PARAM_SIT_LABORAL")
public class ParamSitLaboralEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARAM_SIT_LABORAL_DET")
  @SequenceGenerator(name = "SEQ_PARAM_SIT_LABORAL_DET", sequenceName = "SEQ_PARAM_SIT_LABORAL_DET", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "PARAM_SIT_ID", nullable = false)
  private ParamSituacaoEntity paramSit;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "VINCULO_ID", nullable = false)
  private ParamVinculoEntity vinculo;

  @Column(name = "ESTADO")
  private String estado;


}
