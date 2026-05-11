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
@Table(name = "RH_T_CONTRATO_HISTORICO")
public class ContratoHistoricoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_contrato_hist")
    @SequenceGenerator(name = "seq_contrato_hist", sequenceName = "SEQ_CONTRATO_HIST", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "estado is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private Estado estado;


    @NotNull(message = "dataInicio is mandatory")
    @Column(name="data_inicio", nullable = false)
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    @Column(name="duracao")
    private Integer duracao;


    @NotNull(message = "versao is mandatory")
    @Column(name="versao", nullable = false)
    private Integer versao;


    @Column(name="obs")
    private String obs;


    @Column(name="uuid")
    private UUID uuid;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


}
