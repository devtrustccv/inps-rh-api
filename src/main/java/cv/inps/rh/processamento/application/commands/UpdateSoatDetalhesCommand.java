package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.UpdateDetalheSoatRequestDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSoatDetalhesCommand implements Command {

  @NotEmpty(message = "The field <detalhes> can not be empty")
  @NotNull(message = "The field <detalhes> is required")
  private List<UpdateDetalheSoatRequestDTO> detalhes;

}
