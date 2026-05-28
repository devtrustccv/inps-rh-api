package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.BaixaMedicaReqDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarBaixaMedicaCommand implements Command {

  private String pedidoId;
  private EstadoValidacao validar;
  private BaixaMedicaReqDTO ajuste;

}
