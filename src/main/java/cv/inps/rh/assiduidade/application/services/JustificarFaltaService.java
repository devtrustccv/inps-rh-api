package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JustificarFaltaService {


  public Map<String, ?> justificarFalta(JustificarFaltaCommand command) {
    return null;
  }

}
