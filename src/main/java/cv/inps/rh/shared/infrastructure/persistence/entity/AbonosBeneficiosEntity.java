/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_ABONOS_BENEFICIOS")
public class AbonosBeneficiosEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_abonos_beneficios")
  @SequenceGenerator(name = "seq_abonos_beneficios", sequenceName = "SEQ_ABONOS_BENEFICIOS", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @Column(name = "uuid")
  private UUID uuid;


  @Column(name = "data_inicio")
  private LocalDate dataInicio;


  @Column(name = "data_fim")
  private LocalDate dataFim;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_sit_id", referencedColumnName = "id")
    private ParamSituacaoEntity paramSitId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_sit_det_id", referencedColumnName = "id")
    private ParamSituacaoDetalheEntity paramSitDetId;


    @Column(name="obs", length=4000)
    private String obs;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
