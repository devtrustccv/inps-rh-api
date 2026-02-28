package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.AnaliseFinanceiroRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveDecisaoAnaliseFinanceiraCommand implements Command {

  
  private AnaliseFinanceiroRequestDTO analisefinanceirorequest;
  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}