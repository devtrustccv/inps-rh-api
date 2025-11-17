package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.parametrizacao.domain.repository.TipoDocumentoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.*;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import cv.inps.rh.shared.domain.repository.GeografiaRepository;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

  private final ParamCargoMapper paramCargoMapper;
  private final ParamContratoMapper paramContratoMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final SecaoMapper secaoMapper;
  private final ParamCarreiraMapper paramCarreiraMapper;
  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamLocalTrabMapper paramLocalTrabMapper;

  private final TipoMovimentoMapper tipoMovimentoMapper;


   public CreateFuncionarioCommandHandler(FuncionarioMapper funcionarioMapper, FuncionarioRepository funcionarioRepository, TipoDocumentoRepository tipoDocumentoRepository, GeografiaRepository geografiaRepository, ContactoMapper contactoMapper, EnderecoMapper enderecoMapper, FamiliarMapper familiarMapper, HabilitacaoLiterariaMapper habilitacaoLiterariaMapper, FormacaoFeitaMapper formacaoFeitaMapper, ExperienciaProfissionalMapper experienciaProfissionalMapper, DocumentoMapper documentoMapper, DadosBancariosMapper dadosBancariosMapper, GeografiaMapper geografiaMapper, TipoDocumentoMapper tipoDocumentoMapper, ParamCargoMapper paramCargoMapper, ParamContratoMapper paramContratoMapper, InstituicaoMapper instituicaoMapper, SecaoMapper secaoMapper, ParamCarreiraMapper paramCarreiraMapper, ParamCategoriaMapper paramCategoriaMapper, ParamEscalaoMapper paramEscalaoMapper, ParamVinculoMapper paramVinculoMapper, ParamLocalTrabMapper paramLocalTrabMapper, TipoMovimentoMapper tipoMovimentoMapper) {

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
     this.paramCargoMapper = paramCargoMapper;
     this.paramContratoMapper = paramContratoMapper;
     this.instituicaoMapper = instituicaoMapper;
     this.secaoMapper = secaoMapper;
     this.paramCarreiraMapper = paramCarreiraMapper;
     this.paramCategoriaMapper = paramCategoriaMapper;
     this.paramEscalaoMapper = paramEscalaoMapper;
     this.paramVinculoMapper = paramVinculoMapper;
     this.paramLocalTrabMapper = paramLocalTrabMapper;
     this.tipoMovimentoMapper = tipoMovimentoMapper;
   }

   @IgrpCommandHandler
   public ResponseEntity<FuncionarioResponseDTO> handle(CreateFuncionarioCommand command) {
     var dto = command.getFuncionariorequest();

     LOGGER.info("Iniciando criação de funcionário: {}", dto);

     /*TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("TipoDocumento não encontrado: " + dto.getTipoDocumentoId()));*/

    /* Geografia localNascimento = geografiaRepository.findById(dto.getNaturalidadeId())
         .orElseThrow(() -> IgrpResponseStatusException.badRequest("Geografia não encontrada: " + dto.getNaturalidadeId()));*/

     var dadosPessoais = dto.getDadosPessoais();
     TipoDocumento tipoDocumento = tipoDocumentoMapper.toDomain(dadosPessoais.getTipoDocumentoId());

     Geografia localNascimento = geografiaMapper.toDomain(dadosPessoais.getNaturalidadeId());

     var endereco = enderecoMapper.toDomain(dadosPessoais.getEndereco());


     Funcionario funcionario = Funcionario.create(
         tipoDocumento,
         dadosPessoais.getNumDocumento(),
         dadosPessoais.getNome(),
         dadosPessoais.getUrlFoto(),
         dadosPessoais.getDataNascimento(),
         dadosPessoais.getGenero(),
         dadosPessoais.getNomeMae(),
         dadosPessoais.getNomePai(),
         dadosPessoais.getEstadoCivil(),
         dadosPessoais.getNacionalidade(),
         localNascimento,
         dadosPessoais.getNif(),
         dadosPessoais.getNumSegurado(),
         1L, // entidadeId (preencher se houver lógica)
         1L , // colaboradorId (preencher se houver lógica)
         endereco
     );

     var contactos = contactoMapper.toContactosDomain(dto.getDadosPessoais().getContactos());

     var familiares = familiarMapper.toFamiliaresDomain(dto.getFamiliares());

     var dadosAcademicosProf = dto.getDadosAcademicosProf();
     var habilitacoesLiterarias = habilitacaoLiterariaMapper.toHabilitacoesLiterariasDomain(dadosAcademicosProf.getHabilitacoesLiterarias());
     var formacoesFeitas = formacaoFeitaMapper.toFormacoesFeitasDomain(dadosAcademicosProf.getFormacoesFeitas());
     var experienciasProfissionais  = experienciaProfissionalMapper
         .toExperienciasProfissionaisDomain(dadosAcademicosProf.getExperienciasProfssionais());

     var documentos = documentoMapper.toDocumentosDomain(dto.getAnexos());
     var dadosBancarios = dadosBancariosMapper.toDadosBancariosDomain(dto.getDadosBancarios());


     funcionario.syncContacts(contactos);
     funcionario.syncFamiliares(familiares);
     funcionario.syncHabilitacoes(habilitacoesLiterarias);
     funcionario.syncFormacoes(formacoesFeitas);
     funcionario.syncExperiencias(experienciasProfissionais);
     funcionario.syncDocumentos(documentos);
     funcionario.syncDadosBancarios(dadosBancarios);


     var dadosContratuais = dto.getDadosContratuais();
     var tipoContrato = paramContratoMapper.toDomain(dadosContratuais.getTipoContratoId());
     var cargoPosicao = paramCargoMapper.toDomain(dadosContratuais.getCargoPosicaoId());
     var direcao = instituicaoMapper.toDomain(dadosContratuais.getDirecaoId());
     var seccao = secaoMapper.toDomain(dadosContratuais.getSeccaoId());
     var carreira  = paramCarreiraMapper.toDomain(dadosContratuais.getCarreiraId());
     var categoria = paramCategoriaMapper.toDomain(dadosContratuais.getCategoriaId());
     var escalao = paramEscalaoMapper.toDomain(dadosContratuais.getEscalaoReferenciaId());
     var vinculo = paramVinculoMapper.toDomain(dadosContratuais.getTipoVinculoLaboralId());
     var pais = geografiaMapper.toDomain(dadosContratuais.getPaisId());
     var ilha = geografiaMapper.toDomain(dadosContratuais.getIlhaId());
     var localTrabalho = paramLocalTrabMapper.toDomain(dadosContratuais.getLocalTrabalhoId());


     List<DefPagamento> defPagamentos =dadosContratuais.getEncargosDescontos().stream()
         .map(e -> {
           TipoMovimento tipoMov = tipoMovimentoMapper.toDomain(e.getTipoEncargoId());
           return DefPagamento.create(
               e.getId(),           // id inicial, será persistido depois
               e.getValor(),
               tipoMov,
               e.getDataInicio(),
               e.getDataFim(),
               e.getObservacoes()
           );
         })
         .toList();

     List<DefinicaoRemuneracao> defRemuneracoes = dadosContratuais.getSubsidios().stream()
         .map(s -> {
           TipoMovimento tipoMov = tipoMovimentoMapper.toDomain(s.getTipoSubsidioId());
           return DefinicaoRemuneracao.create(
               s.getId(),
               s.getPercentagem(),
               s.getValor(),
               s.getObservacoes(),
               tipoMov
           );
         })
         .toList();

    funcionario.adicionarDadosContratuais(tipoContrato,
        cargoPosicao,
        direcao,
        seccao,
        dadosContratuais.getCentroCusto(),
         carreira,
         categoria,
        escalao,
        vinculo,
        dadosContratuais.getRegimeTrabalho(),
        dadosContratuais.getSalario(),
        dadosContratuais.getMoeda(),
        dadosContratuais.getDataInicio(),
        dadosContratuais.getDataFim(),
        dadosContratuais.getDuracaoMeses(),
        localTrabalho,
         pais,
         ilha,
        defPagamentos,
        defRemuneracoes,
        "REGISTO_COLABORADOR");




     Funcionario saved = funcionarioRepository.save(funcionario);

     LOGGER.info("Funcionário criado com sucesso: {}", saved.getNomeCompleto());

     var responseDTO = funcionarioMapper.toResponseDTO(saved);

     return ResponseEntity.ok(responseDTO);

   }

}
