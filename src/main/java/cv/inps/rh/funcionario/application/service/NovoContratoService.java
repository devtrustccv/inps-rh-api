package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  private final TipoMovimentoHelper tipoMovimentoHelper;

  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final EntityManager entityManager;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;


  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var dadosContratuais = dto.getDadosContratuais();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

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
      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
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
    contrato.setTipoSituacao(tipoSituacao);

    // Define versão e contrato_id
    if (contratoAnterior == null) {
      contrato.setVersao(1);
      contrato.setContratoId(null); // primeiro contrato, raiz
    } else {
      contrato.setVersao(contratoAnterior.getVersao() + 1);
      contrato.setContratoId(contratoAnterior); // contrato pai
    }

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

    CarreiraEntity carreira = null;
    if (Objects.equals(1, paramVinculo.getFlgCarreira()) && dadosContratuais.getCarreiraId() != null) {
      carreira = carreiraMapper.toCarreira(dadosContratuais, Estado.P);
      if (carreira != null) {
        carreira.setContrVinculoId(contrato);
        contrato.getCarreiras().add(carreira);
      }
    }

    var paramSituacaoLaboral = paramSitLaboralEntityRepository.findByCodigo(SituacaoLaboral.ATIVO.name()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com codigo ATIVO."));

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
    tiposRelacionamento.setFlgProcessa(0);
    tiposRelacionamento.setEstActAdm(1);
    tiposRelacionamento.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tiposRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiposRelacionamento);
    funcionario.getValidacoes().add(valid);

    // verifica se vinculo tem salario
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      /******************** INI RENUMERACOES ********************************/
      if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesRenumeracoes(remList);
      }

      var listAssociacaoVinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(dadosContratuais.getTipoVinculoLaboralId(),
              "REM");

      if (!CollectionUtils.isEmpty(listAssociacaoVinculoTipoMovimentoREM)) {
        listAssociacaoVinculoTipoMovimentoREM.forEach(movimento -> {
          var renumeracao = definicaoRemuneracaoMapper.createRenumeracao(
              dadosContratuais.getSalario(),
              movimento.getTmId(),
              dadosContratuais.getDataInicio(),
              dadosContratuais.getDataFim(),
              funcionario,
              dadosContratuais.getMoeda());
          funcionario.getDefinicoesRenumeracoes().add(renumeracao);
        });
      }
      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesPagamentos(pagList);
      }

      var listAssociacaoVinculoTipoMovimentoPag = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(
          dadosContratuais.getTipoVinculoLaboralId(),
          "PAG");

      if (!CollectionUtils.isEmpty(listAssociacaoVinculoTipoMovimentoPag)) {
        listAssociacaoVinculoTipoMovimentoPag.forEach(movimento -> {
          var pagamento = defPagamentoMapper.createPagamento(
              BigDecimal.ZERO,
              movimento.getTmId(),
              dadosContratuais.getDataInicio(),
              dadosContratuais.getDataFim(),
              funcionario);
          funcionario.getDefinicoesPagamentos().add(pagamento);
        });
      }
    }
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    if (saved.getDefinicoesRenumeracoes() != null && !saved.getDefinicoesRenumeracoes().isEmpty()) {
      java.util.List<TipoRelRemPagEntity> lista = new java.util.ArrayList<>();
      for (var rem : saved.getDefinicoesRenumeracoes()) {
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tiposRelacionamento);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    if (saved.getDefinicoesPagamentos() != null && !saved.getDefinicoesPagamentos().isEmpty()) {
      java.util.List<TipoRelRemPagEntity> lista = new java.util.ArrayList<>();
      for (var pag : saved.getDefinicoesPagamentos()) {
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tiposRelacionamento);
        assoc.setPagId(pag);
        assoc.setRemId(null);
        lista.add(assoc);
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contrato.getId());
          validacaoEntityRepository.save(e);
        });

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento);
  }
}
