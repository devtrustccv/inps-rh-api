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


  @Column(name = "ano_referente", nullable = false)
  private Integer anoReferente;

  @NotBlank(message = "descricao is mandatory")
  @Column(name = "descricao", nullable = false)
  private String descricao;

  @Column(name = "data_especifica", nullable = false)
  private LocalDate dataEspecifica;


  @NotNull(message = "estado is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;

  @Column(name = "situacao")
  private String situacao;

  @Column(name = "desconto_remuneracao")
  private String descontoRemuneracao;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "geogr_id", referencedColumnName = "id")
  private GeografiaEntity geogrId;

  @Column(name = "tipo_feriado")
  private String tipoFeriado;

  @Column(name = "fixo_ano")
  private String fixoAno;

  @Column(name = "dia")
  private Integer dia;

  @Column(name = "mes")
  private Integer mes;


}
