/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DOCUMENTO")
public class DocumentoEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="uuid")
    private String uuid;

  
    @NotNull(message = "tpDocumentoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_documento_id", referencedColumnName = "id")
    private TipoDocumentoEntity tpDocumentoId;
    @Column(name="doc_id")
    private Long docId;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;
    @Column(name="referencia_name")
    private String referenciaName;

  
    @Column(name="referencia_id")
    private String referenciaId;

  
    @Column(name="estado")
    private String estado;

  
}