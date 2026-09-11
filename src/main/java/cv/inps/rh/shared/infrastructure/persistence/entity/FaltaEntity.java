/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FALTA")
public class FaltaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_falta")
    @SequenceGenerator(name = "seq_falta", sequenceName = "SEQ_FALTA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "pedidoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sintese_diario_id", referencedColumnName = "id")
    private AssiduidadeSinteseDiarioEntity sinteseDiarioId;


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="descricao_motivo")
    private String descricaoMotivo;


    @Column(name="decisao_responsavel")
    private String decisaoResponsavel;


    @Column(name="obs_responsavel")
    private String obsResponsavel;


    @Column(name="despacho_rh")
    private String despachoRh;


    @Column(name="horas_ausencia")
    private String horasAusencia;


    @Column(name="data_inicio")
    private LocalDateTime dataInicio;


    @Column(name="data_fim")
    private LocalDateTime dataFim;


    @Column(name="flg_desconto_sal")
    private Integer flgDescontoSal;


    @Column(name="flg_justificativo")
    private String flgJustificativo;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="uuid")
    private UUID uuid;




  /**
   * Desconto de falta no salário, em {@code RH_T_DEF_REMUNERACOES}.
   *
   * <p><b>Preenchido pelo procedimento do processamento salarial</b>, não pelo Java (decisão com o
   * DBA, 11/09): o procedimento lê as faltas em {@code A} ainda sem {@code DEF_REM_ID}, cria a
   * definição com o valor de {@link #valorDesconto} e grava aqui o id. A partir desse momento a
   * falta está apanhada pelo processamento e o editar/eliminar do pedido ficam trancados.
   */
  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "def_rem_id", referencedColumnName = "id")
    private DefinicaoRemuneracaoEntity defRemId;


  /**
   * O que esta falta desconta no vencimento — o <b>líquido</b>: {@link #valor} (bruto) menos o que
   * férias ou dispensa cobriram, e {@code 0} se o tipo não desconta salário ou o saldo cobriu tudo.
   * É a coluna que o procedimento do processamento salarial lê.
   *
   * <p>{@code null} = ainda não apurado: só se preenche quando a falta passa a {@code A} e volta a
   * {@code null} quando o editar ou o eliminar revertem os efeitos.
   */
  @Column(name="valor_desconto")
    private BigDecimal valorDesconto;


  /**
   * Onde a falta é deduzida: {@code FERIAS} ou {@code DISPENSA} (domínio
   * TP_DESCONTO_FALTA). Escolha explícita do RH no formulário "Deduzir Falta Em";
   * antes era inferida do tipo de justificação.
   */
  @Column(name="flg_desconto_falta")
    private String flgDescontoFalta;


  @Column(name="tipo")
    private String tipo;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_sit_id", referencedColumnName = "id")
    private ParamSituacaoEntity paramSitId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", referencedColumnName = "id")
    private ResponsavelEntity responsavelId;
    @Column(name="valor")
    private BigDecimal valor;

}
