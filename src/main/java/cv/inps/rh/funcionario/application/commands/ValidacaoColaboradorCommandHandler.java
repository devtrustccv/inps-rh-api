package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Component
public class ValidacaoColaboradorCommandHandler implements CommandHandler<ValidacaoColaboradorCommand, ResponseEntity<AtivarInativarColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoColaboradorCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;

  public ValidacaoColaboradorCommandHandler(FuncionarioRepository funcionarioRepository) {
    this.funcionarioRepository = funcionarioRepository;
  }

  @IgrpCommandHandler
  public ResponseEntity<AtivarInativarColaboradorDTO> handle(ValidacaoColaboradorCommand command) {
     var dto = command.getAtivarinativarcolaborador();

     var id = IdentificadorUnico.from(command.getId());

     var funcionario = funcionarioRepository.findById(id).orElseThrow(
         () -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId())
     );

     TiposRelacionamento atual =funcionario.getTipoRelacionamentoAtual();

     if (atual == null) {
       throw IgrpResponseStatusException.badRequest("tiposRelacionamento atual nao encontrado para funcionario");
     }

     if (StringUtils.hasText(dto.getValidar())) {
       var estado = Estado.fromCodeOrThrow(dto.getValidar());

     }



     funcionarioRepository.save(funcionario);
     return ResponseEntity.ok(dto);
  }

}
