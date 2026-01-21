package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.funcionario.application.commands.ValidarCarreiraCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FaltaServiceWrite {


  public Map<String, ?> marcarFalta(MarcarFaltaCommand command) {

    return null;
  }


  public Map<String, ?> validarFalta(ValidarFaltaCommand command) {

    return null;
  }
}
