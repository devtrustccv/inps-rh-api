package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_REQUISICAO")
public class MissaoRequisicaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_requisicao")
    @SequenceGenerator(name = "seq_missao_requisicao", sequenceName = "SEQ_MISSAO_REQUISICAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "missaoPrestId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_prest_id", referencedColumnName = "id", nullable = false)
    private MissaoPrestadorEntity missaoPrestId;

    @NotNull(message = "missaoColabId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_colab_id", referencedColumnName = "id", nullable = false)
    private MissaoColaboradorEntity missaoColabId;

    @NotNull(message = "estado is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 1, nullable = false)
    private Estado estado;

    @Column(name = "uuid", length = 100)
    private UUID uuid;
}
