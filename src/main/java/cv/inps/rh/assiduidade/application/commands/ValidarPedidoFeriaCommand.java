package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarPedidoFeriaCommand implements Command {


  private PedidoFeriaReqDTO pedidoferiareq;
  @NotBlank(message = "The field <pedidoId> is required")
  private String pedidoId;

}
