/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Etapas sem formulário (ex.: Autorização): só a acção. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoEtapaActionRequestDTO {

  private ProcessStepAction processoEtapaAction;  // SAVE | NEXT

}
