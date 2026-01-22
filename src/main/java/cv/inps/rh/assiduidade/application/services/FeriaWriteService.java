package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.AlterarPedidoFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.MarcarFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarPedidoFeriaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeriaWriteService {


  public Map<String, ?> marcarFeria(MarcarFeriaCommand command) {
    return null;
  }

  public Map<String, ?> validarFeria(ValidarPedidoFeriaCommand command) {
    return null;
  }

  public Map<String, ?> alterarPedidoFeria(AlterarPedidoFeriaCommand command) {
    return null;
  }

}
