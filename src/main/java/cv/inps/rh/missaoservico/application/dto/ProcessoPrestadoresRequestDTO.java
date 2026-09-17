/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoPrestadoresRequestDTO {

  private List<UUID> prestadores;                 // uuid dos prestadores parametrizados (1 a 3); lista completa
  private MissaoNotificacaoRequestDTO notificacao; // assunto/corpo editados; vazio = template
  private ProcessStepAction processoEtapaAction;  // SAVE | NEXT

}
