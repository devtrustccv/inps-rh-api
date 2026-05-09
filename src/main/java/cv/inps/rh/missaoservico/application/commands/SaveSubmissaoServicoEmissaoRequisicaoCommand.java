package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoRequisicaoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSubmissaoServicoEmissaoRequisicaoCommand implements Command {


  private MissaoEmissaoRequisicaoRequestDTO missaoemissaorequisicaorequest;
  @NotBlank(message = "The field <uui> is required")
  private String uui;

}
