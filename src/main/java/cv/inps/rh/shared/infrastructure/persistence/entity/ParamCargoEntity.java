/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

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
@Table(name = "RH_T_PARAM_CARGO")
public class ParamCargoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="uuid")
    private String uuid;


    @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_carr_id", referencedColumnName = "id")
    private ParamCarreiraEntity paramCarrId;
    @Column(name="dirigente", length=1)
    private String dirigente;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
