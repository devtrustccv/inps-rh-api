package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PagTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PagTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;

  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

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
      var contratoAtual = tipoRelacionamentoAtual.getContratoId();
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

    /************ adicionar novo contrato **************/
    var dc = dto.getDadosContratuais();
    if (dc == null) {
      throw IgrpResponseStatusException.badRequest("Dados contratuais obrigatórios");
    }
    var contrato = contratoMapper.toContrato(dc, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setVersao(isPrimeiroContrato ? 1
        : (funcionarioRules.getContratoComMaiorVersao(funcionario) != null
            && funcionarioRules.getContratoComMaiorVersao(funcionario).getVersao() != null
                ? funcionarioRules.getContratoComMaiorVersao(funcionario).getVersao() + 1
                : 1));
    contrato.setSituacaoLaboral(tipoSituacao);
    funcionario.getContratos().add(contrato);

    var regime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if (regime != null) {
      regime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(regime);
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(funcionario);
      funcionario.getMobilidades().add(mobilidade);
    }

    var carreira = carreiraMapper.toCarreira(dc, Estado.P);
    if (carreira != null) {
      carreira.setContrVinculoId(contrato);
    }

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

    var tr = dadosContratuaisMapper.toRelacionamento(dc, Estado.P);
    tr.setFunId(funcionario);
    tr.setContratoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    // tr.setSituacLaboralId(sl);
    funcionario.getTiposrelacionamentos().add(tr);

    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT", "CONTRATO", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tr);
    funcionario.getValidacoes().add(valid);

    var tmSalario = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("SALL", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento SALARIO não encontrado"));
    var tmInps = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("INPS", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento INPS não encontrado"));
    var tmIur = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("IUR", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento IUR não encontrado"));

    var renumeracaoSalario = definicaoRemuneracaoMapper
        .createRenumeracao(dc.getSalario() != null ? dc.getSalario() : BigDecimal.ZERO, tmSalario, dc.getDataInicio(),
            dc.getDataFim(), funcionario);
    var renumeracaoInps = definicaoRemuneracaoMapper
        .createRenumeracao(BigDecimal.ZERO, tmInps, dc.getDataInicio(), dc.getDataFim(), funcionario);
    funcionario.getDefinicoesRenumeracoes().addAll(List.of(renumeracaoSalario, renumeracaoInps));

    var pagamentoDescontoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tmIur, dc.getDataInicio(), dc.getDataFim(), funcionario);
    var pagamentoDescontoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tmInps, dc.getDataInicio(), dc.getDataFim(), funcionario);
    funcionario.getDefinicoesPagamentos().addAll(List.of(pagamentoDescontoIUR, pagamentoDescontoINPS));

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    var sl = dadosContratuaisMapper.toSituacaoLaboralInicial(dc, param, Estado.P);
    sl.setContrVinculoId(contrato);
    situacaoLaboralEntityRepository.save(sl);

    if (carreira != null) {
      carreiraEntityRepository.save(carreira);
    }

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
