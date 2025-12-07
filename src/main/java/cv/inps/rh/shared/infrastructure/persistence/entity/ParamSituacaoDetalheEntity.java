/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_SIT_LABORAL_DET")
public class ParamSituacaoDetalheEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_sit_laboral_det")
    @SequenceGenerator(name = "seq_param_sit_laboral_det", sequenceName = "SEQ_PARAM_SIT_LABORAL_DET", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "situacaoLaboralId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_laboral_id", referencedColumnName = "id")
    private ParamSitLaboralEntity situacaoLaboralId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinculo_id", referencedColumnName = "id")
    private ParamVinculoEntity vinculoId;
    @Column(name="motivo")
    private String motivo;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="uuid")
    private UUID uuid;

  
}