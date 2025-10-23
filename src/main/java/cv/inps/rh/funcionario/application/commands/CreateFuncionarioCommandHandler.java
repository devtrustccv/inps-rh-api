package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.TipoDocumento;
import cv.inps.rh.shared.domain.repository.GeografiaRepository;
import cv.inps.rh.shared.domain.repository.TipoDocumentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<FuncionarioResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final TipoDocumentoRepository tipoDocumentoRepository;
  private final GeografiaRepository geografiaRepository;

   public CreateFuncionarioCommandHandler(FuncionarioMapper funcionarioMapper, FuncionarioRepository funcionarioRepository, TipoDocumentoRepository tipoDocumentoRepository, GeografiaRepository geografiaRepository) {

     this.funcionarioMapper = funcionarioMapper;
     this.funcionarioRepository = funcionarioRepository;
     this.tipoDocumentoRepository = tipoDocumentoRepository;
     this.geografiaRepository = geografiaRepository;
   }

   @IgrpCommandHandler
   public ResponseEntity<FuncionarioResponseDTO> handle(CreateFuncionarioCommand command) {
     var dto = command.getFuncionariorequest();

     LOGGER.info("Iniciando criação de funcionário: {}", dto);

     TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId().longValue())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("TipoDocumento não encontrado: " + dto.getTipoDocumentoId()));

     Geografia localNascimento = geografiaRepository.findById(dto.getNaturalidadeId().longValue())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("Geografia não encontrada: " + dto.getNaturalidadeId()));


     Funcionario funcionario = Funcionario.create(
         tipoDocumento,
         dto.getNumDocumento(),
         dto.getNome(),
         dto.getUrlFoto(),
         dto.getDataNascimento(),
         dto.getGenero(),
         dto.getNomeMae(),
         dto.getNomePai(),
         dto.getEstadoCivil(),
         dto.getNacionalidade(),
         localNascimento,
         dto.getNif(),
         dto.getNumSegurado(),
         null, // entidadeId (preencher se houver lógica)
         null  // colaboradorId (preencher se houver lógica)
     );

     Funcionario saved = funcionarioRepository.save(funcionario);

     LOGGER.info("Funcionário criado com sucesso: {}", saved.getNomeCompleto());

     FuncionarioResponseDTO responseDTO = funcionarioMapper.toDTO(saved);

     return ResponseEntity.ok(responseDTO);

   }

}
