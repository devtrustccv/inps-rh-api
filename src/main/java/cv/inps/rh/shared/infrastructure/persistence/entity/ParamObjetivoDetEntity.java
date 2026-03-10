/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_OBJETIVO_DET")
public class ParamObjetivoDetEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_objetivo_det")
    @SequenceGenerator(name = "seq_param_objetivo_det", sequenceName = "SEQ_PARAM_OBJETIVO_DET", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="uuid")
    private UUID uuid;

  
}