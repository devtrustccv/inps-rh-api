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
@Table(name = "RH_T_SIM_EVOLUCAO_CARREIRA")
public class SimEvolucaoCarreiraEntity extends AuditEntity {

  @Id
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "CARREIRA_ID_DE", nullable = false)
  private CarreiraEntity carreiraIdDe;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ESCALAO_ID_DE", nullable = false)
  private ParamEscalaoEntity escalaoIdDe;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ESCALAO_ID_PARA", nullable = false)
  private ParamEscalaoEntity escalaoIdPara;

  @NotNull
  @Column(name = "DATA_REFERENTE", nullable = false)
  private LocalDate dataReferente;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "TIPREL_ID", nullable = false)
  private TiposRelacionamentoEntity tiprel;

  @Size(max = 500)
  @Column(name = "OBSERVACAO", length = 500)
  private String observacao;

  @Size(max = 1)
  @NotNull
  @Column(name = "FLG_HISTORICO", nullable = false, length = 1)
  private String flgHistorico;

  @Size(max = 1)
  @NotNull
  @Column(name = "TIPO", nullable = false, length = 1)
  private String tipo;

  @Column(name = "AVALIACAO_MEDIA")
  private Long avaliacaoMedia;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;


}
