/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ParecerRequestDTO {

  private String responsavel;             // só na Aprovação RH: COORDENADOR_RH | DIRECTOR_RH
  private String parecer;                 // domínio PARECER: FAVORAVEL | DESFAVORAVEL — obrigatório
  private String observacao;              // obrigatória com DESFAVORAVEL
  private ProcessStepAction processoEtapaAction;  // SAVE = rascunho | NEXT = emitir

}
