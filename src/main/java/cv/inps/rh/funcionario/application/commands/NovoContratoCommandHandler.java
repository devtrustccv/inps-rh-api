package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.parametrizacao.domain.repository.ParamSituacaoLaboralRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.parametrizacao.infrastructure.mappers.*;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import java.util.List;

@Component
public class NovoContratoCommandHandler implements CommandHandler<NovoContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NovoContratoCommandHandler.class);

  private final FuncionarioRepository funcionarioRepository;
  private final ParamContratoMapper paramContratoMapper;
  private final ParamCargoMapper paramCargoMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final SecaoMapper secaoMapper;
  private final ParamCarreiraMapper paramCarreiraMapper;
  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamVinculoMapper paramVinculoMapper;
  private final GeografiaMapper geografiaMapper;
  private final ParamLocalTrabMapper paramLocalTrabMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final FuncionarioMapper funcionarioMapper;

  private final ParamSituacaoLaboralRepository paramSituacaoLaboralRepository;

  public NovoContratoCommandHandler(FuncionarioRepository funcionarioRepository,
                                    ParamContratoMapper paramContratoMapper,
                                    ParamCargoMapper paramCargoMapper,
                                    InstituicaoMapper instituicaoMapper,
                                    SecaoMapper secaoMapper,
                                    ParamCarreiraMapper paramCarreiraMapper,
                                    ParamCategoriaMapper paramCategoriaMapper,
                                    ParamEscalaoMapper paramEscalaoMapper,
                                    ParamVinculoMapper paramVinculoMapper,
                                    GeografiaMapper geografiaMapper,
                                    ParamLocalTrabMapper paramLocalTrabMapper,
                                    TipoMovimentoMapper tipoMovimentoMapper, FuncionarioMapper funcionarioMapper, ParamSituacaoLaboralRepository paramSituacaoLaboralRepository) {
    this.funcionarioRepository = funcionarioRepository;
    this.paramContratoMapper = paramContratoMapper;
    this.paramCargoMapper = paramCargoMapper;
    this.instituicaoMapper = instituicaoMapper;
    this.secaoMapper = secaoMapper;
    this.paramCarreiraMapper = paramCarreiraMapper;
    this.paramCategoriaMapper = paramCategoriaMapper;
    this.paramEscalaoMapper = paramEscalaoMapper;
    this.paramVinculoMapper = paramVinculoMapper;
    this.geografiaMapper = geografiaMapper;
    this.paramLocalTrabMapper = paramLocalTrabMapper;
    this.tipoMovimentoMapper = tipoMovimentoMapper;
    this.funcionarioMapper = funcionarioMapper;
    this.paramSituacaoLaboralRepository = paramSituacaoLaboralRepository;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(NovoContratoCommand command) {
     LOGGER.info("Novo contrato para funcionario: {}", command.getIdFuncionario());

     DadosContratuaisReqDTO dados = command.getDadoscontratuaisreq();
     var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

     var funcionario = funcionarioRepository.findById(idFunc).orElseThrow();

     var tipoContrato = paramContratoMapper.toDomain(dados.getTipoContratoId());
     var cargoPosicao = paramCargoMapper.toDomain(dados.getCargoPosicaoId());
     var direcao = instituicaoMapper.toDomain(dados.getDirecaoId());
     var seccao = secaoMapper.toDomain(dados.getSeccaoId());
     var carreira  = paramCarreiraMapper.toDomain(dados.getCarreiraId());
     var categoria = paramCategoriaMapper.toDomain(dados.getCategoriaId());
     var escalao = paramEscalaoMapper.toDomain(dados.getEscalaoReferenciaId());
     var vinculo = paramVinculoMapper.toDomain(dados.getTipoVinculoLaboralId());
     var pais = geografiaMapper.toDomain(dados.getPaisId());
     var ilha = geografiaMapper.toDomain(dados.getIlhaId());
     var localTrabalho = paramLocalTrabMapper.toDomain(dados.getLocalTrabalhoId());

    var paramSituacaoLaboral = paramSituacaoLaboralRepository.findByNomeActivo()
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("ParamSituacaoLaboral com nome 'ATIVO'"));

     List<DefPagamento> defPagamentos = dados.getEncargosDescontos().stream()
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

     List<DefinicaoRemuneracao> defRemuneracoes = dados.getSubsidios().stream()
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

     funcionario.registrarNovoContrato(
         tipoContrato,
         cargoPosicao,
         direcao,
         seccao,
         dados.getCentroCusto(),
         carreira,
         categoria,
         escalao,
         vinculo,
         paramSituacaoLaboral,
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

     funcionarioRepository.save(funcionario);

     var resp = funcionarioMapper.dadosContratuaisRespDTO(funcionario);
     return ResponseEntity.ok(resp);
  }

}
