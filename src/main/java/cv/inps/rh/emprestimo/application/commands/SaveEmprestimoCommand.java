package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveEmprestimoCommand implements Command {


  private PedidoEmprestimoDTO pedidoemprestimo;

}
