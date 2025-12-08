package cv.inps.rh.funcionario.application.service.historicolaboral;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.ValidarHistoricoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.ValidarDadosContratuaisService;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
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
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final OrdemServicoEntityRepository ordemServicoEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public ValidarNovoHistoricoLaboralDTO validar(ValidarHistoricoLaboralCommand command) {

    var dto = command.getValidarnovohistoricolaboral();

    var tipoMobilidade = dto.getTipoAlteracao();

    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var dc = dto.getDadosContratuais();

    validarDadosContratuaisService.validar(dc);

    if (dto.getValidar() != null) {
      var relacionamentoPendente = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario,
          Estado.P);
      if (relacionamentoPendente == null) {
        throw IgrpResponseStatusException.badRequest("Não existe histórico laboral pendente para validar");
      }

      var estadoFinal = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

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

      var remTiprels = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(relacionamentoPendente, Estado.P);
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
          Estado.P, "MOBILIDADE");
      if (validacao != null) {
        validacao.setEstado(estadoFinal);
        validacaoEntityRepository.save(validacao);
      }

      if (Estado.A.equals(estadoFinal)) {
        var os = new OrdemServicoEntity();
        os.setNuOrdem("1");
        os.setDescricao((tipoMobilidade != null ? tipoMobilidade : "MOBILIDADE") + " - "
            + (funcionario.getNome() != null ? funcionario.getNome() : ""));
        os.setReferente("MOBILIDADE");
        os.setFunId(funcionario);
        os.setTiprelId(relacionamentoPendente);
        os.setEstado(Estado.A);
        funcionario.getOrdemServicos().add(os);
      }

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var pendente = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    if (pendente != null) {
      throw IgrpResponseStatusException
          .badRequest("Existe histórico laboral pendente por validar. Valide antes de criar novo.");
    }

    if (tipoMobilidade != null && requiresActiveContract(tipoMobilidade)) {
      var trAtualContrato = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      if (trAtualContrato == null || trAtualContrato.getContrVinculoId() == null
          || trAtualContrato.getContrVinculoId().getEstado() != Estado.A) {
        throw IgrpResponseStatusException.badRequest("Não existe contrato activo associado ao colaborador");
      }
    }

    var trAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (trAtual == null) {
      throw IgrpResponseStatusException.notFound("Relacionamento atual não encontrado");
    }

    trAtual.setDataFim(LocalDate.now());
    trAtual.setEstActAdm(0);
    trAtual.setEstado(Estado.I);
    tiposRelacionamentoEntityRepository.save(trAtual);

    var remAssocAtivos = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(trAtual, Estado.A);
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

    var trNovo = dadosContratuaisMapper.clone(trAtual);
    trNovo.setEstado(Estado.P);
    trNovo.setDataInicio(dc.getDataInicio());
    trNovo.setDataFim(dc.getDataFim());
    trNovo.setEstActAdm(1);
    trNovo.setTipoSituacao("MOBILIDADE");
    trNovo.setObs(tipoMobilidade);
    dadosContratuaisMapper.toUpdateRelacionamento(trNovo, dc);
    trNovo.setReferente("MOBILIDADE");

    var contratoAtual = trAtual.getContrVinculoId();
    ContratoEntity novoContrato = null;
    if (tipoMobilidade != null && tipoMobilidade.equalsIgnoreCase("CONVERSAO_CONTRATO")) {
      novoContrato = contratoMapper.toContrato(dc, Estado.P);
      if (novoContrato != null) {
        var ultimo = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());
        novoContrato.setVersao(ultimo != null && ultimo.getVersao() != null ? ultimo.getVersao() + 1 : 1);
        novoContrato.setObs(tipoMobilidade);
        novoContrato.setEstado(Estado.P);
        novoContrato.setFunId(funcionario);
        funcionario.getContratos().add(novoContrato);
        trNovo.setContrVinculoId(novoContrato);
      }
    } else {
      trNovo.setContrVinculoId(contratoAtual);
    }

    var regimeMudou = (trAtual.getRegime() == null && dc.getRegimeTrabalho() != null)
        || (trAtual.getRegime() != null && !trAtual.getRegime().equals(dc.getRegimeTrabalho()));
    var novoRegime = regimeMudou ? regimeTrabalhoMapper.toRegime(dc, Estado.P) : null;
    if (novoRegime != null) {
      novoRegime.setFunId(funcionario);
      novoRegime.setTipoSituacao("INICIO");
      novoRegime.setObs(tipoMobilidade);
      funcionario.getRegimesTrabalhos().add(novoRegime);
      trNovo.setRegimeId(novoRegime);
    }

    var direcaoMudou = trAtual.getInstitId() == null
        || !java.util.Objects.equals(trAtual.getInstitId().getId(), dc.getDirecaoId());
    var seccaoMudou = trAtual.getSeccaoId() == null
        || !java.util.Objects.equals(trAtual.getSeccaoId().getId(), dc.getSeccaoId());
    var localMudou = trAtual.getLocTrabId() == null
        || !java.util.Objects.equals(trAtual.getLocTrabId().getId(), dc.getLocalTrabalhoId());
    var novaMobilidade = (direcaoMudou || seccaoMudou || localMudou) ? mobilidadeMapper.toMobilidade(dc, Estado.P)
        : null;
    if (novaMobilidade != null) {
      novaMobilidade.setTipoSituacao("MOBILIDADE");
      novaMobilidade.setObs("MOBILIDADE");
      funcionario.getMobilidades().add(novaMobilidade);
      trNovo.setMobId(novaMobilidade);
    }

    funcionario.getTiposrelacionamentos().add(trNovo);

    var tmSalario = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("SALL", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento SALARIO não encontrado"));
    var tmInps = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("INPS", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento INPS não encontrado"));
    var tmIur = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("IUR", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento IUR não encontrado"));

    if (dc.getSalario() != null) {
      var renumeracaoSalario = definicaoRemuneracaoMapper.createRenumeracao(dc.getSalario(), tmSalario,
          dc.getDataInicio(), dc.getDataFim(), funcionario, dc.getMoeda());
      funcionario.getDefinicoesRenumeracoes().add(renumeracaoSalario);
    }

    var pagamentosAtivos = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    boolean temIurAtivo = pagamentosAtivos.stream()
        .anyMatch(p -> p.getTmId() != null && "IUR".equalsIgnoreCase(p.getTmId().getShortDesc()));
    boolean temInpsAtivo = pagamentosAtivos.stream()
        .anyMatch(p -> p.getTmId() != null && "INPS".equalsIgnoreCase(p.getTmId().getShortDesc()));
    if (!temIurAtivo) {
      var pagamentoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO, tmIur, dc.getDataInicio(), dc.getDataFim(),
          funcionario);
      funcionario.getDefinicoesPagamentos().add(pagamentoIUR);
    }
    if (!temInpsAtivo) {
      var pagamentoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO, tmInps, dc.getDataInicio(),
          dc.getDataFim(), funcionario);
      funcionario.getDefinicoesPagamentos().add(pagamentoINPS);
    }

    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT", "MOBILIDADE", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(trNovo);
    funcionario.getValidacoes().add(valid);

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId()).ifPresent(v -> {
      var refId = novaMobilidade != null ? novaMobilidade.getId() : trNovo.getId();
      v.setReferenciaId(refId);
      validacaoEntityRepository.save(v);
    });

    var listRemTiprel = saved.getDefinicoesRenumeracoes().stream()
        .filter(rem -> rem.getEstado() == Estado.P)
        .map(rem -> {
          var r = new RemuneracaoTiprelEntity();
          r.setRemId(rem);
          r.setTiprelId(trNovo);
          r.setEstado(Estado.P);
          return r;
        }).toList();
    remuneracaoTiprelEntityRepository.saveAll(listRemTiprel);

    var listPagTiprel = saved.getDefinicoesPagamentos().stream()
        .filter(pag -> pag.getEstado() == Estado.P)
        .map(pag -> {
          var p = new PagTiprelEntity();
          p.setPagId(pag);
          p.setTiprelId(trNovo);
          p.setEstado(Estado.P);
          return p;
        }).toList();
    pagTiprelEntityRepository.saveAll(listPagTiprel);

    var carreiraMudou = trAtual.getCarrPccId() == null
        || !java.util.Objects.equals(trAtual.getCarrPccId().getId(), dc.getCarreiraId())
        || trAtual.getCategoriaId() == null
        || !java.util.Objects.equals(trAtual.getCategoriaId().getId(), dc.getCategoriaId())
        || trAtual.getEscalaoId() == null
        || !java.util.Objects.equals(trAtual.getEscalaoId().getId(), dc.getEscalaoReferenciaId());
    var novaCarreira = carreiraMudou ? carreiraMapper.toCarreira(dc, Estado.P) : null;
    if (novaCarreira != null) {
      novaCarreira.setContrVinculoId(novoContrato != null ? novoContrato : contratoAtual);
      carreiraEntityRepository.save(novaCarreira);
      trNovo.setCarreiraId(novaCarreira);
      tiposRelacionamentoEntityRepository.save(trNovo);
    }

    var situacaoAtual = trAtual.getSituacLaboralId();
    var precisaSituacaoAtivo = situacaoAtual == null || situacaoAtual.getSituacaoLaboralId() == null
        || situacaoAtual.getSituacaoLaboralId().getNome() == null
        || !situacaoAtual.getSituacaoLaboralId().getNome().equalsIgnoreCase("ATIVO");
    if (precisaSituacaoAtivo) {
      var lista = paramSitLaboralEntityRepository.findAllByNome("ATIVO");
      if (lista != null && !lista.isEmpty()) {
        var paramAtivo = lista.get(0);
        var sl = dadosContratuaisMapper.toSituacaoLaboral(dc, paramAtivo, Estado.P, "ATIVO", "MOBILIDADE");
        sl.setContrVinculoId(novoContrato != null ? novoContrato : contratoAtual);
        situacaoLaboralEntityRepository.save(sl);
        trNovo.setSituacLaboralId(sl);
        tiposRelacionamentoEntityRepository.save(trNovo);
      }
    }

    return dto;
  }

  private boolean requiresActiveContract(String tipoAlteracao) {
    if (tipoAlteracao == null)
      return false;
    var t = tipoAlteracao.trim().toUpperCase();
    return List.of("MOBILIDADE", "CONVERSAO_CONTRATO").contains(t);
  }

}
