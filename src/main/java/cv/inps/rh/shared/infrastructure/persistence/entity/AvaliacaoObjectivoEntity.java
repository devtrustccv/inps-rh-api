package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
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
@Table(name = "RH_T_AVD_OBJECTIVO")
public class AvaliacaoObjectivoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_objectivo")
    @SequenceGenerator(name = "seq_avd_objectivo", sequenceName = "SEQ_AVD_OBJECTIVO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacaoObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAM_OBJECTIVO_ID")
    private ParamObjetivoEntity paramObjetivo;

    @Column(name = "NUMERO_ORDEM", nullable = false)
    private Integer numeroOrdem;

    @Column(name = "ABRAGENCIA", length = 100, nullable = false)
    private String abrangencia;

    @Column(name = "OBJECTIVOS", length = 300, nullable = false)
    private String objectivos;

    @Column(name = "KPI", length = 300)
    private String kpi;

    @Column(name = "META", length = 300)
    private String meta;

    @Column(name = "PONDERACAO", precision = 5, scale = 2)
    private BigDecimal ponderacao;

    @Column(name = "AUTO_REALIZADO", length = 200)
    private String autoRealizado;

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

    @Column(name = "REALIZADO", length = 200)
    private String realizado;

    @Column(name = "AVALIACAO", precision = 5, scale = 2)
    private BigDecimal avaliacao;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "UUID")
    private UUID uuid;
}
