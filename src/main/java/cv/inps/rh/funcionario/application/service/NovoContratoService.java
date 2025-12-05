package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PagTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

  private final ValidarDadosContratuaisService validarDadosContratuaisService;

  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var dadosContratuais = dto.getDadosContratuais();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    validarDadosContratuaisService.validar(dadosContratuais);

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario possui validacao de contrato pendente");
    }

   boolean temContratoAtivo = funcionario.getContratos().stream()
        .anyMatch(c -> c.getEstado().equals(Estado.A));

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

    // contrato anterior, se existir
    var contratoAnterior = funcionario.getContratos().stream()
        .max(Comparator.comparing(ContratoEntity::getVersao))
        .orElse(null);
// Cria o contrato
    var contrato = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setSituacaoLaboral(tipoSituacao);

// Define versão e contrato_id
    if (contratoAnterior == null) {
      contrato.setVersao(1);
      contrato.setContratoId(null); // primeiro contrato, raiz
    } else {
      contrato.setVersao(contratoAnterior.getVersao() + 1);
      contrato.setContratoId(contratoAnterior); // contrato pai
    }

    funcionario.getContratos().add(contrato);

    System.out.println("isPrimeiroContrato:: " + isPrimeiroContrato);
    System.out.println("contrato versao: " + contrato.getVersao());

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

    var tiposRelacionamento = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tiposRelacionamento.setFunId(funcionario);
    tiposRelacionamento.setContrVinculoId(contrato);
    tiposRelacionamento.setCarreiraId(carreira);
    tiposRelacionamento.setRegimeId(regime);
    tiposRelacionamento.setMobId(mobilidade);
    tiposRelacionamento.setFlgProcessa("NAO");
    tiposRelacionamento.setEstActAdm(1);
    tiposRelacionamento.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tiposRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiposRelacionamento);
    funcionario.getValidacoes().add(valid);


    var tipoMovimentoSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
    var tipoMovimentoInps = tipoMovimentoHelper.getTipoMovimentoEntityInps();
    var tipoMovimentoIUR = tipoMovimentoHelper.getTipoMovimentoEntityIur();

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

    // Percorre todas as remunerações e cria RemuneracaoTiprelEntity
    List<RemuneracaoTiprelEntity> listTiprel = saved.getDefinicoesRenumeracoes().stream()
        .map(rem -> {
          RemuneracaoTiprelEntity r = new RemuneracaoTiprelEntity();
          r.setRemId(rem);
          r.setTiprelId(tiposRelacionamento);
          r.setUuid(UuidCreator.getTimeOrderedEpoch());
          r.setEstado(Estado.P);
          return r;
        })
        .collect(Collectors.toList());
    remuneracaoTiprelEntityRepository.saveAll(listTiprel);


    // Percorre todas as definições de pagamento e cria PagTiprelEntity
    List<PagTiprelEntity> listPagTiprel = saved.getDefinicoesPagamentos().stream()
        .map(pag -> {
          PagTiprelEntity p = new PagTiprelEntity();
          p.setPagId(pag);
          p.setTiprelId(tiposRelacionamento);
          p.setUuid(UuidCreator.getTimeOrderedEpoch());
          p.setEstado(Estado.P);
          return p;
        })
        .collect(Collectors.toList());
    // Salva todas em batch
    pagTiprelEntityRepository.saveAll(listPagTiprel);


    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento);
  }
}
