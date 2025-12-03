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
@Table(name = "RH_T_TIPOS_DOCUMENTOS")
public class TipoDocumentoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipo_documento")
    @SequenceGenerator(name = "seq_tipo_documento", sequenceName = "SEQ_TIPO_DOCUMENTO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="uuid")
    private UUID uuid;


    @NotBlank(message = "referencia is mandatory")
    @Column(name="referencia", nullable = false)
    private String referencia;


    @Column(name="codigo")
    private String codigo;


    @Column(name="nome")
    private String nome;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
