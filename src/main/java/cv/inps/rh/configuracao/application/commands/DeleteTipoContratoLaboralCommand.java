package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTipoContratoLaboralCommand implements Command {

  @NotBlank(message = "The field <tipoContratoLaboralId> is required")
  private String tipoContratoLaboralId;

}
