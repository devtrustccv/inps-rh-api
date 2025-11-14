package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTipoContratoLaboralCommand implements Command {


  private TipoContratoLaboralRequestDTO tipocontratolaboralrequest;

}
