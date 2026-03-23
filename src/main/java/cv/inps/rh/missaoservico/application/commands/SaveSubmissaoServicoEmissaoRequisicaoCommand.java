package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoRequisicaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSubmissaoServicoEmissaoRequisicaoCommand implements Command {

  
  private MissaoEmissaoRequisicaoRequestDTO missaoemissaorequisicaorequest;
  @NotBlank(message = "The field <uui> is required")
  private String uui;

}