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
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "RH_T_MISSAO_SERVICO")
public class MissaoServicoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_servico")
    @SequenceGenerator(name = "seq_missao_servico", sequenceName = "SEQ_MISSAO_SERVICO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "nrMissao is mandatory")
    @Column(name = "nr_missao", nullable = false)
    private Long nrMissao;

    @NotNull(message = "paisDestinoId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_destino_id", referencedColumnName = "id", nullable = false)
    private GeografiaEntity paisDestinoId;

    @Column(name = "flg_destino")
    private Integer flgDestino;

    @Column(name = "descricao_destino", length = 200)
    private String descricaoDestino;

    @NotNull(message = "dataInicio is mandatory")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @NotNull(message = "nrDias is mandatory")
    @Column(name = "nr_dias", nullable = false)
    private Integer nrDias;

    @NotBlank(message = "autorizadoPor is mandatory")
    @Column(name = "autorizado_por", length = 200, nullable = false)
    private String autorizadoPor;

    @NotNull(message = "dataAutorizacao is mandatory")
    @Column(name = "data_autorizacao", nullable = false)
    private LocalDate dataAutorizacao;

    @NotBlank(message = "etapa is mandatory")
    @Column(name = "etapa", length = 50, nullable = false)
    private String etapa;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @Column(name = "referencia_pagamento", length = 200)
    private String referenciaPagamento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
