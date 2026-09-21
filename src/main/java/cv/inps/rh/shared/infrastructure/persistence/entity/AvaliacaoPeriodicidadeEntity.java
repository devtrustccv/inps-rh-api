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

/**
 * Medição de uma componente de avaliação num período concreto.
 *
 * <p>Substitui os campos {@code REALIZADO} / {@code AVALIACAO} / {@code AUTO_REALIZADO} /
 * {@code AUTO_AVALIACAO} que viviam em {@link AvaliacaoObjectivoEntity},
 * {@link AvaliacaoCompetenciaEntity} e {@link AvaliacaoAtitudePessoalEntity} — essas colunas
 * continuam nas tabelas mas ficaram legado. A {@code PONDERACAO} não migrou: continua na
 * linha da componente, porque é da definição do objectivo e não muda de período para período.</p>
 *
 * <p>A ligação à componente é polimórfica: {@link #referencia} diz qual a tabela
 * (ver {@link cv.inps.rh.shared.application.constants.ComponenteAvaliacaoRef}) e
 * {@link #referenciaId} o id da linha. Não há FK — é o preço de ter uma única tabela de
 * medições para as três componentes.</p>
 */
@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AVD_PERIODICIDADE")
public class AvaliacaoPeriodicidadeEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_periodicidade")
    @SequenceGenerator(name = "seq_avd_periodicidade", sequenceName = "SEQ_AVD_PERIODICIDADE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacao;

    /** Período concreto: SEMESTRE1, TRIMESTRE3, ANUAL, ... (domínio PERIODICIDADE). */
    @Column(name = "PERIODICIDADE", length = 100, nullable = false)
    private String periodicidade;

    /** Que componente se está a medir. Ver {@code ComponenteAvaliacaoRef}. */
    @Column(name = "REFERENCIA", length = 200, nullable = false)
    private String referencia;

    /** Id da linha da componente, na tabela que {@link #referencia} indica. */
    @Column(name = "REFERENCIA_ID", nullable = false)
    private Long referenciaId;

    /** DEFINICAO ou AVALIACAO. Ver {@code TipoProcessoAvaliacao}. */
    @Column(name = "TIPO_PROCESSO", length = 100, nullable = false)
    private String tipoProcesso;

    @Column(name = "REALIZADO", length = 300)
    private String realizado;

    @Column(name = "AVALIACAO", precision = 5, scale = 2)
    private BigDecimal avaliacaoValor;

    @Column(name = "AUTO_REALIZADO", length = 300)
    private String autoRealizado;

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

    @Column(name = "ESTADO", length = 1, nullable = false)
    private String estado;

    @Column(name = "UUID", nullable = false)
    private UUID uuid;
}
