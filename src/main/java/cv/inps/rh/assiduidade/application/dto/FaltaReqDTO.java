/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FaltaReqDTO  {



  private UUID colaboradorId ;


  private String colaboradorNome ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer totalDias ;


  private String totalDeHorasAusentes ;


  /**
   * "Com Justificativo?" do formulário (DOMAIN SIM_NAO). Faz duas coisas: com "SIM" abre os
   * blocos Motivo/Tipo Falta/Anexos/Parecer e cria pedido + RH_T_FALTA; com "NÃO" só se regista
   * a síntese diária e o dia fica na lista por justificar. Gravado em
   * RH_T_FALTA.FLG_JUSTIFICATIVO, o mesmo campo que o ecrã de Justificar Falta preenche.
   */
  private String justificar ;


  /**
   * "Motivo Ausencia" do formulario, gravado em RH_T_FALTA.DESCRICAO_MOTIVO.
   *
   * <p>Chamava-se {@code motivoAusencia} e era o unico sitio da aplicacao com esse nome: o
   * Justificar Falta, a dispensa e as ferias usam todos {@code motivo} para a mesma coluna.
   * Dois nomes para o mesmo campo faziam com que um payload com o nome do outro ecra passasse
   * em silencio -- 200, e o motivo perdido. Harmonizado a 11/09.
   */
  private String motivo ;


  private String parecer ;


  private UUID responsavel ;

  private String responsavelNome ;


  private String observacao ;


  private EstadoValidacao validar ;




  private Long tipoJustificacao ;

  /** "Deduzir Falta Em" — DOMAIN TP_DESCONTO_FALTA: FERIAS | DISPENSA. */
  private String deduzirFaltaEm ;

  /** Só de resposta: valor da falta por dia. */
  private BigDecimal valorDiario ;

  /** Só de resposta: valorDiario x totalDias. */
  private BigDecimal valorTotal ;


  /**
   * Quanto sai do vencimento: soma de RH_T_FALTA.VALOR_DESCONTO das faltas activas (o liquido
   * que o processamento salarial le). Zero enquanto o pedido nao for despachado.
   */
  private BigDecimal valorDescontado ;


  /** A parte que o saldo (ferias/dispensa) cobriu: valorTotal - valorDescontado. */
  private BigDecimal valorCoberto ;

  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

  private String tipoOrdemServico ;

}
