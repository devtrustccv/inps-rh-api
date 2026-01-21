package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.MarcarDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarDispensaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DispensaWriteService {


  public Map<String, ?> marcarDispensa(MarcarDispensaCommand command){
    return null;
  }

  public Map<String, ?> validarDispensa(ValidarDispensaCommand command){
    return null;
  }

}
