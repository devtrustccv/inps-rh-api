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
@Table(name = "RH_T_REGULARIZACAO_SDO")
public class RegularizacaoSdoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_REGULARIZACAO_SDO_id_gen")
  @SequenceGenerator(name = "RH_T_REGULARIZACAO_SDO_id_gen", sequenceName = "SEQ_REGULARIZACAO_SDO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 10)
  @NotNull
  @Column(name = "MES_REFERENTE", nullable = false, length = 10)
  private String mesReferente;

  @NotNull
  @Column(name = "SDO_RECEBIDO", nullable = false)
  private BigDecimal sdoRecebido;

  @NotNull
  @Column(name = "VALOR_RETROATIVO_SALARIO", nullable = false)
  private BigDecimal valorRetroativoSalario;

  @NotNull
  @Column(name = "VALOR_RETROATIVO_SDO", nullable = false)
  private BigDecimal valorRetroativoSdo;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "PROC_FUN_ID", nullable = false)
  private ProcessamentoFuncionarioEntity procFun;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ABONO_BENEFICIO_ID", nullable = false)
  private AbonosBeneficiosEntity abonoBeneficio;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
