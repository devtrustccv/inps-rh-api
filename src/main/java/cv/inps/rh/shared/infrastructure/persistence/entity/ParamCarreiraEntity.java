/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

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
@Table(name = "RH_T_PARAM_CARREIRA")
public class ParamCarreiraEntity extends AuditEntity {

    @Id
    @SequenceGenerator(name = "seq_param_carreira", sequenceName = "SEQ_PARAM_CARREIRA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_carreira")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "uuid is mandatory")
    @Column(name="uuid", nullable = false)
    private UUID uuid;


    @Column(name="nome")
    private String nome;


    @Column(name="codigo")
    private String codigo;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
