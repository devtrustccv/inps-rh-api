package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NovaMobilidadeService {


  public MobilidadeDTO save(SaveMobilidadeCommand command) {

    return null;
  }
}
