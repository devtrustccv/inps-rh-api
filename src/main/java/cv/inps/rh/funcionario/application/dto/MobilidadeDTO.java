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


  // Campos só de leitura: o servidor devolve-os, nunca os lê do corpo do pedido. O readOnly tira-os
  // do schema de request no Swagger. Substituir por MobilidadeReqDTO/MobilidadeRespDTO quando o
  // fluxo de mobilidade estabilizar — é a convenção do resto do módulo.
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String direcaoOrigemDesc ;


  // Id da direcção de origem (só leitura). DirecaoEntity não tem uuid — o identificador é o Long id,
  // igual ao usado em direcaoDestino e nos selects do formulário.
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long direcaoOrigemId ;


  private Long direcaoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String direcaoDestinoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String seccaoOrigemDesc ;


  private Long seccaoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String seccaoDestinoDesc ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String localTrabalhoOrigemDesc ;


  private Long localTrabalhoDestino ;


  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String localTrabalhoDestinoDesc ;

  private String tipoOrdemServico ;

}
