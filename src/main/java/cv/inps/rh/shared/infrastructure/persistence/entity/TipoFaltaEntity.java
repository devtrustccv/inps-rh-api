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


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_TIPO_FALTAS")
public class TipoFaltaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipo_falta")
    @SequenceGenerator(name = "seq_tipo_falta", sequenceName = "SEQ_TIPO_FALTA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;


  @Column(name="tipo")
    private String tipo;


  @Column(name="tf_id")
    private Long tfId;


  @Column(name="falta")
    private String falta;


  @Column(name = "situacao")
  private String situacao;


  @Column(name = "desconto_remuneracao")
  private String descontoRemuneracao;


  @NotBlank(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false)
  private String uuid;


  @NotNull(message = "estado is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;


}
