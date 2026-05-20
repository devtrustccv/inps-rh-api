package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.AtivarInativarSubsidioNatalDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtivarInactivarSubsidioNatalCommand implements Command {


  private AtivarInativarSubsidioNatalDTO ativarinativarsubsidionatal;

}
