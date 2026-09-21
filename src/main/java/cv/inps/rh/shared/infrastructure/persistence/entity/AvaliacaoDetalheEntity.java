package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Resultado de uma avaliação num período.
 *
 * <p>É aqui que passam a viver os campos de resultado que estavam em {@link AvaliacaoEntity}
 * (notas parciais, nota final, classificação qualitativa, entrevista, observação geral,
 * parecer do colaborador e observação da comissão executiva). A {@code RH_T_AVD} fica só com
 * a identificação — ano, abrangência, colaborador, direção, unidade, carreira, cargo — e
 * ganha N detalhes, um por período do ciclo.</p>
 */
@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AVD_DETALHE")
public class AvaliacaoDetalheEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_detalhe")
    @SequenceGenerator(name = "seq_avd_detalhe", sequenceName = "SEQ_AVD_DETALHE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacao;

    /** Período concreto: SEMESTRE1, TRIMESTRE3, ANUAL, ... (domínio PERIODICIDADE). */
    @Column(name = "PERIODICIDADE", length = 100, nullable = false)
    private String periodicidade;

    @Column(name = "AVALIACAO_OBJECTIVO", precision = 10, scale = 2)
    private BigDecimal avaliacaoObjectivo;

    @Column(name = "AVALIACAO_COMPETENCIA", precision = 10, scale = 2)
    private BigDecimal avaliacaoCompetencia;

    @Column(name = "AVALIACAO_ATITUDE_PESS", precision = 10, scale = 2)
    private BigDecimal avaliacaoAtitudePess;

    /** Expressão quantitativa do período: soma das três componentes ponderadas. */
    @Column(name = "AVALIACAO_FINAL", precision = 10, scale = 2)
    private BigDecimal avaliacaoFinal;

    /** Expressão qualitativa, obtida do escalão de RH_T_PARAM_ESCALA onde a nota cai. */
    @Column(name = "AVALIACAO_QUALITATIVA", length = 100)
    private String avaliacaoQualitativa;

    @Column(name = "OBSERVACAO_GERAL", length = 300)
    private String observacaoGeral;

    @Column(name = "DESCRICAO_PLANO", length = 300)
    private String descricaoPlano;

    @Column(name = "DATA_INICIO_ENTREVISTA")
    private LocalDate dataInicioEntrevista;

    @Column(name = "HORA_INICIO_ENTREVISTA", length = 100)
    private String horaInicioEntrevista;

    @Column(name = "HORA_FIM_ENTREVISTA", length = 100)
    private String horaFimEntrevista;

    @Column(name = "PARECER_COLABORADOR", length = 300)
    private String parecerColaborador;

    @Column(name = "JUSTIFICACAO_MOTIVO", length = 300)
    private String justificacaoMotivo;

    @Column(name = "OBS_COMISSAO_EXEC", length = 300)
    private String obsComissaoExec;

    @Column(name = "ESTADO", length = 1, nullable = false)
    private String estado;

    @Column(name = "UUID", nullable = false)
    private UUID uuid;
}
