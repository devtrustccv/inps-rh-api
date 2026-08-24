package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PROC_SALARIOS")
public class ProcessamentoSalarialEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_PROC_SALARIOS_id_gen")
  @SequenceGenerator(name = "RH_T_PROC_SALARIOS_id_gen", sequenceName = "SEQ_PROC_SALARIOS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "DATA_DE", nullable = false)
  private LocalDate dataDe;

  @NotNull
  @Column(name = "DATA_ATE", nullable = false)
  private LocalDate dataAte;

  @NotNull
  @Column(name = "CC_ID", nullable = false)
  private Long ccId;

  @Size(max = 5)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 5)
  private String estado;

  @Column(name = "DATA_PROC_PROVISORIO")
  private LocalDate dataProcProvisorio;

  @Column(name = "DATA_PROC_DEFINITIVO")
  private LocalDate dataProcDefinitivo;

  @Size(max = 200)
  @NotNull
  @Column(name = "OBS", nullable = false, length = 200)
  private String obs;

  @Size(max = 1)
  @Column(name = "FLG_FECHADO", length = 1)
  private String flgFechado;

  @Column(name = "CAB_1_ID")
  private Long cab1Id;

  @Column(name = "RH_PROC_SALARIOS")
  private Long rhProcSalarios;

  @Column(name = "TIPO_PROCESSAMENTO", length = 55)
  private String tipoProcessamento;

  @Column(name = "INSTIT_ID")
  private Long institId;

  @Size(max = 100)
  @Column(name = "USER_VALID_PROV", length = 100)
  private String userValidProv;

  @Size(max = 100)
  @Column(name = "USER_VALID_DEF", length = 100)
  private String userValidDef;

  @Size(max = 100)
  @Column(name = "USER_CABIMENTO", length = 100)
  private String userCabimento;

  @Size(max = 100)
  @Column(name = "USER_AUTORIZACAO", length = 100)
  private String userAutorizacao;
}
