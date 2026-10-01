/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.progressaopromocao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProgressaoPromocaoRowDTO  {



  // Id numérico da entidade (RH_T_SIM/VAL/EVOLUCAO_CARREIRA.ID) — é o que
  // validar/confirmar/enviar-historico/extrair-ordem-servico esperam
  // (HistoricoIdsDTO.ids: List<Long>, ConfirmarProgressaoCommand.validacaoId
  // via Long.valueOf). "id" abaixo continua a ser o uuid, usado só por
  // Anexar Ordem de Serviço (AnexarOrdemServicoRequestDTO.evolucaoCarreiraUuid).
  private Long rowId ;



  private String id ;


  private String progressaoPromocao ;


  private LocalDate dataReferente ;


  private String nomeColaborador ;


  private String carreira ;


  private String cargo ;


  private String escalaoDe ;


  private String escalaoPara ;


  private String observacao ;


  private Long avaliacaoMedia ;


  private String historico ;

}
