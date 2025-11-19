package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ValidarContratoService {

  private final ContratoEntityRepository contratoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public ValidarContratoService(ContratoEntityRepository contratoEntityRepository,
                                FuncionarioEntityRepository funcionarioEntityRepository) {
    this.contratoEntityRepository = contratoEntityRepository;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
  }

  public ResponseEntity<DadosContratuaisRespDTO> validarContrato(ValidarContratoCommand command) {
     return null;
  }
}
