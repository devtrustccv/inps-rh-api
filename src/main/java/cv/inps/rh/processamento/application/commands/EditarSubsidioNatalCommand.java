package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.SubsidioNatalEditarRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarSubsidioNatalCommand implements Command {

  @NotNull(message = "The field <subsidioId> is required")
  private Long subsidioId;

  @NotNull(message = "The field <request> is required")
  private SubsidioNatalEditarRequestDTO request;
}
