/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoSubmissaoRequestDTO  {

  private Long paisDestinoId;
  private String descricaoDestino;
  private String ambitoMissao;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private String autorizadoPor;
  private LocalDate dataAutorizacao;

  private List<MissaoColaboradorRequestDTO> colaboradores;

  private List<AnexoReqDTO> documentos = new ArrayList<>();

  private String estado;                  // default: "A"

  private ProcessStepAction processoEtapaAction;
}
