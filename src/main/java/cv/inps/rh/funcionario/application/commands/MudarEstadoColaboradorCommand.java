package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MudarEstadoColaboradorCommand implements Command {


  private AtivarInativarColaboradorDTO ativarinativarcolaborador;

}
