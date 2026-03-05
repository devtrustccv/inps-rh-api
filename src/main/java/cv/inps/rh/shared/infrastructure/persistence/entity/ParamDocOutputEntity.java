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
@Table(name = "RH_T_PARAM_DOC_OUTPUT")
public class ParamDocOutputEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rh_s_param_doc_output")
  @SequenceGenerator(name = "rh_s_param_doc_output", sequenceName = "RH_S_PARAM_DOC_OUTPUT", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "TIPO_DOCUMENTO", nullable = false)
  private String tipoDocumento;

  @Column(name = "TITULO", nullable = false)
  private String titulo;

  @Lob
  @Column(name = "CORPO")
  private String corpo;

  @Column(name = "ASSINADO_POR")
  private String assinadoPor;

  @ManyToOne
  @JoinColumn(name = "RESPONSAVEL_ID")
  private ResponsavelEntity responsavel;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;


}
