/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MobilidadeDTO  {



  private EstadoValidacao validar ;


  private String tipoMobilidade ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  // Estado do registo (só leitura). O ecrã precisa dele para rotular o que recebe: um registo por
  // validar (P/C) mostra uma proposta, um consolidado (A/I) mostra a posição já aplicada. Sem isto o
  // frontend só conhecia o estado pela listagem.
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String estado ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String estadoDesc ;


  // Campos SEM sufixo = a situação de onde se parte. Num registo por validar (P/C) são os valores do
  // pai (RH_T_MOBILIDADE.MOB_ID), ou seja onde o colaborador está enquanto a mobilidade não é
  // aplicada; nos restantes estados são os valores do próprio registo pedido.
  //
  // Campos com sufixo Destino = para onde vai. Só vêm preenchidos enquanto há movimento por
  // concretizar (P/C); num registo consolidado (A/I) ficam null, porque já não há para onde ir.
  //
  // Todos os *Desc e os ids do lado sem sufixo são só de leitura: o servidor devolve-os, nunca os lê
  // do corpo do pedido (o readOnly tira-os do schema de request no Swagger). O que o formulário
  // reenvia no PUT/POST é o lado Destino. Substituir por MobilidadeReqDTO/MobilidadeRespDTO quando o
  // fluxo de mobilidade estabilizar — é a convenção do resto do módulo.
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String direcaoDesc ;


  // DirecaoEntity não tem uuid — o identificador é o Long id, igual ao usado em direcaoDestino e nos
  // selects do formulário. O mesmo vale para seccaoId e localTrabalhoId.
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long direcaoId ;


  private Long direcaoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String direcaoDestinoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String seccaoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long seccaoId ;


  private Long seccaoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String seccaoDestinoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String localTrabalhoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long localTrabalhoId ;


  private Long localTrabalhoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String localTrabalhoDestinoDesc ;

  private String tipoOrdemServico ;

}
