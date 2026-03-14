package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_LOGISTICA")
public class MissaoLogisticaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_logistica")
    @SequenceGenerator(name = "seq_missao_logistica", sequenceName = "SEQ_MISSAO_LOGISTICA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "prestadorServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoPrestadorEntity prestadorServId;

    @Column(name = "nome_seguradora", length = 200)
    private String nomeSeguradora;

    @Column(name = "ent_id")
    private Long entId;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "moeda", length = 50)
    private String moeda;

    @Column(name = "lugar_hospedagem", length = 200)
    private String lugarHospedagem;

    @Column(name = "flg_alimentacao", length = 3)
    private String flgAlimentacao;

    @Column(name = "valor_diario")
    private BigDecimal valorDiario;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "nr_dias")
    private Integer nrDias;

    @NotNull(message = "missaoServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoServicoEntity missaoServId;

    @Column(name = "flg_alojamento", length = 3)
    private String flgAlojamento;

    @Column(name = "cab_id")
    private Long cabId;

    @Column(name = "estado_cabimento", length = 100)
    private String estadoCabimento;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
