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

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_SITUACAO_LABORAL")
public class SituacaoLaboralEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_situacao_laboral")
    @SequenceGenerator(name = "seq_situacao_laboral", sequenceName = "SEQ_SITUACAO_LABORAL", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "situacaoLaboralId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_laboral_id", referencedColumnName = "id")
    private ParamSituacaoEntity situacaoLaboralId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_sit_lab_id", referencedColumnName = "id")
    private ParamSituacaoDetalheEntity motivoSitLabId;

    @Column(name="tipo_situacao")
    private String tipoSituacao;


    @Column(name="data_inicio")
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="obs", length=4000)
    private String obs;


    @Column(name="uuid")
    private UUID uuid;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "contr_vinculo_id")
   private ContratoEntity contrVinculoId;


}
