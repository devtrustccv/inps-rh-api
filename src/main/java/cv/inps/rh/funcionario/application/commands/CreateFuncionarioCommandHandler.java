package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.shared.domain.repository.GeografiaRepository;
import cv.inps.rh.parametrizacao.domain.repository.TipoDocumentoRepository;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CreateFuncionarioCommandHandler implements CommandHandler<CreateFuncionarioCommand, ResponseEntity<FuncionarioResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateFuncionarioCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final TipoDocumentoRepository tipoDocumentoRepository;
  private final GeografiaRepository geografiaRepository;

  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;

  private final GeografiaMapper geografiaMapper;
  private final TipoDocumentoMapper tipoDocumentoMapper;

   public CreateFuncionarioCommandHandler(FuncionarioMapper funcionarioMapper, FuncionarioRepository funcionarioRepository, TipoDocumentoRepository tipoDocumentoRepository, GeografiaRepository geografiaRepository, ContactoMapper contactoMapper, EnderecoMapper enderecoMapper, FamiliarMapper familiarMapper, HabilitacaoLiterariaMapper habilitacaoLiterariaMapper, FormacaoFeitaMapper formacaoFeitaMapper, ExperienciaProfissionalMapper experienciaProfissionalMapper, DocumentoMapper documentoMapper, DadosBancariosMapper dadosBancariosMapper, GeografiaMapper geografiaMapper, TipoDocumentoMapper tipoDocumentoMapper) {

     this.funcionarioMapper = funcionarioMapper;
     this.funcionarioRepository = funcionarioRepository;
     this.tipoDocumentoRepository = tipoDocumentoRepository;
     this.geografiaRepository = geografiaRepository;
     this.contactoMapper = contactoMapper;
     this.enderecoMapper = enderecoMapper;
     this.familiarMapper = familiarMapper;
     this.habilitacaoLiterariaMapper = habilitacaoLiterariaMapper;
     this.formacaoFeitaMapper = formacaoFeitaMapper;
     this.experienciaProfissionalMapper = experienciaProfissionalMapper;
     this.documentoMapper = documentoMapper;
     this.dadosBancariosMapper = dadosBancariosMapper;
     this.geografiaMapper = geografiaMapper;
     this.tipoDocumentoMapper = tipoDocumentoMapper;
   }

   @IgrpCommandHandler
   public ResponseEntity<FuncionarioResponseDTO> handle(CreateFuncionarioCommand command) {
     var dto = command.getFuncionariorequest();

     LOGGER.info("Iniciando criação de funcionário: {}", dto);

     /*TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("TipoDocumento não encontrado: " + dto.getTipoDocumentoId()));*/

    /* Geografia localNascimento = geografiaRepository.findById(dto.getNaturalidadeId())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("Geografia não encontrada: " + dto.getNaturalidadeId()));*/

     TipoDocumento tipoDocumento = tipoDocumentoMapper.toDomain(dto.getTipoDocumentoId());

     Geografia localNascimento = geografiaMapper.toDomain(dto.getNaturalidadeId());

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
         1L, // entidadeId (preencher se houver lógica)
         1L  // colaboradorId (preencher se houver lógica)
     );

     var contactos = contactoMapper.toContactosDomain(dto.getContactos());

     var enderecos = enderecoMapper.toEnderecosDomain(dto.getEnderecos());

     var familiares = familiarMapper.toFamiliaresDomain(dto.getFamiliares());

     var habilitacoesLiterarias = habilitacaoLiterariaMapper.toHabilitacoesLiterariasDomain(dto.getHabilitacoesLiterarias());

     var formacoesFeitas = formacaoFeitaMapper.toFormacoesFeitasDomain(dto.getFormacoesFeitas());

     var experienciasProfissionais  = experienciaProfissionalMapper.toExperienciasProfissionaisDomain(dto.getExperienciasProfssionais());

     var documentos = documentoMapper.toDocumentosDomain(dto.getAnexos());

     var dadosBancarios = dadosBancariosMapper.toDadosBancariosDomain(dto.getDadosBancarios());


     funcionario.syncContacts(contactos);
     funcionario.syncEnderecos(enderecos);
     funcionario.syncFamiliares(familiares);
     funcionario.syncHabilitacoes(habilitacoesLiterarias);
     funcionario.syncFormacoes(formacoesFeitas);
     funcionario.syncExperiencias(experienciasProfissionais);
     funcionario.syncDocumentos(documentos);
     funcionario.syncDadosBancarios(dadosBancarios);

     Funcionario saved = funcionarioRepository.save(funcionario);

     LOGGER.info("Funcionário criado com sucesso: {}", saved.getNomeCompleto());

     FuncionarioResponseDTO responseDTO = funcionarioMapper.toDTO(saved);

     return ResponseEntity.ok(responseDTO);

   }

}
