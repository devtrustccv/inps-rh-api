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
@Table(name = "RH_T_EVOLUCAO_CARREIRA")
public class EvolucaoCarreiraEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_EVOLUCAO_CARREIRA_id_gen")
  @SequenceGenerator(name = "RH_T_EVOLUCAO_CARREIRA_id_gen", sequenceName = "SEQ_EVOLUCAO_CARREIRA", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CARREIRA_ID_DE", nullable = false)
  private CarreiraEntity carreiraIdDe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARREIRA_ID_PARA")
  private CarreiraEntity carreiraIdPara;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ESCALAO_ID_DE", nullable = false)
  private ParamEscalaoEntity escalaoIdDe;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ESCALAO_ID_PARA", nullable = false)
  private ParamEscalaoEntity escalaoIdPara;

  @NotNull
  @Column(name = "DATA_REFERENTE", nullable = false)
  private LocalDate dataReferente;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "TIPREL_ID", nullable = false)
  private TiposRelacionamentoEntity tiprel;

  @Size(max = 500)
  @Column(name = "OBSERVACAO", length = 500)
  private String observacao;

  @Size(max = 1)
  @Column(name = "TIPO", length = 1)
  private String tipo;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Size(max = 3)
  @Column(name = "ORDEM_SERVICO_ID", length = 3)
  private String ordemServicoId;

  @Column(name = "AVALIACAO_MEDIA")
  private Long avaliacaoMedia;
}
