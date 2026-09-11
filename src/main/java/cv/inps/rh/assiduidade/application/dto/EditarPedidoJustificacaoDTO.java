/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Corpo do {@code PUT falta/justificar/pedido/{pedidoUuid}}.
 *
 * <p>Não tem {@code itensFalta} de propósito: o editar mexe no <b>pedido</b>, não na composição
 * dele. Os dias que o compõem descobrem-se pelo próprio pedido, tal como no Eliminar, e
 * mantêm-se todos — a spec (:658) só dá ao Eliminar o efeito de pôr faltas em {@code 'E'}.
 *
 * <p>Enquanto o corpo era partilhado com o {@code POST} de justificar, um payload que trouxesse
 * apenas alguns dias apagava silenciosamente os restantes e revertia o dinheiro deles. Com um
 * DTO próprio isso deixa de ser exprimível.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EditarPedidoJustificacaoDTO  {


  /** "Motivo" — aplicado a todas as faltas do pedido (RH_T_FALTA.DESCRICAO_MOTIVO). */
  private String motivo ;


  /** "Com Justificativo?" (DOMAIN SIM_NAO) — RH_T_FALTA.FLG_JUSTIFICATIVO. */
  private String comJustificativo ;


  private Long tipoJustificacao ;


  /** "Deduzir Falta Em" — DOMAIN TP_DESCONTO_FALTA: FERIAS | DISPENSA. */
  private String deduzirFaltaEm ;


  private String parecerResponsavel ;


  private Long responsavelId ;


  private String obsResponsavel ;


  /**
   * Anexos do pedido. Semântica dos arrays da casa: {@code null} preserva, item sem id cria,
   * item existente omitido fica {@code 'E'}.
   *
   * <p>Sem valor inicial de propósito: com {@code new ArrayList<>()} o campo omitido chegava como
   * lista vazia e apagava os anexos todos, em vez de os preservar.
   */
  @Valid
  private List<AnexoReqDTO> documentos ;

}
