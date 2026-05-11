package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexarDocOrdemServicoCommand implements Command {

  @NotBlank(message = "The field <osUuid> is required")
  private String osUuid;

  private AnexoReqDTO anexo;

}
