package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.emprestimo.application.dto.ChangeStatusData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MudarEstadoEmprestimoCommand implements Command {

  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

  private ChangeStatusData data;

}
