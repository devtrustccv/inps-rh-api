package cv.inps.rh.processamento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "RH_V_VALIDACAO")
@Immutable
public class RhValidacaoView {

    @Id
    @Column(name = "ID")   // Se a view não tiver ID, usamos uma coluna técnica
    private Long id;

    @Column(name = "PRSALS_ID")
    private Long processamentoId;

    @Column(name = "TIPO")
    private String tipoValidacao;

    @Column(name = "NOME_COLABORADOR")
    private String nomeColaborador;

    @Column(name = "NIB")
    private String nib;

    @Column(name = "TIPO_MOVIMENTO")
    private String tipoMovimento;

    @Column(name = "VALOR_ANTERIOR")
    private BigDecimal valorAnterior;

    @Column(name = "VALOR_ATUAL")
    private BigDecimal valorAtual;

    @Column(name = "MES_ANTERIOR")
    private String mesAnterior;

    @Column(name = "MES_ATUAL")
    private String mesAtual;

    @Column(name = "NUMERO")
    private Integer numero;

    @Column(name = "SITUACAO_LABORAL")
    private String situacaoLaboral;

    @Column(name = "VALOR_ESCALAO")
    private BigDecimal valorEscalao;
}
