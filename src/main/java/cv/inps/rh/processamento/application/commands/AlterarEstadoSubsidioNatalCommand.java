package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.SubsidioNatalIdsRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarEstadoSubsidioNatalCommand implements Command {

  @Valid
  @NotNull(message = "The field <request> is required")
  private SubsidioNatalIdsRequestDTO request;
}
