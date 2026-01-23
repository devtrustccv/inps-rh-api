package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarPedidoFeriaCommand implements Command {

  
  private PedidoFeriaAlterarReqDTO pedidoferiaalterarreq;
  @NotBlank(message = "The field <feriaId> is required")
  private String feriaId;

}