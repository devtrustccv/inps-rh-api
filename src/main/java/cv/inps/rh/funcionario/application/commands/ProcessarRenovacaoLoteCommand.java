package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.RenovarLoteReqDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessarRenovacaoLoteCommand implements Command {


  @Valid
  @NotNull(message = "The field <renovarLote> is required")
  private RenovarLoteReqDTO renovarLote;

}
