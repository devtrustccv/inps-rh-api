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
public class ValidarPedidoFeriaCommand implements Command {

  
  private PedidoFeriaReqDTO pedidoferiareq;
  @NotBlank(message = "The field <pedidoId> is required")
  private String pedidoId;

}