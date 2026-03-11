package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_ESCALA")
public class ParamEscalaAvaliacaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_escala_avaliacao")
    @SequenceGenerator(name = "seq_param_escala_avaliacao", sequenceName = "SEQ_PARAM_ESCALA", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "UUID", nullable = false)
    private UUID uuid;

    @Column(name = "NIVEL", nullable = false)
    private Integer nivel;

    @Column(name = "QUALITATIVA", length = 100, nullable = false)
    private String qualitativa;

    @Column(name = "DESCRICAO", length = 300, nullable = false)
    private String descricao;

    @Column(name = "QUANTITATIVA_DE", nullable = false, precision = 5, scale = 2)
    private BigDecimal quantitativaDe;

    @Column(name = "QUANTITATIVA_ATE", nullable = false, precision = 5, scale = 2)
    private BigDecimal quantitativaAte;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false)
    private Estado estado;
}
