package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveConfiguracaoInfoEmprestimoCommand implements Command {

  
  private List<InformacaoEmprestimoRequestDTO> informacaoemprestimorequest;

}