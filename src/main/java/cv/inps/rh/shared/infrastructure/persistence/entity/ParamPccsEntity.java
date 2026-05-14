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
@Table(name = "RH_T_PARAM_PCCS")
public class ParamPccsEntity extends AuditEntity {

    @Id
    @SequenceGenerator(name = "seq_param_pccs", sequenceName = "SEQ_PARAM_PCCS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_pccs")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "flg_copia_anterior")
    private Integer flgCopiaAnterior;

    @Column(name = "flg_fechar_anterior")
    private Integer flgFecharAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado;
}
