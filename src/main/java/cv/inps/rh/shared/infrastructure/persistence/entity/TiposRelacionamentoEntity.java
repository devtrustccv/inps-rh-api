/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import cv.inps.rh.shared.application.constants.Estado;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


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


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instit_id", referencedColumnName = "id")
    private InstituicaoEntity institId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinculo_id", referencedColumnName = "id")
    private ParamVinculoEntity vinculoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccao_id", referencedColumnName = "id")
    private SecaoEntity seccaoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private ParamCategoriaEntity categoriaId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalao_id", referencedColumnName = "id")
    private ParamEscalaoEntity escalaoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carr_pcc_id", referencedColumnName = "id")
    private ParamCarreiraEntity carrPccId;
    @Column(name="salario")
    private BigDecimal salario;

  
    @Column(name="moeda")
    private String moeda;

  
    @Column(name="regime")
    private String regime;

  
    @Column(name="tipo_situacao")
    private String tipoSituacao;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="flg_processa")
    private String flgProcessa;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="data_inicio_contrato")
    private LocalDate dataInicioContrato;

  
    @Column(name="data_fim_contrato")
    private LocalDate dataFimContrato;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


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
    @JoinColumn(name = "loc_trab_id", referencedColumnName = "id")
    private ParamLocalTrabEntity locTrabId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regime_id", referencedColumnName = "id")
    private RegimeTrabalhoEntity regimeId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id", referencedColumnName = "id")
    private ParamContratoEntity tipoContratoId;
    @Column(name="referente")
    private String referente;

  
    @Column(name="ult_proc")
    private LocalDate ultProc;

  
    @Column(name="motivo_sit_lab")
    private String motivoSitLab;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situac_laboral_id", referencedColumnName = "id")
    private ParamSitLaboralEntity situacLaboralId;
    @Column(name="tp_contrato")
    private String tpContrato;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}