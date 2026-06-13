package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarVinculoMovimentoCommand implements Command {

  @NotNull(message = "The field <vinculoId> is required")
  private Long vinculoId;

  @NotNull(message = "The field <id> is required")
  private Long id;

  @Valid
  @NotNull(message = "The field <dto> is required")
  private VinculoMovimentoRequestDTO dto;

}
