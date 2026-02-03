package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GerarPlanoFinanceiroCommand implements Command {

  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}
