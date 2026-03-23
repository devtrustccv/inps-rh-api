package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexarOrdemServicoCommand implements Command {


  private AnexarOrdemServicoRequestDTO anexarordemservicorequest;

}
