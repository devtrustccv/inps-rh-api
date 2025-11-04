package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.parametrizacao.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component
public class ValidarRegistoColaboradorCommandHandler implements CommandHandler<ValidarRegistoColaboradorCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRegistoColaboradorCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final GeografiaMapper geografiaMapper;

  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;

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

   public ValidarRegistoColaboradorCommandHandler(FuncionarioRepository funcionarioRepository, TipoDocumentoMapper tipoDocumentoMapper, GeografiaMapper geografiaMapper, ContactoMapper contactoMapper, EnderecoMapper enderecoMapper, FamiliarMapper familiarMapper, HabilitacaoLiterariaMapper habilitacaoLiterariaMapper, FormacaoFeitaMapper formacaoFeitaMapper, ExperienciaProfissionalMapper experienciaProfissionalMapper, DocumentoMapper documentoMapper, DadosBancariosMapper dadosBancariosMapper, ParamCargoMapper paramCargoMapper, ParamContratoMapper paramContratoMapper, InstituicaoMapper instituicaoMapper, SecaoMapper secaoMapper, ParamCarreiraMapper paramCarreiraMapper, ParamCategoriaMapper paramCategoriaMapper, ParamEscalaoMapper paramEscalaoMapper, ParamVinculoMapper paramVinculoMapper, ParamLocalTrabMapper paramLocalTrabMapper, TipoMovimentoMapper tipoMovimentoMapper) {

     this.funcionarioRepository = funcionarioRepository;
     this.tipoDocumentoMapper = tipoDocumentoMapper;
     this.geografiaMapper = geografiaMapper;
     this.contactoMapper = contactoMapper;
     this.enderecoMapper = enderecoMapper;
     this.familiarMapper = familiarMapper;
     this.habilitacaoLiterariaMapper = habilitacaoLiterariaMapper;
     this.formacaoFeitaMapper = formacaoFeitaMapper;
     this.experienciaProfissionalMapper = experienciaProfissionalMapper;
     this.documentoMapper = documentoMapper;
     this.dadosBancariosMapper = dadosBancariosMapper;
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
   public ResponseEntity<Map<String, ?>> handle(ValidarRegistoColaboradorCommand command) {

     var dto = command.getFuncionariorequest();
     LOGGER.info("Iniciando atualização de funcionário: {}", dto);

     var funcionario = funcionarioRepository.findById(command.getId())
         .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId()));

      var validacao = dto.getValidar();

       TipoDocumento tipoDocumento = tipoDocumentoMapper.toDomain(dto.getTipoDocumentoId());
       Geografia localNascimento = geografiaMapper.toDomain(dto.getNaturalidadeId());

       funcionario.update(
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
           contactoMapper.toContactosDomain(dto.getContactos()),
           enderecoMapper.toDomain(dto.getEndereco()),
           familiarMapper.toFamiliaresDomain(dto.getFamiliares()),
           habilitacaoLiterariaMapper.toHabilitacoesLiterariasDomain(dto.getHabilitacoesLiterarias()),
           formacaoFeitaMapper.toFormacoesFeitasDomain(dto.getFormacoesFeitas()),
           experienciaProfissionalMapper.toExperienciasProfissionaisDomain(dto.getExperienciasProfssionais()),
           documentoMapper.toDocumentosDomain(dto.getAnexos()),
           dadosBancariosMapper.toDadosBancariosDomain(dto.getDadosBancarios())
       );


       var dados = dto.getDadosContratuais();

       var tipoContrato = paramContratoMapper.toDomain(dados.getTipoContrato());
       var paramCargo = paramCargoMapper.toDomain(dados.getCargoPosicao());
       var direcao = instituicaoMapper.toDomain(dados.getDirecao());
       var seccao = secaoMapper.toDomain(dados.getSeccao());
       var paramCarreira = paramCarreiraMapper.toDomain(dados.getCarreira());
       var paramCategoria = paramCategoriaMapper.toDomain(dados.getCategoria());
       var paramEscalao = paramEscalaoMapper.toDomain(dados.getEscalaoReferencia());
       var paramVinculo = paramVinculoMapper.toDomain(dados.getTipoVinculoLaboral());
       Geografia pais = geografiaMapper.toDomain(dados.getPais());
       Geografia ilha = geografiaMapper.toDomain(dados.getIlha());
       var localTrabalho = paramLocalTrabMapper.toDomain(dados.getLocalTrabalho());

       List<DefPagamento> defPagamentos = dto.getEncargosDescontos().stream()
           .map(e -> {
             TipoMovimento tipoMov = tipoMovimentoMapper.toDomain(e.getTipoEncargoId());
             return DefPagamento.create(
                 e.getId(),
                 e.getValor(),
                 tipoMov,
                 e.getDataInicio(),
                 e.getDataFim(),
                 e.getObservacoes()
             );
           })
           .toList();

       List<DefinicaoRemuneracao> defRemuneracoes = dto.getSubsidios().stream()
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

       funcionario.atualizarDadosContratuais(
           tipoContrato,
           paramCargo,
           direcao,
           seccao,
           dados.getCentroCusto(),
           paramCarreira,
           paramCategoria,
           paramEscalao,
           paramVinculo,
           dados.getRegimeTrabalho(),
           dados.getSalario(),
           dados.getMoeda(),
           dados.getDataInicio(),
           dados.getDataFim(),
           dados.getDuracaoMeses(),
           localTrabalho,
           pais,
           ilha,
           defPagamentos,
           defRemuneracoes
       );

       if(validacao!=null){
         funcionario.validar(validacao);
       }


     funcionarioRepository.save(funcionario);

     return ResponseEntity.ok(Map.of());

   }


}
