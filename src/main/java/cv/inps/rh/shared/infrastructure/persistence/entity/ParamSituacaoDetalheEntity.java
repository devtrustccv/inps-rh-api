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

import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_SITUACAO_DET")
public class ParamSituacaoDetalheEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_situacao_det")
    @SequenceGenerator(name = "seq_param_situacao_det", sequenceName = "SEQ_PARAM_SITUACAO_DET", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "situacaoLaboralId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_id", referencedColumnName = "id")
    private ParamSituacaoEntity situacaoId;
    @Column(name="motivo")
    private String motivo;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="uuid")
    private UUID uuid;


}
