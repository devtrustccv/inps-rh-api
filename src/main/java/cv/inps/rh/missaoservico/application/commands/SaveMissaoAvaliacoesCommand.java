package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.MissaoAvaliacoesRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Gravação em bloco das avaliações dos prestadores da missão. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMissaoAvaliacoesCommand implements Command {

  private MissaoAvaliacoesRequestDTO missaoavaliacoesrequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
