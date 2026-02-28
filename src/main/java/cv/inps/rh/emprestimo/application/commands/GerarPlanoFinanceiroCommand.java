package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GerarPlanoFinanceiroCommand implements Command {

  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}
