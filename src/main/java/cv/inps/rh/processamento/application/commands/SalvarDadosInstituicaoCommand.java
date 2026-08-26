package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalvarDadosInstituicaoCommand implements Command {

  @Valid
  @NotNull(message = "The field <dadosInstituicaoRequest> is required")
  private DadosInstituicaoRequestDTO dadosInstituicaoRequest;
}
