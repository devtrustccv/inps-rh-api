package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarFeriaCommand implements Command {

  
  private PedidoFeriaReqDTO pedidoferiareq;

}