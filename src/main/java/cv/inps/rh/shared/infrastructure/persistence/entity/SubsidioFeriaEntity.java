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
@Table(name = "RH_T_SUBSIDIO_FERIAS")
public class SubsidioFeriaEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_SUBSIDIO_FERIAS_id_gen")
  @SequenceGenerator(name = "RH_T_SUBSIDIO_FERIAS_id_gen", sequenceName = "SEQ_SUBSIDIO_FERIAS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "ANO_REFERENTE", nullable = false)
  private Long anoReferente;

  @NotNull
  @Column(name = "ESCALAO_ID", nullable = false)
  private Long escalaoId;

  @NotNull
  @Column(name = "MES_TRAB", nullable = false)
  private Long mesTrab;

  @NotNull
  @Column(name = "DIAS_TRAB", nullable = false)
  private Long diasTrab;

  @NotNull
  @Column(name = "VALOR_SUBSIDIO", nullable = false)
  private Long valorSubsidio;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity fun;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Size(max = 20)
  @Column(name = "DATA_FIM", length = 20)
  private String dataFim;

  @Column(name = "VALOR_MES")
  private Long valorMes;

  @Column(name = "VALOR_DIA")
  private Long valorDia;

  @Size(max = 100)
  @Column(name = "SITUACAO", length = 100)
  private String situacao;

  @Size(max = 20)
  @NotNull
  @Column(name = "FLG_ATIVO_INACTIVO", nullable = false, length = 20)
  private String flgAtivoInactivo;

  @Column(name = "PROC_SAL_ID")
  private Long procSalId;
}
