package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.EquipamentoListRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveEquipamentosLocalTrabalhoCommand implements Command {


  private EquipamentoListRequestDTO equipamentolistrequest;
  @NotBlank(message = "The field <localTrabalhoId> is required")
  private String localTrabalhoId;

}
