/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_FERIADO")
public class FeriadoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rh_t_param_feriado")
  @SequenceGenerator(name = "seq_rh_t_param_feriado", sequenceName = "SEQ_RH_T_PARAM_FERIADO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false)
  private String uuid;


  @NotNull(message = "anoReferente is mandatory")
  @Column(name = "ano_referente", nullable = false)
  private Integer anoReferente;


  @NotBlank(message = "descricao is mandatory")
  @Column(name = "descricao", nullable = false)
  private String descricao;


  @NotNull(message = "data is mandatory")
  @Column(name = "data", nullable = false)
  private LocalDate data;


  @NotNull(message = "estado is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;


}
