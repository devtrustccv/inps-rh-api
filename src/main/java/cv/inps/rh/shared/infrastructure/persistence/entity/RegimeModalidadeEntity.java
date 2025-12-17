/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "RH_T_REGIME_MODAL")
public class RegimeModalidadeEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_regime_modal")
    @SequenceGenerator(name = "seq_regime_modal", sequenceName = "SEQ_REGIME_MODAL", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotBlank(message = "modalidade is mandatory")
    @Column(name="modalidade", nullable = false)
    private String modalidade;


    @Column(name="dias_semana")
    private String diasSemana;


    @Column(name="num_horas")
    private Integer numHoras;


    @Column(name="uuid")
    private UUID uuid;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "regime_id")
   private RegimeTrabalhoEntity regimeId;


}
