package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.InativarAtivarColaboradorCommand;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InativarAtivarColaborarService {


  public AtivarInativarColaboradorDTO execute(InativarAtivarColaboradorCommand command) {
  }
}
