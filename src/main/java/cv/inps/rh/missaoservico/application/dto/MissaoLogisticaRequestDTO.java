/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoLogisticaRequestDTO  {

  private List<BilhetePassagemRequestDTO> bilhetesPassagem;
  private List<SeguroViagemRequestDTO> segurosViagem;
  private List<AlojamentoRequestDTO> alojamentos;
  private List<AjudaCustoRequestDTO> ajudasCusto;

  private MissaoNotificacaoRequestDTO notificacao;

  private ProcessStepAction processoEtapaAction;
}
