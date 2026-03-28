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
@Table(name = "RH_T_AVD_COMPETENCIA")
public class AvaliacaoCompetenciaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_competencia")
    @SequenceGenerator(name = "seq_avd_competencia", sequenceName = "SEQ_AVD_COMPETENCIA", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAM_OBJECTIVO_ID", nullable = false)
    private ParamObjetivoEntity paramObjetivo;

    @Column(name = "NUMERO_ORDEM", nullable = false)
    private Integer numeroOrdem;

    @Column(name = "ABRAGENCIA", length = 100, nullable = false)
    private String abrangencia;

    @Column(name = "DESCRICAO", length = 300, nullable = false)
    private String descricao;

    @Column(name = "PONDERACAO", nullable = false, precision = 5, scale = 2)
    private BigDecimal ponderacao;

    @Column(name = "COMPONENTE", length = 100, nullable = false)
    private String componente; // COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA

    @Column(name = "PESO", precision = 5, scale = 2)
    private BigDecimal peso; // peso relativo dentro da subcomponente

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

  @Column(name = "avaliacao", precision = 5, scale = 2)
  private BigDecimal avaliacaoProcessual;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "UUID")
    private UUID uuid;
}
