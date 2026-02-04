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
@Table(name = "RH_T_DOCUMENTO")
public class DocumentoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_documento")
  @SequenceGenerator(name = "seq_documento", sequenceName = "SEQ_DOCUMENTO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @Column(name = "uuid")
  private UUID uuid;


  @NotNull(message = "tpDocumentoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tp_documento_id", referencedColumnName = "id")
  private TipoDocumentoEntity tpDocumentoId;
  @Column(name = "doc_id")
  private Long docId;


  @Column(name = "referencia_name")
  private String referenciaName;


  @Column(name = "referencia_id")
  private String referenciaId;

  @Column(name = "referencia_uuid")
  private UUID referenciaUuid;


  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private Estado estado;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id")
  private FuncionarioEntity funId;

  @Column(name = "url")
  private String url;


}
