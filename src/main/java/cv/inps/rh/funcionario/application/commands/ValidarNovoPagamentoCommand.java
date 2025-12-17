package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.ValidarPagamentoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarNovoPagamentoCommand implements Command {


  private ValidarPagamentoRequestDTO validarpagamentorequest;

}
