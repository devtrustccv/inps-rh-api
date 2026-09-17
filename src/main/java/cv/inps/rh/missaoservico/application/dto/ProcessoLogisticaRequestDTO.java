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
public class ProcessoLogisticaRequestDTO {

  // Só a secção do tipo do processo é aceite. A lista é a secção completa; null = não mexer.
  private List<BilhetePassagemRequestDTO> bilhetesPassagem;   // processo BILHETE_PASSAGEM
  private List<SeguroViagemRequestDTO> segurosViagem;         // processo SEGURO_VIAGEM
  private List<AlojamentoRequestDTO> alojamentos;             // processo ALOJAMENTO
  private List<AjudaCustoRequestDTO> ajudasCusto;             // processo AJUDA_CUSTO
  private MissaoNotificacaoRequestDTO notificacao;            // aviso aos colaboradores no NEXT (editável)
  private ProcessStepAction processoEtapaAction;              // SAVE | NEXT

}
