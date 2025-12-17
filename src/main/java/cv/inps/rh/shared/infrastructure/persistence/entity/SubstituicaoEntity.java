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

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_SUBSTITUICAO")
public class SubstituicaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_substituicao")
    @SequenceGenerator(name = "seq_substituicao", sequenceName = "SEQ_SUBSTITUICAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "tiprelIdPara is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substituido_tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity substituidoTiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substituto_tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity substitutoTiprelId;
    @Column(name="data_inicio")
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    @Column(name="motivo", length=200)
    private String motivo;


    @Column(name="obs", length=4000)
    private String obs;


    @Column(name="uuid")
    private UUID uuid;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
