package cv.inps.rh.funcionario.application.service.historicolaboral;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.ValidarHistoricoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HistoricoLaboralWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final ContratoMapper contratoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final OrdemServicoEntityRepository ordemServicoEntityRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public ValidarNovoHistoricoLaboralDTO validar(ValidarHistoricoLaboralCommand command) {

    var dto = command.getValidarnovohistoricolaboral();

    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).getValor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var dc = dto.getDadosContratuais();

    validarDadosContratuais(dc);

    if (dto.getValidar() != null) {
      var estadoFinal = dto.getValidar().name().equals("SIM") ? Estado.A : Estado.I;
      var relacionamentoPendente = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario,
          Estado.P);
      if (relacionamentoPendente == null) {
        throw IgrpResponseStatusException.badRequest("Não existe histórico laboral pendente para validar");
      }

      relacionamentoPendente.setEstado(estadoFinal);
      tiposRelacionamentoEntityRepository.save(relacionamentoPendente);

      var definicoesRemP = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario,
          Estado.P);
      for (var dr : definicoesRemP) {
        dr.setEstado(estadoFinal);
        definicaoRemuneracaoEntityRepository.save(dr);
      }

      var definicoesPagP = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
      for (var dp : definicoesPagP) {
        dp.setEstado(estadoFinal);
        defPagamentoEntityRepository.save(dp);
      }

      var remTiprels = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(relacionamentoPendente,
          Estado.P.name());
      for (var rt : remTiprels) {
        rt.setEstado(estadoFinal);
        remuneracaoTiprelEntityRepository.save(rt);
      }

      var pagTiprels = pagTiprelEntityRepository.findByTiprelIdAndEstado(relacionamentoPendente, Estado.P);
      for (var pt : pagTiprels) {
        pt.setEstado(estadoFinal);
        pagTiprelEntityRepository.save(pt);
      }

      var validacao = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamentoPendente,
          Estado.P, "HISTORICO_LABORAL");
      if (validacao != null) {
        validacao.setEstado(estadoFinal);
        validacaoEntityRepository.save(validacao);
      }

      if (dto.getGerarOrdemServico() != null && dto.getGerarOrdemServico().equalsIgnoreCase("SIM")) {
        var os = new OrdemServicoEntity();
        os.setNuOrdem("OS-" + UuidCreator.getTimeOrderedEpoch());
        os.setDescricao((dto.getTipoAlteracao() != null ? dto.getTipoAlteracao() : "") + " - "
            + (funcionario.getNome() != null ? funcionario.getNome() : ""));
        os.setReferente(dto.getTipoAlteracao());
        os.setFunId(funcionario);
        os.setTiprelId(relacionamentoPendente);
        os.setValidacaoId(validacao);
        os.setEstado(Estado.A);
        os.setUuid(UuidCreator.getTimeOrderedEpoch());
        ordemServicoEntityRepository.save(os);
      }

      return dto;
    }

    var trPendenteExistente = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario,
        Estado.P);
    if (trPendenteExistente != null) {
      throw IgrpResponseStatusException
          .badRequest("Existe histórico laboral pendente por validar. Valide antes de criar novo.");
    }

    if (dto.getTipoAlteracao() != null && requiresActiveContract(dto.getTipoAlteracao())) {
      var trAtualContrato = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      if (trAtualContrato == null || trAtualContrato.getContratoId() == null
          || trAtualContrato.getContratoId().getEstado() != Estado.A) {
        throw IgrpResponseStatusException.badRequest("Não existe contrato activo associado ao colaborador");
      }
    }

    var trAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    if (trAtual == null) {
      throw IgrpResponseStatusException.notFound("Relacionamento atual não encontrado");
    }
    trAtual.setDataFim(LocalDate.now());
    trAtual.setEstActAdm(0);
    trAtual.setEstado(Estado.I);
    tiposRelacionamentoEntityRepository.save(trAtual);

    var remAssocAtivos = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(trAtual, Estado.A.name());
    for (var rt : remAssocAtivos) {
      rt.setEstado(Estado.I);
      remuneracaoTiprelEntityRepository.save(rt);
    }
    var pagAssocAtivos = pagTiprelEntityRepository.findByTiprelIdAndEstado(trAtual, Estado.A);
    for (var pt : pagAssocAtivos) {
      pt.setEstado(Estado.I);
      pagTiprelEntityRepository.save(pt);
    }

    var remAtivos = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    for (var r : remAtivos) {
      r.setDataFim(LocalDate.now());
      r.setEstado(Estado.I);
      definicaoRemuneracaoEntityRepository.save(r);
    }
    var pagAtivos = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    for (var p : pagAtivos) {
      p.setDataFim(LocalDate.now());
      p.setEstado(Estado.I);
      defPagamentoEntityRepository.save(p);
    }

    var tmSalario = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("SALL", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento SALARIO não encontrado"));
    var tmInps = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("INPS", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento INPS não encontrado"));
    var tmIur = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("IUR", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento IUR não encontrado"));

    var trNovo = dadosContratuaisMapper.clone(trAtual);
    trNovo.setEstado(Estado.P);
    trNovo.setDataInicio(dc.getDataInicio());
    trNovo.setDataFim(dc.getDataFim());
    trNovo.setEstActAdm(1);
    trNovo.setTipoSituacao(dto.getTipoAlteracao());
    trNovo.setObs(dto.getTipoAlteracao());
    dadosContratuaisMapper.toUpdateRelacionamento(trNovo, dc);

    var contratoAtual = trAtual.getContratoId();
    var novoContrato = dto.getTipoAlteracao() != null && dto.getTipoAlteracao().equalsIgnoreCase("CONVERSAO_CONTRATO")
        ? contratoMapper.toContrato(dc, Estado.P)
        : null;
    if (novoContrato != null) {
      var ultimo = funcionarioRules.getContratoComMaiorVersao(funcionario);
      novoContrato.setVersao(ultimo != null && ultimo.getVersao() != null ? ultimo.getVersao() + 1 : 1);
      novoContrato.setObs(dto.getTipoAlteracao());
      novoContrato.setEstado(Estado.P);
      novoContrato.setFunId(funcionario);
      funcionario.getContratos().add(novoContrato);
      trNovo.setContratoId(novoContrato);
    } else {
      trNovo.setContratoId(contratoAtual);
    }

    var novaCarreira = carreiraMapper.toCarreira(dc, Estado.P);
    if (novaCarreira != null) {
      novaCarreira.setFunId(funcionario);
      novaCarreira.setTipoSituacao("INICIO");
      novaCarreira.setObs(dto.getTipoAlteracao());
      funcionario.getCarreiras().add(novaCarreira);
      trNovo.setCarreiraId(novaCarreira);
    }

    var novoRegime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if (novoRegime != null) {
      novoRegime.setFunId(funcionario);
      novoRegime.setTipoSituacao("INICIO");
      novoRegime.setObs(dto.getTipoAlteracao());
      funcionario.getRegimesTrabalhos().add(novoRegime);
      trNovo.setRegimeId(novoRegime);
    }

    var novaMobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    if (novaMobilidade != null) {
      novaMobilidade.setFunId(funcionario);
      novaMobilidade.setTipoSituacao("INICIO");
      novaMobilidade.setObs(dto.getTipoAlteracao());
      funcionario.getMobilidades().add(novaMobilidade);
      trNovo.setMobId(novaMobilidade);
    }

    trNovo.setFunId(funcionario);
    trNovo.setReferente("HISTORICO_LABORAL");
    funcionario.getTiposrelacionamentos().add(trNovo);

    var renumeracaoSalario = definicaoRemuneracaoMapper.createRenumeracao(
        dc.getSalario() != null ? dc.getSalario() : BigDecimal.ZERO,
        tmSalario, dc.getDataInicio(), dc.getDataFim(), funcionario);
    renumeracaoSalario.setObs(dto.getTipoAlteracao());
    funcionario.getDefinicoesRenumeracoes().add(renumeracaoSalario);

    var pagamentoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO, tmIur, dc.getDataInicio(), dc.getDataFim(),
        funcionario);
    pagamentoIUR.setObs(dto.getTipoAlteracao());
    var pagamentoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO, tmInps, dc.getDataInicio(), dc.getDataFim(),
        funcionario);
    pagamentoINPS.setObs(dto.getTipoAlteracao());
    funcionario.getDefinicoesPagamentos().addAll(List.of(pagamentoIUR, pagamentoINPS));

    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT", "HISTORICO_LABORAL", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(trNovo);
    funcionario.getValidacoes().add(valid);

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId()).ifPresent(v -> {
      var refId = novoContrato != null ? novoContrato.getId() : trNovo.getId();
      v.setReferenciaId(refId);
      validacaoEntityRepository.save(v);
    });

    var listRemTiprel = saved.getDefinicoesRenumeracoes().stream()
        .map(rem -> {
          var r = new RemuneracaoTiprelEntity();
          r.setRemId(rem);
          r.setTiprelId(trNovo);
          r.setUuid(UuidCreator.getTimeOrderedEpoch());
          r.setEstado(Estado.P);
          return r;
        }).toList();
    remuneracaoTiprelEntityRepository.saveAll(listRemTiprel);

    var listPagTiprel = saved.getDefinicoesPagamentos().stream()
        .map(pag -> {
          var p = new PagTiprelEntity();
          p.setPagId(pag);
          p.setTiprelId(trNovo);
          p.setUuid(UuidCreator.getTimeOrderedEpoch());
          p.setEstado(Estado.P);
          return p;
        }).toList();
    pagTiprelEntityRepository.saveAll(listPagTiprel);

    return dto;

  }

  /* ------------------ helpers ------------------ */

  private boolean requiresActiveContract(String tipoAlteracao) {
    if (tipoAlteracao == null)
      return false;
    String t = tipoAlteracao.toUpperCase();
    return t.contains("MOBIL") || t.contains("MUDAR_VINCULO") || t.contains("CONVERSAO_CONTRATO");
  }

  private void validarDadosContratuais(DadosContratuaisReqDTO dc) {

    if (dc.getTipoContratoId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato é obrigatório.");

    if (dc.getCargoPosicaoId() == null)
      throw IgrpResponseStatusException.badRequest("Cargo/posição é obrigatório.");

    if (dc.getDirecaoId() == null)
      throw IgrpResponseStatusException.badRequest("Direção é obrigatória.");

    if (dc.getSeccaoId() == null)
      throw IgrpResponseStatusException.badRequest("Seção é obrigatória.");

    if (dc.getLocalTrabalhoId() == null)
      throw IgrpResponseStatusException.badRequest("Local de trabalho é obrigatório.");

    if (dc.getPaisId() == null)
      throw IgrpResponseStatusException.badRequest("País é obrigatório.");

    if (dc.getIlhaId() == null)
      throw IgrpResponseStatusException.badRequest("Ilha é obrigatória.");

    if (dc.getMoeda() == null || dc.getMoeda().isBlank())
      throw IgrpResponseStatusException.badRequest("Moeda é obrigatória.");

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");

    var hoje = LocalDate.now();
    if (dc.getDataInicio().isAfter(hoje))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser maior que a data actual.");

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser superior à data fim.");

    if (dc.getTipoVinculoLaboralId() != null) {
      var vinculo = entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId());
      if (vinculo.getFlgCarreira() != null && vinculo.getFlgCarreira() == 1) {
        if (dc.getCarreiraId() == null)
          throw IgrpResponseStatusException.badRequest("Carreira é obrigatória para este tipo de vínculo.");
        if (dc.getCategoriaId() == null)
          throw IgrpResponseStatusException.badRequest("Categoria é obrigatória para este tipo de vínculo.");
        if (dc.getEscalaoReferenciaId() == null)
          throw IgrpResponseStatusException.badRequest("Escalão é obrigatório para este tipo de vínculo.");
        var escalao = entityManager.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId());
        if (escalao != null && escalao.getValor() != null)
          dc.setSalario(escalao.getValor());
      }
      if (vinculo.getFlgSalario() != null && vinculo.getFlgSalario() == 1) {
        if (dc.getSalario() == null)
          throw IgrpResponseStatusException.badRequest("Salário é obrigatório para este tipo de vínculo.");
      }
    }
  }

}
