package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtivarInactivarSubsidioNatalCommand implements Command {

  
  private SubsidioResponseNatalDTO subsidioresponsenatal;
  @NotNull(message = "The field <subsidioId> is required")
  private Long subsidioId;
  @NotNull(message = "The field <ano> is required")
  private Long ano;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;
  @NotBlank(message = "The field <status> is required")
  private String status;

}