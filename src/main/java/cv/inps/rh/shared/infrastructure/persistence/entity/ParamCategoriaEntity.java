/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_CATEGORIA")
public class ParamCategoriaEntity extends AuditEntity {

    @Id
    @SequenceGenerator(name = "seq_param_categoria", sequenceName = "SEQ_PARAM_CATEGORIA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_categoria")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_carr_id", referencedColumnName = "id")
    private ParamCarreiraEntity paramCarrId;
    @Column(name="uuid")
    private UUID uuid;


    @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;


    @Column(name="codigo")
    private String codigo;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
