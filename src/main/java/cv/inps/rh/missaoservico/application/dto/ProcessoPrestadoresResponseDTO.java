/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoPrestadoresResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private MissaoProcessoResponseDTO processo;
  private List<ProcessoPrestadorResponseDTO> prestadores;
  private MissaoNotificacaoResponseDTO notificacao;   // pré-preenchida com o template
  private String executadoPor;
  private LocalDate dataExecucao;

}
