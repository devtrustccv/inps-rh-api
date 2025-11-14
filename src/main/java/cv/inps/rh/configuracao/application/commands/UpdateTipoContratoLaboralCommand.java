package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTipoContratoLaboralCommand implements Command {


  private TipoContratoLaboralRequestDTO tipocontratolaboralrequest;
  @NotBlank(message = "The field <tipoContratoLaboralId> is required")
  private String tipoContratoLaboralId;

}
