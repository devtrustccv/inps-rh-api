package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.queries.GetFaltaJustificadaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {


  public JustificarFaltaDTO getFaltaJustificada(GetFaltaJustificadaQuery query) {
    return new JustificarFaltaDTO();
  }

}
