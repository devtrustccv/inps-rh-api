package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaJustificadaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JustificarFaltaWriteService {


  public Map<String, ?> justificarFalta(JustificarFaltaCommand command) {
    return null;
  }

  public Map<String, ?> validarFaltaJustificada(ValidarFaltaJustificadaCommand command) {
    return null;
  }

}
