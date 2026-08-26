package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.DadosApoliceRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalvarDadosApoliceCommand implements Command {

  @Valid
  @NotNull(message = "The field <dadosApoliceRequest> is required")
  private DadosApoliceRequestDTO dadosApoliceRequest;
}
