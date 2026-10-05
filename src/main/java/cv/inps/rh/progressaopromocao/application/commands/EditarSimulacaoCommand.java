package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.progressaopromocao.application.dto.EditarSimulacaoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarSimulacaoCommand implements Command {

  private EditarSimulacaoDTO editarSimulacaoDTO;
}
