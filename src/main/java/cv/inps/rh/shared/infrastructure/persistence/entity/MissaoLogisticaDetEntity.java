package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_LOGISTICA_DET")
public class MissaoLogisticaDetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_logistica_det")
    @SequenceGenerator(name = "seq_missao_logistica_det", sequenceName = "SEQ_MISSAO_LOGISTICA_DET", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "missaoLogistId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_logist_id", referencedColumnName = "id", nullable = false)
    private MissaoLogisticaEntity missaoLogistId;

    @NotNull(message = "missaoColabId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_colab_id", referencedColumnName = "id", nullable = false)
    private MissaoColaboradorEntity missaoColabId;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 3, nullable = false)
    private String estado;
}
