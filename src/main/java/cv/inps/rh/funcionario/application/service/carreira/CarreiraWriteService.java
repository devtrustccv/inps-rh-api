package cv.inps.rh.funcionario.application.service.carreira;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoCarreiraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CarreiraWriteService {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final CarreiraMapper carreiraMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;

  public void novaCarreira(String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    if (carreiraEntityRepository.existsByContrVinculoIdFunIdAndEstado(funcionario, Estado.P))
      throw IgrpResponseStatusException.conflict("Existe um registo de carreira por validar!");

    if (!contratoEntityRepository.existsByFunIdAndEstado(funcionario, Estado.A))
      throw IgrpResponseStatusException.conflict("Este funcionário não possui um contrato ativo");

    var currentDate = LocalDate.now();
    var currentDateMinusOneDay = currentDate.minusDays(1);

    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());

    var relacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    relacionamentoAtual.setDataFim(currentDateMinusOneDay);
    relacionamentoAtual.setEstActAdm(0);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defRemuneracao.forEach(obj -> {
      obj.setDataFim(currentDateMinusOneDay);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defPagamento.forEach(obj -> {
      obj.setDataFim(currentDateMinusOneDay);
      defPagamentoEntityRepository.save(obj);
    });

    var carreiraAtual = relacionamentoAtual.getCarreiraId();
    carreiraAtual.setDataFim(currentDate);
    carreiraEntityRepository.save(carreiraAtual);

    var novaCarreira = Objects.requireNonNull(carreiraMapper.toCarreira(dto, Estado.P));
    novaCarreira.setObs("CARREIRA");
    carreiraEntityRepository.save(novaCarreira);

    var novoRelacionamento = contratuaisEntityMapper.toRelacionamento(dto, Estado.P);
    novoRelacionamento.setObs("MOBILIDADE- || TIPO_CARREIRA");
    novoRelacionamento.setDataInicio(currentDate);
    novoRelacionamento.setContrVinculoId(contratoAtual);
    novoRelacionamento.setCarreiraId(novaCarreira);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoRelacionamento);

    var novasRemuneracoes = new ArrayList<DefinicaoRemuneracaoEntity>();
    var novosPagamentos = new ArrayList<DefPagamentoEntity>();
    if (dto.getSubsidios() != null && !dto.getSubsidios().isEmpty()) {
      var remList = dto.getSubsidios().stream()
          .map(s -> {
            var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
            obj.setObs("MOBILIDADE- || TIPO_CARREIRA");
            return obj;
          })
          .toList();
      novasRemuneracoes.addAll(remList);
      funcionario.setDefinicoesRenumeracoes(remList);
    }

    var vinculoAtualId = contratoAtual.getVinculoId() != null ? contratoAtual.getVinculoId().getId() : null;
    var escalaoAtualId = relacionamentoAtual.getCarreiraId() != null ? relacionamentoAtual.getCarreiraId().getEscalaoId().getId() : null;

    var criarNovoSalario = houveMudancaSalario(vinculoAtualId, escalaoAtualId, dto, funcionario);
    if (criarNovoSalario) {
      var vinculoTipoMovimentoREMList = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(dto.getTipoVinculoLaboralId(), "REM");
      var vinculoTipoMovimentoREM = vinculoTipoMovimentoREMList.stream().findFirst().orElse(null);
      if (vinculoTipoMovimentoREM != null) {
        var salario = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario);
        salario.setTmId(vinculoTipoMovimentoREM.getTmId());
        novasRemuneracoes.add(salario);
        funcionario.getDefinicoesRenumeracoes().add(salario);
      }
    }

    if (dto.getEncargosDescontos() != null && !dto.getEncargosDescontos().isEmpty()) {
      var pagList = dto.getEncargosDescontos().stream()
          .map(e -> {
            var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
            def.setObs("MOBILIDADE- || TIPO_CARREIRA");
            return def;
          })
          .toList();
      novosPagamentos.addAll(pagList);
      funcionario.setDefinicoesPagamentos(pagList);
    }

    if (!Objects.equals(vinculoAtualId, dto.getTipoVinculoLaboralId())) {
      var listAssociacaoVinculoTipoMovimentoPag =
          paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(
              dto.getTipoVinculoLaboralId(), "PAG");
      if (!CollectionUtils.isEmpty(listAssociacaoVinculoTipoMovimentoPag)) {
        listAssociacaoVinculoTipoMovimentoPag.forEach(movimento -> {
          var pagamento = defPagamentoMapper.createPagamento(
              BigDecimal.ZERO,
              movimento.getTmId(),
              dto.getDataInicio(),
              dto.getDataFim(),
              funcionario);
          novosPagamentos.add(pagamento);
          funcionario.getDefinicoesPagamentos().add(pagamento);
        });
      }
    }

    var saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    var listAssociacoes = new ArrayList<TipoRelRemPagEntity>();
    var renumeracoesAtivasAntigas = funcionarioRules.getRemuneracoesAssociadosAtivos(relacionamentoAtual.getId());
    var pagamentosAtivosAntigos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(relacionamentoAtual.getId());

    var remIdsAdicionados = new java.util.HashSet<Long>();
    var pagIdsAdicionados = new java.util.HashSet<Long>();

    if (renumeracoesAtivasAntigas != null) {
      for (var rem : renumeracoesAtivasAntigas) {
        if (rem != null && rem.getId() != null && remIdsAdicionados.add(rem.getId())) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(novoRelacionamento);
          assoc.setRemId(rem);
          assoc.setPagId(null);
          listAssociacoes.add(assoc);
        }
      }
    }
    if (pagamentosAtivosAntigos != null) {
      for (var pag : pagamentosAtivosAntigos) {
        if (pag != null && pag.getId() != null && pagIdsAdicionados.add(pag.getId())) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(novoRelacionamento);
          assoc.setPagId(pag);
          assoc.setRemId(null);
          listAssociacoes.add(assoc);
        }
      }
    }

    if (!novasRemuneracoes.isEmpty()) {
      for (var rem : novasRemuneracoes) {
        if (rem != null && rem.getId() != null && remIdsAdicionados.add(rem.getId())) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(novoRelacionamento);
          assoc.setRemId(rem);
          assoc.setPagId(null);
          listAssociacoes.add(assoc);
        }
      }
    }
    if (!novosPagamentos.isEmpty()) {
      for (var pag : novosPagamentos) {
        if (pag != null && pag.getId() != null && pagIdsAdicionados.add(pag.getId())) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTiprelId(novoRelacionamento);
          assoc.setPagId(pag);
          assoc.setRemId(null);
          listAssociacoes.add(assoc);
        }
      }
    }

    if (!listAssociacoes.isEmpty()) {
      tipoRelRemPagEntityRepository.saveAll(listAssociacoes);
    }

    var mobilidade = mobilidadeEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao("INSERT");
    validation.setReferenciaName("CARREIRA");
    validation.setReferenciaId(mobilidade.getId());
    validation.setTiprelId(novoRelacionamento);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  @NotNull
  private DefinicaoRemuneracaoEntity getSalarioDefinicaoRemuneracaoEntity(DadosContratuaisReqDTO dto, FuncionarioEntity funcionario) {
    var salario = new DefinicaoRemuneracaoEntity();
    salario.setValor(dto.getSalario());
    salario.setEstado(Estado.P);
    salario.setObs("MOBILIDADE- || TIPO_CARREIRA");
    salario.setDataInicio(dto.getDataInicio());
    salario.setDataFim(dto.getDataFim());
    salario.setFunId(funcionario);
    salario.setUuid(UuidCreator.getTimeOrderedEpoch());
    return salario;
  }

  public void validarCarreira(String funcionarioId, ValidacaoCarreiraDTO dto) {

    ValidationUtil.validateDecision(dto.getValidacao());

    // TODO 30/11/2025 11:04 check this
    var dados = dto.getDados();

    var estado = dto.getValidacao().equals("S") ? Estado.A : Estado.I;

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var carreira = carreiraEntityRepository.findByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    carreira.setEstado(estado);
    carreiraEntityRepository.save(carreira);

    var relacionamento = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    relacionamento.setEstado(estado);
    tiposRelacionamentoEntityRepository.save(relacionamento);

    var definicoesRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesRemuneracao.forEach(obj -> {
      obj.setEstado(estado);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var definicoesPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesPagamento.forEach(obj -> {
      obj.setEstado(estado);
      defPagamentoEntityRepository.save(obj);
    });

    var validation = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamento, Estado.P, "CARREIRA");
    validation.setEstado(estado);
    validacaoEntityRepository.save(validation);
  }

  public void eliminarCareira(String carreiraId) {

    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));
    if (!Estado.P.equals(carreira.getEstado()))
      throw IgrpResponseStatusException.badRequest("Esta carreira não se encontra no estado pendente");

    var funcionario = Objects.requireNonNull(carreira.getContrVinculoId()).getFunId();

    carreira.setEstado(Estado.E);
    carreiraEntityRepository.save(carreira);

    var relacionamentoAtual = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    defRemuneracao.forEach(obj -> {
      obj.setEstado(Estado.E);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    defPagamento.forEach(obj -> {
      obj.setEstado(Estado.E);
      defPagamentoEntityRepository.save(obj);
    });

    definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P).
        forEach(obj -> {
          obj.setEstado(Estado.E);
          definicaoRemuneracaoEntityRepository.save(obj);
        });

    var validation = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamentoAtual, Estado.P, "CARREIRA");
    validation.setEstado(Estado.E);
    validacaoEntityRepository.save(validation);
  }

  public void atualizarCarreira(String carreiraId, String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));

    if (carreira.getContrVinculoId().getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Carreira não pertence a este funcionário");

    if (!carreira.getEstado().equals(Estado.P))
      throw IgrpResponseStatusException.badRequest("Carreira só pode ser atualiza no estado pendente");

    if (!CollectionUtils.isEmpty(dto.getSubsidios())) {
      var remList = dto.getSubsidios().stream()
          .map(s -> {
            DefinicaoRemuneracaoEntity obj;
            if (s.getId() == null) {
              obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
              obj.setObs("MOBILIDADE- || TIPO_CARREIRA");
            } else {
              obj = definicaoRemuneracaoEntityRepository.findByIdOrThrow(s.getId());
              obj.setPercentagem(s.getPercentagem());
              obj.setValor(s.getValor());
              obj.setObs(s.getObservacoes());
              if (s.getTipoSubsidioId() != null)
                obj.setTmId(entityManager.getReference(TipoMovimentoEntity.class, s.getTipoSubsidioId()));
            }
            return obj;
          })
          .toList();
      definicaoRemuneracaoEntityRepository.saveAll(remList);
    }

    if (CollectionUtils.isEmpty(dto.getEncargosDescontos()))
      return;

    var pagList = dto.getEncargosDescontos().stream()
        .map(e -> {
          DefPagamentoEntity obj;
          if (e.getId() == null) {
            obj = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
            obj.setObs("MOBILIDADE- || TIPO_CARREIRA");
          } else {
            obj = defPagamentoEntityRepository.findByIdOrThrow(e.getId());
            obj.setValor(e.getValor());
            obj.setObs(e.getObservacoes());
            if (e.getTipoEncargoId() != null)
              obj.setTmId(entityManager.getReference(TipoMovimentoEntity.class, e.getTipoEncargoId()));
          }
          return obj;
        })
        .toList();
    defPagamentoEntityRepository.saveAll(pagList);
  }

  private boolean houveMudancaSalario(Long vinculoId, Long escalaoId, DadosContratuaisReqDTO dc, FuncionarioEntity funcionario) {
    if (escalaoId != null && escalaoId > 0) {
      if (!Objects.equals(escalaoId, dc.getEscalaoReferenciaId())) {
        return true;
      }
    }
    var vinculoTipoMovimentoREMList = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoId, "REM");
    var tmRem = vinculoTipoMovimentoREMList.stream()
        .findFirst()
        .map(ParamVinculoMovimentoEntity::getTmId)
        .orElse(null);
    if (tmRem == null) return true;
    var renumeracoes = definicaoRemuneracaoEntityRepository
        .findByFunIdAndTmIdAndEstado(funcionario, tmRem, Estado.A);
    var renumeracao = renumeracoes.stream().findFirst().orElse(null);
    if (renumeracao != null) {
      if (!Objects.equals(renumeracao.getValor(), dc.getSalario())) {
        return true;
      }
    }
    return false;
  }
}
