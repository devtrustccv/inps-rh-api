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
   * Desconto de falta no salário.
   *
   * <p>A falta desconta via <em>pagamento</em>, não via remuneração: PROCESSA_FALTA usa
   * o tipo de movimento PAG_FALTA, grava em RH_T_DEF_PAGAMENTOS e actualiza esta coluna
   * (package body, linha 2496).
   *
   * <p>Existiu aqui um DEF_REM_ID que nunca chegou a ser usado — sem dados, sem PL/SQL
   * e sem vistas a referi-lo — e foi removido da tabela. Não confundir com
   * RH_T_HORA_EXTRA.DEF_REM_ID, esse sim alimentado por PROCESSA_HORA: a hora extra
   * acresce como remuneração, a falta desconta como pagamento.
   */
  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "def_pag_id", referencedColumnName = "id")
    private DefPagamentoEntity defPagId;


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
