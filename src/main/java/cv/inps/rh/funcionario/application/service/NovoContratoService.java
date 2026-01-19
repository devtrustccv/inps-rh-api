package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
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

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;


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

    boolean isPrimeiroContrato = funcionario.getContratos().isEmpty();

    if (isPrimeiroContrato) {
      return primeiroContrato(funcionario, dadosContratuais);
    }

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    tipoRelacionamentoAtual.setEstActAdm(0);

    var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
    contratoAtual.setEstado(Estado.I);
    var fim = contratoAtual.getDataFim();
    tipoRelacionamentoAtual.setDataFim(fim);

    if (tipoRelacionamentoAtual.getRegimeId() != null && tipoRelacionamentoAtual.getRegimeId().getDataFim() == null) {
      tipoRelacionamentoAtual.getRegimeId().setDataFim(fim);
    }

    var contratoNovo = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contratoNovo.setFunId(funcionario);
    contratoNovo.setTipoSituacao("CONTINUIDADE");
    contratoNovo.setVersao(contratoAtual.getVersao() + 1);
    contratoNovo.setContratoId(contratoAtual); // contrato pai
    funcionario.getContratos().add(contratoNovo);

    //**************** INI verifica se mudou carreira e tambem se foi escolhido carreira***********/
    CarreiraEntity carreira = null;
    if (Objects.equals(1, paramVinculo.getFlgCarreira()) && dadosContratuais.getCarreiraId() != null) {

      CarreiraEntity atual = tipoRelacionamentoAtual.getCarreiraId() != null ? tipoRelacionamentoAtual.getCarreiraId() : null;
      if (atual != null) {
        carreira = mudaCarreiraOuManter(atual, dadosContratuais);
        carreira.setContrVinculoId(contratoNovo);
        contratoNovo.getCarreiras().add(carreira);
      }

    }

    //**************** FIM verifica se mudou carreira e tambem se foi escolhido carreira***********/

    var mobilidade = mudaMobilidadeOuManter(tipoRelacionamentoAtual.getMobId(), dadosContratuais,
        funcionario);

    var regime = regimeTrabalhoMapper.toRegime(dadosContratuais, Estado.P);
    if (regime != null) {
      regime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(regime);
    }

    var paramSituacaoLaboral = entityManager.getReference(ParamSituacaoEntity.class, dadosContratuais.getSituacaoLaboralId());


    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "NOVO_CONTRATO", "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contratoNovo);
    contratoNovo.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tiposRelacionamentoNovo = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tiposRelacionamentoNovo.setFunId(funcionario);
    tiposRelacionamentoNovo.setContrVinculoId(contratoNovo);
    tiposRelacionamentoNovo.setCarreiraId(carreira);
    tiposRelacionamentoNovo.setRegimeId(regime);
    tiposRelacionamentoNovo.setMobId(mobilidade);
    tiposRelacionamentoNovo.setFlgProcessa(0);
    tiposRelacionamentoNovo.setEstActAdm(1);
    tiposRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tiposRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiposRelacionamentoNovo);
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

      var escalaoId = tipoRelacionamentoAtual.getCarreiraId() != null ?
          tipoRelacionamentoAtual.getCarreiraId().getEscalaoId().getId() : null;

      var vinculoId = tipoRelacionamentoAtual.getContrVinculoId() != null ?
          tipoRelacionamentoAtual.getContrVinculoId().getVinculoId().getId() : null;


      var houveMudancaSalario = houveMudancaSalario(vinculoId, escalaoId, dadosContratuais, funcionario);

      if (houveMudancaSalario) {
        if (funcionario.getDefinicoesRenumeracoes() != null) {
          funcionario.getDefinicoesRenumeracoes().stream()
              .filter(r -> r.getEstado() == Estado.A)
              .forEach(r -> r.setEstado(Estado.I));
        }

        var vinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
            .findByVinculoId_IdAndTipo(dadosContratuais.getTipoVinculoLaboralId(),
                "REM").getFirst(); // so é associado um tipo REM SALL ao vinculo

        var renumeracao = definicaoRemuneracaoMapper.createRenumeracao(
            dadosContratuais.getSalario(),
            vinculoTipoMovimentoREM.getTmId(),
            dadosContratuais.getDataInicio(),
            dadosContratuais.getDataFim(),
            funcionario,
            dadosContratuais.getMoeda());
        funcionario.getDefinicoesRenumeracoes().add(renumeracao);
      }

      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesPagamentos(pagList);
      }

      if(Objects.equals(vinculoId, dadosContratuais.getTipoVinculoLaboralId())) {

        if (funcionario.getDefinicoesPagamentos() != null) {
          funcionario.getDefinicoesPagamentos().stream()
              .filter(r -> r.getEstado() == Estado.A)
              .forEach(r -> r.setEstado(Estado.I));
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
    }
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    // Associações para remunerações
    if (saved.getDefinicoesRenumeracoes() != null) {
      List<TipoRelRemPagEntity> lista = new ArrayList<>();
      for (var rem : saved.getDefinicoesRenumeracoes()) {
        if (rem.getEstado() == Estado.A) {  // só ativo
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(tiposRelacionamentoNovo);
          assoc.setRemId(rem);
          assoc.setPagId(null);
          lista.add(assoc);
        }
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    // Associações para pagamentos
    if (saved.getDefinicoesPagamentos() != null) {
      List<TipoRelRemPagEntity> lista = new ArrayList<>();
      for (var pag : saved.getDefinicoesPagamentos()) {
        if (pag.getEstado() == Estado.A) { // só ativo
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(tiposRelacionamentoNovo);
          assoc.setPagId(pag);
          assoc.setRemId(null);
          lista.add(assoc);
        }
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contratoNovo.getId());
          validacaoEntityRepository.save(e);
        });

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamentoNovo);
  }

  private CarreiraEntity mudaCarreiraOuManter(
      CarreiraEntity carreiraAtual,
      DadosContratuaisReqDTO dc) {

    if (!houveMudancaFuncionalCarreira(carreiraAtual, dc)) {
      return carreiraAtual;
    }

    carreiraAtual.setDataFim(LocalDate.now());
    return carreiraMapper.toCarreira(dc, Estado.P);
  }

  private boolean houveMudancaSalario(Long vinculoId, Long escalaoId, DadosContratuaisReqDTO dc, FuncionarioEntity funcionario) {

    if (escalaoId != null && escalaoId > 0) {
      if (!Objects.equals(escalaoId, dc.getEscalaoReferenciaId())) {
        return true;
      }
    }

    // se nao tiver escalao procuramos pelo vinculo associado ao tipo movimento, e depois vamos procurar na remuneracao
    var vinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoId,
            "REM");
    if (CollectionUtils.isEmpty(vinculoTipoMovimentoREM))
      return true;

    var renumeracao = definicaoRemuneracaoEntityRepository
        .findByFunIdAndTmIdAndEstado(funcionario, vinculoTipoMovimentoREM.getFirst().getTmId(), Estado.A).getFirst();

    if (renumeracao != null) {
      if (!Objects.equals(renumeracao.getValor(), dc.getSalario())) {
        return true;
      }
    }

    return false;
  }

  private boolean houveMudancaFuncionalCarreira(CarreiraEntity atual, DadosContratuaisReqDTO dc) {

    if (atual == null) {
      return true;
    }

    if (!Objects.equals(atual.getCargoId().getId(), dc.getCargoPosicaoId())) {
      return true;
    }

    if (!Objects.equals(atual.getEscalaoId().getId(), dc.getEscalaoReferenciaId())) {
      return true;
    }

    if (!Objects.equals(atual.getCategoriaId().getId(), dc.getCategoriaId())) {
      return true;
    }

    return !Objects.equals(atual.getCarrPccsId().getId(), dc.getCarreiraId());

  }

  private MobilidadeEntity mudaMobilidadeOuManter(MobilidadeEntity mobilidadeAtual, DadosContratuaisReqDTO dc,
                                                  FuncionarioEntity funcionario) {

    if (!houveMudancaFuncionalMobilidade(mobilidadeAtual, dc)) {
      return mobilidadeAtual;
    }
    mobilidadeAtual.setDataFim(LocalDate.now());

    MobilidadeEntity nova = mobilidadeMapper.toMobilidade(dc, Estado.P);
    nova.setFunId(funcionario);
    funcionario.getMobilidades().add(nova);
    return nova;
  }


  private boolean houveMudancaFuncionalMobilidade(MobilidadeEntity atual, DadosContratuaisReqDTO dc) {

    if (!Objects.equals(atual.getLocalTrabId().getId(), dc.getLocalTrabalhoId())) {
      return true;
    }

    if (!Objects.equals(atual.getSecaoId().getId(), dc.getSeccaoId())) {
      return true;
    }

    if (!Objects.equals(atual.getInstidId() != null ? atual.getInstidId().getId() : null, dc.getDirecaoId())) {
      return true;
    }

    return false;
  }


  private DadosContratuaisRespDTO primeiroContrato(FuncionarioEntity funcionario, DadosContratuaisReqDTO dadosContratuais) {

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    var contrato = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setTipoSituacao("INICIO");
    contrato.setVersao(1);
    contrato.setContratoId(null);

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

    var paramSituacaoLaboral = entityManager.getReference(ParamSituacaoEntity.class, dadosContratuais.getSituacaoLaboralId());


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

      var vinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(dadosContratuais.getTipoVinculoLaboralId(),
              "REM").getFirst(); // so é associado um tipo REM SALL ao vinculo

      if (!Objects.isNull(vinculoTipoMovimentoREM)) {
        var renumeracao = definicaoRemuneracaoMapper.createRenumeracao(
            dadosContratuais.getSalario(),
            vinculoTipoMovimentoREM.getTmId(),
            dadosContratuais.getDataInicio(),
            dadosContratuais.getDataFim(),
            funcionario,
            dadosContratuais.getMoeda());
        funcionario.getDefinicoesRenumeracoes().add(renumeracao);
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

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contrato.getId());
          validacaoEntityRepository.save(e);
        });

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento);

  }
}
