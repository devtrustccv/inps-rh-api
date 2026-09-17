package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.AvaliacaoPrestadorRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAvaliacaoPrestadorCommand implements Command {

  private AvaliacaoPrestadorRequestDTO avaliacaoprestadorrequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;
  @NotBlank(message = "The field <tipoProcesso> is required")
  private String tipoProcesso;
  @NotBlank(message = "The field <missaoPrestUuid> is required")
  private String missaoPrestUuid;

}
