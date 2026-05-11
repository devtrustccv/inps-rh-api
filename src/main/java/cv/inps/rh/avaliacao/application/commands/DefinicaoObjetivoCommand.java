package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefinicaoObjetivoCommand implements Command {


  private DefinicaoObjectivoDTO definicaoobjectivo;

}
