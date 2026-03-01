package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.PedidoReforcoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePedidosReforcoCommand implements Command {

  
  private PedidoReforcoRequestDTO pedidoreforcorequest;

}