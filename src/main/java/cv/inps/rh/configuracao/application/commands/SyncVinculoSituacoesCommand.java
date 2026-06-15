package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.VinculoSituacaoLaboralRequestDTO;
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
public class SyncVinculoSituacoesCommand implements Command {

  @NotBlank(message = "The field <vinculoUuid> is required")
  private String vinculoUuid;

  @Valid
  @NotNull(message = "The field <situacoes> is required")
  private List<VinculoSituacaoLaboralRequestDTO> situacoes;

}
