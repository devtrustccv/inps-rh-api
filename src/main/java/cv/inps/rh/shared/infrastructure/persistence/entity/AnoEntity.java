/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_ANO")
public class AnoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ano")
    @SequenceGenerator(name = "seq_ano", sequenceName = "SEQ_ANO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "ano is mandatory")
    @Column(name="ano", nullable = false)
    private String ano;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="uuid")
    private String uuid;

  
}