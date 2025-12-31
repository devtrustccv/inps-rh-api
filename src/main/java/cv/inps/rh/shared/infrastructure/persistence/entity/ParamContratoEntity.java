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
@Table(name = "RH_T_PARAM_CONTRATO")
public class ParamContratoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_contrato")
  @SequenceGenerator(name = "seq_param_contrato", sequenceName = "SEQ_PARAM_CONTRATO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @Column(name = "uuid")
  private UUID uuid;


  @NotBlank(message = "codigo is mandatory")
  @Column(name = "codigo", nullable = false)
  private String codigo;


  @Column(name = "nome")
  private String nome;


  @Column(name = "natureza")
  private String natureza;


  @Column(name = "flg_renovavel")
  private Integer flgRenovavel;


  @Column(name = "duracao_renovavel")
  private Integer duracaoRenovavel;


  @Column(name = "prazo_obrigatorio")
  private Integer prazoObrigatorio;


  @Column(name = "max_renovacao")
  private Integer maxRenovacao;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "param_vinculo_id", referencedColumnName = "id")
  private ParamVinculoEntity paramVinculoId;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private Estado estado;


}
