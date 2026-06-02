/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BaixaMedicaReqDTO  {


  private UUID colaborador ;


  private Long tipoLicenca ;


  private Long motivo ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private LocalDate dataInicioFalta ;


  private String observacao ;

  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

}
