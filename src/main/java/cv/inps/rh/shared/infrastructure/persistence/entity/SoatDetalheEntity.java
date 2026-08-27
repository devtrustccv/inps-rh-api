package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "RH_T_SOAT_DETALHE")
public class SoatDetalheEntity extends AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_SOAT_DETALHE_id_gen")
  @SequenceGenerator(name = "RH_T_SOAT_DETALHE_id_gen", sequenceName = "SEQ_SOAT_DETALHE", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "SOAT_ID", nullable = false)
  private SoatEntity soat;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity fun;

  @NotNull
  @Column(name = "DIR_SERV_ID", nullable = false)
  private Long dirServId;

  @NotNull
  @Column(name = "NU_TRAB_AUTO", nullable = false)
  private Long nuTrabAuto;

  @NotNull
  @Column(name = "NU_TRAB_MAN", nullable = false)
  private Long nuTrabMan;

  @NotNull
  @Column(name = "VL_REMUN_AUTO", nullable = false, precision = 18, scale = 2)
  private BigDecimal vlRemunAuto;

  @NotNull
  @Column(name = "VL_REMUN_MAN", nullable = false, precision = 18, scale = 2)
  private BigDecimal vlRemunMan;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "PR_REM_ID", nullable = false)
  private RhTRemuneracoe prRem;
}
