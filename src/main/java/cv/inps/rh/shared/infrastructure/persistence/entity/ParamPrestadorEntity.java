/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "RH_T_PARAM_PRESTADOR")
public class ParamPrestadorEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_prestador")
  @SequenceGenerator(name = "seq_param_prestador", sequenceName = "SEQ_PARAM_PRESTADOR", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  // INPSSIGOF.ENTIDADES.ID
  @NotNull(message = "entId is mandatory")
  @Column(name = "ent_id", nullable = false)
  private Long entId;

  @NotBlank(message = "nome is mandatory")
  @Column(name = "nome", length = 200, nullable = false)
  private String nome;

  @Column(name = "nif", length = 20)
  private String nif;

  @NotBlank(message = "email is mandatory")
  @Column(name = "email", length = 200, nullable = false)
  private String email;

  @Column(name = "telefone", length = 30)
  private String telefone;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ilha_id", referencedColumnName = "id")
  private GeografiaEntity ilhaId;

  @Column(name = "morada", length = 300)
  private String morada;

  @NotBlank(message = "estado is mandatory")
  @Column(name = "estado", length = 1, nullable = false)
  private String estado;

  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false, length = 36)
  private UUID uuid;
}
