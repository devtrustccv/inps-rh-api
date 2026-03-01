package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveConfiguracaoInfoEmprestimoCommand implements Command {


  private List<InformacaoEmprestimoRequestDTO> informacaoemprestimorequest;

}
