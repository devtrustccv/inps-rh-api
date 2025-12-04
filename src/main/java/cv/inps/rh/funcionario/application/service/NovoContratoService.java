package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PagTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
  private final ValidacaoEntityRepository validacaoEntityRepository;

  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;
  private final TipoMovimentoHelper tipoMovimentoHelper;

  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var dadosContratuais = dto.getDadosContratuais();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    boolean temContratoAtivo = funcionario.getContratos().stream()
        .anyMatch(c -> c.getEstado() == Estado.A);

    if (temContratoAtivo) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionário já possui contrato ativo");
    }

    boolean isPrimeiroContrato = funcionario.getContratos().isEmpty();
    String tipoSituacao = isPrimeiroContrato ? "INICIO" : "CONTINUIDADE";

    if (!isPrimeiroContrato) {
      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      tipoRelacionamentoAtual.setEstActAdm(0);
      var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
      var fim = contratoAtual != null && contratoAtual.getDataFim() != null ? contratoAtual.getDataFim()
          : LocalDate.now();
      tipoRelacionamentoAtual.setDataFim(fim);
      if (tipoRelacionamentoAtual.getCarreiraId() != null) {
        tipoRelacionamentoAtual.getCarreiraId().setDataFim(fim);
      }
      if (tipoRelacionamentoAtual.getRegimeId() != null && tipoRelacionamentoAtual.getRegimeId().getDataFim() == null) {
        tipoRelacionamentoAtual.getRegimeId().setDataFim(fim);
      }
      if (tipoRelacionamentoAtual.getMobId() != null && tipoRelacionamentoAtual.getMobId().getDataFim() == null) {
        tipoRelacionamentoAtual.getMobId().setDataFim(fim);
      }
    }

    /********* adicionar novo contrato **************/

    var contrato = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setVersao(isPrimeiroContrato ? 1
        : (funcionarioRules.getContratoComMaiorVersao(funcionario) != null
            && funcionarioRules.getContratoComMaiorVersao(funcionario).getVersao() != null
                ? funcionarioRules.getContratoComMaiorVersao(funcionario).getVersao() + 1
                : 1));
    contrato.setSituacaoLaboral(tipoSituacao);
    funcionario.getContratos().add(contrato);

    var regime = regimeTrabalhoMapper.toRegime(dadosContratuais, Estado.P);
    if (regime != null) {
      regime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(regime);
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dadosContratuais, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(funcionario);
      funcionario.getMobilidades().add(mobilidade);
    }

    var carreira = carreiraMapper.toCarreira(dadosContratuais, Estado.P);
    if (carreira != null) {
      carreira.setContrVinculoId(contrato);
      contrato.getCarreiras().add(carreira);
    }


    var paramSituacaoLaboral = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    if (paramSituacaoLaboral == null) {
      throw IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com nome ATIVO. " +
          "Verifique se o parametro esta cadastrado no banco de dados e tente novamente.");
    }

    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "NOVO_CONTRATO", "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contrato);
    contrato.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tr = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tr.setFunId(funcionario);
    tr.setContrVinculoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    tr.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tr);

    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT", "CONTRATO", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tr);
    funcionario.getValidacoes().add(valid);


    var tipoMovimentoSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
    var tipoMovimentoInps = tipoMovimentoHelper.getTipoMovimentoEntityInps();
    var tipoMovimentoIUR =  tipoMovimentoHelper.getTipoMovimentoEntityIur();

    /******************** INI RENUMERACOES ********************************/
    if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
      var remList = dadosContratuais.getSubsidios().stream()
          .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P))
          .collect(Collectors.toList());
      funcionario.setDefinicoesRenumeracoes(remList);
    }

    var renumeracaoSalario = definicaoRemuneracaoMapper
        .createRenumeracao(dadosContratuais.getSalario(), tipoMovimentoSalario, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), funcionario, dadosContratuais.getMoeda());
    /*var renumeracaoInps = definicaoRemuneracaoMapper
        .createRenumeracao(BigDecimal.ZERO, tipoMovimentoInps, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), funcionario, dadosContratuais.getMoeda());*/
    funcionario.getDefinicoesRenumeracoes().addAll(new ArrayList<>(List.of(renumeracaoSalario)));
    /******************** FIM RENUMERACOES ********************************/


    /******************** INI PAGAMENTOS DESCONTOS ********************************/
    if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
      var pagList = dadosContratuais.getEncargosDescontos().stream()
          .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
          .collect(Collectors.toList());
      funcionario.setDefinicoesPagamentos(pagList);
    }

    var pagamentoDescontoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoIUR, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), funcionario);
    var pagamentoDescontoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoInps, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), funcionario);

    funcionario.getDefinicoesPagamentos().addAll(new ArrayList<>(List.of(pagamentoDescontoIUR, pagamentoDescontoINPS)));
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/


    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contrato.getId());
          validacaoEntityRepository.save(e);
        });

    var listTiprel = saved.getDefinicoesRenumeracoes().stream()
        .map(rem -> {
          var r = new RemuneracaoTiprelEntity();
          r.setRemId(rem);
          r.setTiprelId(tr);
          r.setEstado(Estado.P);
          return r;
        }).toList();
    remuneracaoTiprelEntityRepository.saveAll(listTiprel);

    var listPagTiprel = saved.getDefinicoesPagamentos().stream()
        .map(pag -> {
          var p = new PagTiprelEntity();
          p.setPagId(pag);
          p.setTiprelId(tr);
          p.setEstado(Estado.P);
          return p;
        }).toList();
    pagTiprelEntityRepository.saveAll(listPagTiprel);

    return dadosContratuaisMapper.dadosContratuaisRespDTO(saved);
  }
}
