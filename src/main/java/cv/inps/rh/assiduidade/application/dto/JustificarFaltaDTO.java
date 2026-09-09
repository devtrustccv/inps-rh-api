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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class JustificarFaltaDTO  {


  private UUID colaboradorId ;


  private String nomeColaborador ;

  /**
   * UUID do pedido de justificação a que este conjunto de faltas pertence. Só de resposta,
   * e só preenchido na leitura por pedido: é a chave com que o ecrã chama o Editar/Eliminar
   * do grupo (spec 09/09: "agrupados por RH_T_FALTA.PEDIDO_ID").
   */
  private UUID pedidoId ;

  @Valid
  private List<FaltaItemDTO> itensFalta = new ArrayList<>();


  private String parecerResponsavel ;


  private Long responsavelId ;


  private String obsResponsavel ;



  private Long tipoJustificacao ;

  /**
   * "Motivo" do bloco "Justificar Faltas Selecionadas" — um único texto aplicado a todas as
   * faltas seleccionadas (o ecrã tem uma só caixa, não uma por dia). Guardado em
   * RH_T_FALTA.DESCRICAO_MOTIVO, igual em todas as faltas do pedido.
   */
  private String motivo ;

  /**
   * "Com Justificativo?" (DOMAIN SIM_NAO) — o radio único do cabeçalho do formulário, que
   * comanda a visibilidade dos restantes blocos. Guardado em RH_T_FALTA.FLG_JUSTIFICATIVO,
   * igual em todas as faltas do pedido; devolvido na leitura para o Editar repor o radio.
   */
  private String comJustificativo ;

  /** "Deduzir Falta Em" — DOMAIN TP_DESCONTO_FALTA: FERIAS | DISPENSA. */
  private String deduzirFaltaEm ;

  /** Só de resposta: valor da falta por dia. */
  private BigDecimal valorDiario ;

  /** Só de resposta: soma dos dias justificados. */
  private BigDecimal valorTotal ;

  /**
   * Documentos comprovativos do bloco "Justificar Faltas Selecionadas" — o formulário
   * permite anexar vários ("Adicionar outro documento"). Aplicam-se a todas as faltas
   * seleccionadas; {@code FaltaItemDTO.documento} continua a servir o anexo de um dia
   * específico.
   */
  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();


  private EstadoValidacao validar ;


  private Integer ano ;


  private Integer mes ;

  private String tipoOrdemServico ;

}
