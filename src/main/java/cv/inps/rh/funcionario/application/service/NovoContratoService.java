package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NovoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;

  private final ValidacaoEntityRepository validacaoEntityRepository;


  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

    boolean temContratoAtivo = funcionario.getContratos().stream()
        .anyMatch(c -> c.getEstado() == Estado.A);

    if (temContratoAtivo) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionário já possui contrato ativo"
      );
    }

    boolean isPrimeiroContrato = funcionario.getContratos().isEmpty();
    String tipoSituacao = isPrimeiroContrato ? "INICIO" : "CONTINUIDADE";

    if(!isPrimeiroContrato){
      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      tipoRelacionamentoAtual.setEstActAdm(0);
      var contratoAtual = tipoRelacionamentoAtual.getContratoId();

      if(tipoRelacionamentoAtual.getCarreiraId()!=null){
       tipoRelacionamentoAtual.getCarreiraId().setDataFim(contratoAtual.getDataFim());
      }
      if(tipoRelacionamentoAtual.getRegimeId()!=null && tipoRelacionamentoAtual.getRegimeId().getDataFim()==null){
        tipoRelacionamentoAtual.getRegimeId().setDataFim(contratoAtual.getDataFim());
      }
      if(tipoRelacionamentoAtual.getMobId()!=null && tipoRelacionamentoAtual.getMobId().getDataFim()==null){
        tipoRelacionamentoAtual.getMobId().setDataFim(contratoAtual.getDataFim());
      }
    }


    /************ adicionar novo contrato **************/
    var dc = dto.getDadosContratuais();
    if (dc == null) {
      throw IgrpResponseStatusException.badRequest("Dados contratuais obrigatórios");
    }
    var contrato = contratoMapper.toContrato(dc, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setVersao(1);
    contrato.setSituacaoLaboral(tipoSituacao);
    funcionario.getContratos().add(contrato);


    var carreira = carreiraMapper.toCarreira(dc, Estado.P);
    carreira.setFunId(funcionario);
    funcionario.getCarreiras().add(carreira);


    var regime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    regime.setFunId(funcionario);
    funcionario.getRegimesTrabalhos().add(regime);


    var mobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    mobilidade.setFunId(funcionario);
    funcionario.getMobilidades().add(mobilidade);


    if (dc.getSubsidios() != null && !dc.getSubsidios().isEmpty()) {
      var remList = dc.getSubsidios().stream()
          .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P))
          .toList();
      funcionario.getDefinicoesRenumeracoes().addAll(remList);
    }

    if (dc.getEncargosDescontos() != null && !dc.getEncargosDescontos().isEmpty()) {
      var pagList = dc.getEncargosDescontos().stream()
          .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
          .toList();
       funcionario.getDefinicoesPagamentos().addAll(pagList);
    }

    var param = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    if (param == null) {
      throw IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com nome ATIVO. " +
          "Verifique se o parametro esta cadastrado no banco de dados e tente novamente.");
    }

    var sl = dadosContratuaisMapper.toSituacaoLaboralInicial(dc, param, Estado.P);
    sl.setFunId(funcionario);
    funcionario.getSituacoesLaborais().add(sl);


    var tr = dadosContratuaisMapper.toRelacionamento(dc, Estado.P);
    tr.setFunId(funcionario);
    tr.setContratoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    //tr.setSituacLaboralId(sl);
    funcionario.getTiposrelacionamentos().add(tr);


    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT","CONTRATO", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tr);
    funcionario.getValidacoes().add(valid);

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contrato.getId());
          validacaoEntityRepository.save(e);
        });

    return dadosContratuaisMapper.dadosContratuaisRespDTO(saved);
  }
}
