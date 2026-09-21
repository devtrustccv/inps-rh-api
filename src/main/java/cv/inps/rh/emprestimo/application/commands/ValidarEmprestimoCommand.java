package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.emprestimo.application.dto.ValidarEmprestimoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarEmprestimoCommand implements Command {

  private String emprestimoId;

  private ValidarEmprestimoRequestDTO validarEmprestimoRequest;

}
