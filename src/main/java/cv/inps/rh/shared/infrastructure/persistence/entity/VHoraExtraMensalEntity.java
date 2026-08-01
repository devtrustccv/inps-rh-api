/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "RH_V_HORA_EXTRA_MENSAL")
public class VHoraExtraMensalEntity  {

    /** Chave sintética: {@code hora_extra_id * 1000000 + YYYYMM}. */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="hora_extra_id")
    private Long horaExtraId;


    @Column(name="hora_extra_uuid")
    private String horaExtraUuid;


    /** Mês de referência, formato YYYYMM. */
    @Column(name="mes")
    private String mesReferencia;


    @Column(name="mes_numero")
    private Integer mesNumero;


    /** Início do período integral do registo (não recortado ao mês). */
    @Column(name="periodo_inicio")
    private LocalDate periodoInicio;


    @Column(name="periodo_fim")
    private LocalDate periodoFim;


    @Column(name="dias_uteis")
    private Integer diasUteis;


    @Column(name="dias_nao_uteis")
    private Integer diasNaoUteis;


    @Column(name="horas_extra_diarias")
    private BigDecimal horasExtraDiarias;


    @Column(name="percentagem_util")
    private BigDecimal percentagemUtil;


    @Column(name="percentagem_nao_util")
    private BigDecimal percentagemNaoUtil;


    @Column(name="valor_diario_util")
    private BigDecimal valorDiarioUtil;


    @Column(name="valor_diario_nao_util")
    private BigDecimal valorDiarioNaoUtil;


    /** Valor acumulado neste mês — já é o somatório. */
    @Column(name="valor_acumulado_mes")
    private BigDecimal valorAcumuladoMes;


    /** Total do período tal como CALCULO_HORA_EXTRA o devolveu. */
    @Column(name="valor_periodo")
    private BigDecimal valorPeriodo;


    @Column(name="data_pedido")
    private LocalDate dataPedido;


    @NotBlank(message = "funcionarioId is mandatory")
    @Column(name="funcionario_id", nullable = false)
    private String funcionarioId;


    @Column(name="funcionario_uuid")
    private UUID funcionarioUuid;


    @Column(name="nome_funcionario")
    private String nomeFuncionario;


    @Column(name="cargo_id")
    private Long cargoId;


    @Column(name="nome_cargo")
    private String nomeCargo;


    @Column(name="id_direcao")
    private Long idDirecao;


    @Column(name="nome_direcao")
    private String nomeDirecao;


    @Column(name="id_secao")
    private Long idSecao;


    @Column(name="nome_secao")
    private String nomeSecao;


    @Column(name="id_ilha")
    private Long idIlha;


    @Column(name="nome_ilha")
    private String nomeIlha;


    @Column(name="ano")
    private Integer ano;


    /** Início do período dentro deste mês (recortado às fronteiras). */
    @Column(name="data_inicio")
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    /** Jornada diária contratada, em {@code HH:MM} (ex.: "08:00") — é texto, não número. */
    @Column(name="horas_contratado_diario")
    private String horasContratadoDiario;


    @Column(name="horas_contratado_mensal")
    private BigDecimal horasContratadoMensal;


    @Column(name="horas_trabalho")
    private BigDecimal horasTrabalho;


    @Column(name="salario_mensal")
    private BigDecimal salarioMensal;


    // VALOR_HORAS_MENSAL e VALOR_HORAS_DIARIO existiam na versão anterior da vista,
    // que multiplicava o valor por 12. Foram substituídos pela repartição mensal real:
    // valorDiarioUtil / valorDiarioNaoUtil / valorAcumuladoMes / valorPeriodo.


    @Column(name="percentagem")
    private Integer percentagem;


    @Column(name="percentagem_referente")
    private String percentagemReferente;


    @Column(name="pedido_id")
    private Long pedidoId;

  @Column(name="pedido_uuid")
  private UUID pedidoUuid;



  @Column(name="estado")
    private String estado;


    @Column(name="estado_desc")
    private String estadoDesc;


}
