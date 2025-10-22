/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DOCUMENTO_PESSOAL")
public class DocumentoPessoalEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "numDocumento is mandatory")
    @Column(name="numdocumento", nullable = false)
    private String numDocumento;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_documento_id", referencedColumnName = "id")
    private TipoDocumentoEntity tipoDocumentoId;
    @Column(name="estado")
    private String estado;

  
    @Column(name="uuid")
    private UUID uuid;

  
}