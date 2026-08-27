/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_TIPOS_RELACIONAMENTO")
public class TiposRelacionamentoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tip_rel")
    @SequenceGenerator(name = "seq_tip_rel", sequenceName = "SEQ_TIP_REL", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private ParamCargoEntity cargoId;

  // Escalão gravado DIRECTAMENTE no tiprel para vínculos SEM carreira (SIM_PCCS mas flg_carreira=0):
  // nestes casos não há RH_T_CARREIRA que segure o escalão, por isso o id fica aqui
  // (RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID). Para vínculos COM carreira, o escalão continua a vir da
  // carreira (carreiraId.escalaoId) e esta coluna fica null.
  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalao_id", referencedColumnName = "id")
    private ParamEscalaoEntity escalaoId;
    @Column(name="salario")
    private BigDecimal salario;


  @Column(name="moeda")
    private String moeda;


  @Column(name="tipo_situacao")
    private String tipoSituacao;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="flg_processa")
    private Integer flgProcessa;


  @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


  @Column(name="obs", length=4000)
    private String obs;


  @Column(name="data_inicio")
    private LocalDate dataInicio;


  @Column(name="data_fim")
    private LocalDate dataFim;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contr_vinculo_id", referencedColumnName = "id")
    private ContratoEntity contrVinculoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carreira_id", referencedColumnName = "id")
    private CarreiraEntity carreiraId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mob_id", referencedColumnName = "id")
    private MobilidadeEntity mobId;
    @NotNull(message = "uuid is mandatory")
    @Column(name="uuid", nullable = false)
    private UUID uuid;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regime_id", referencedColumnName = "id")
    private RegimeTrabalhoEntity regimeId;
    @Column(name="referente")
    private String referente;


  @Column(name="ult_proc")
    private LocalDate ultProc;


  @Column(name="motivo_sit_lab")
    private String motivoSitLab;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situac_laboral_id", referencedColumnName = "id")
    private SituacaoLaboralEntity situacLaboralId;
    @Column(name="est_act_adm")
    private Integer estActAdm;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}
