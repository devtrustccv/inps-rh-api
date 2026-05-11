/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AUSENCIA")
public class AusenciaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ausencia")
    @SequenceGenerator(name = "seq_ausencia", sequenceName = "SEQ_AUSENCIA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotNull(message = "paramSitId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_sit_id", referencedColumnName = "id")
    private ParamSituacaoEntity paramSitId;
    @Column(name="referencia_name")
    private String referenciaName;


  @Column(name="referencia_id")
    private Long referenciaId;


  @Column(name="obs")
    private String obs;


  @Column(name="uuid")
    private UUID uuid;


  @Column(name="hora")
    private Integer hora;


  @Column(name="data_inicio")
    private LocalDate dataInicio;


  @Column(name="data_fim")
    private LocalDate dataFim;


  @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
