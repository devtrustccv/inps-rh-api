package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncVinculoMovimentosCommand implements Command {

  @NotBlank(message = "The field <vinculoId> is required")
  private String vinculoId;

  @Valid
  @NotNull(message = "The field <movimentos> is required")
  private List<VinculoMovimentoRequestDTO> movimentos;

}
