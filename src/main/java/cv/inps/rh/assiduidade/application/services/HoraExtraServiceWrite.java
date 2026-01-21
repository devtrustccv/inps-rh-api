package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.MarcarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarHoraExtraCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HoraExtraServiceWrite {


  public Map<String, ?> marcarHoraExtra(MarcarHoraExtraCommand command ) {
    return null;
  }

  public Map<String, ?> validarHoraExtra(ValidarHoraExtraCommand command ) {
    return null;
  }



}
