package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reconcilia os movimentos FIXOS do vinculo (REM salario + PAG encargos/descontos) com o estado
 * do funcionario. Chamado na VALIDACAO POSITIVA (nao na criacao). Idempotente (guard estado A/P):
 * nao duplica em re-validacao/edicao. Os extras do utilizador (subsidios/encargos manuais) NAO sao
 * tocados aqui — sao tratados pelos syncRemuneracoes/syncPagamentos.
 *
 * <p>Reutilizavel por qualquer fluxo de validacao que fixe movimentos por vinculo
 * (validar novo contrato, validar registo de colaborador).</p>
 */
@Service
@RequiredArgsConstructor
public class ReconciliacaoMovimentoVinculoService {

  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;

  /**
   * @param contrato contrato a validar; o vinculo NOVO vem de {@code contrato.getVinculoId()} e o
   *                 vinculo ANTERIOR (para inactivar PAG obsoletos) de {@code contrato.getContratoId()}
   *                 (contrato-pai; null no primeiro contrato).
   */
  public void reconciliar(FuncionarioEntity funcionario, ContratoEntity contrato,
                          BigDecimal salario, String moeda, LocalDate dataInicio, LocalDate dataFim) {
    if (contrato == null || contrato.getVinculoId() == null) {
      return;
    }
    var vinculoNovoId = contrato.getVinculoId().getId();
    Long vinculoAntigoId = (contrato.getContratoId() != null && contrato.getContratoId().getVinculoId() != null)
        ? contrato.getContratoId().getVinculoId().getId() : null;

    reconciliarRemuneracao(funcionario, vinculoNovoId, vinculoAntigoId, salario, moeda, dataInicio, dataFim);
    reconciliarPagamentos(funcionario, vinculoNovoId, vinculoAntigoId, dataInicio, dataFim);
  }

  private void reconciliarRemuneracao(FuncionarioEntity funcionario, Long vinculoNovoId, Long vinculoAntigoId,
                                      BigDecimal salario, String moeda, LocalDate dataInicio, LocalDate dataFim) {
    var remMovs = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoNovoId, "REM", Estado.A);
    if (CollectionUtils.isEmpty(remMovs) || remMovs.getFirst().getTmId() == null) {
      return;
    }
    var remTm = remMovs.getFirst().getTmId();
    if (funcionario.getDefinicoesRenumeracoes() == null) {
      funcionario.setDefinicoesRenumeracoes(new ArrayList<>());
    }
    // mudanca de vinculo: se a REM-base do vinculo ANTIGO usa um tm diferente, inactiva-la
    // (senao ficariam dois salarios-base activos)
    if (vinculoAntigoId != null && !Objects.equals(vinculoAntigoId, vinculoNovoId)) {
      var antigoRemMovs = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoAntigoId, "REM", Estado.A);
      if (!CollectionUtils.isEmpty(antigoRemMovs) && antigoRemMovs.getFirst().getTmId() != null) {
        Long antigoRemTmId = antigoRemMovs.getFirst().getTmId().getId();
        if (!Objects.equals(antigoRemTmId, remTm.getId())) {
          funcionario.getDefinicoesRenumeracoes().stream()
              .filter(r -> ativoOuPendente(r.getEstado()) && r.getTmId() != null
                  && Objects.equals(antigoRemTmId, r.getTmId().getId()))
              .forEach(r -> r.setEstado(Estado.I));
        }
      }
    }
    var existentes = funcionario.getDefinicoesRenumeracoes().stream()
        .filter(r -> ativoOuPendente(r.getEstado()) && r.getTmId() != null
            && Objects.equals(remTm.getId(), r.getTmId().getId()))
        .toList();
    boolean jaTemValorCerto = existentes.stream().anyMatch(r -> Objects.equals(r.getValor(), salario));
    if (!jaTemValorCerto) {
      existentes.forEach(r -> r.setEstado(Estado.I));
      var nova = definicaoRemuneracaoMapper.createRenumeracao(salario, remTm, dataInicio, dataFim, funcionario, moeda);
      nova.setEstado(Estado.A); // reconciliar so corre em validacao positiva → fixo fica logo activo
      funcionario.getDefinicoesRenumeracoes().add(nova);
    }
  }

  private void reconciliarPagamentos(FuncionarioEntity funcionario, Long vinculoNovoId, Long vinculoAntigoId,
                                     LocalDate dataInicio, LocalDate dataFim) {
    var novosPagMovs = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoNovoId, "PAG", Estado.A);
    Set<Long> novosPagTms = novosPagMovs.stream()
        .filter(m -> m.getTmId() != null).map(m -> m.getTmId().getId()).collect(Collectors.toSet());

    Set<Long> antigosPagTms = vinculoAntigoId == null ? Set.of()
        : paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoAntigoId, "PAG", Estado.A).stream()
            .filter(m -> m.getTmId() != null).map(m -> m.getTmId().getId()).collect(Collectors.toSet());

    if (funcionario.getDefinicoesPagamentos() == null) {
      funcionario.setDefinicoesPagamentos(new ArrayList<>());
    }
    // inactivar os do vinculo antigo que nao estao no novo
    funcionario.getDefinicoesPagamentos().stream()
        .filter(p -> ativoOuPendente(p.getEstado()) && p.getTmId() != null
            && antigosPagTms.contains(p.getTmId().getId()) && !novosPagTms.contains(p.getTmId().getId()))
        .forEach(p -> p.setEstado(Estado.I));
    // criar os do novo vinculo que ainda nao existem (activo/pendente)
    Set<Long> existentesPagTms = funcionario.getDefinicoesPagamentos().stream()
        .filter(p -> ativoOuPendente(p.getEstado()) && p.getTmId() != null)
        .map(p -> p.getTmId().getId()).collect(Collectors.toSet());
    novosPagMovs.forEach(m -> {
      if (m.getTmId() != null && !existentesPagTms.contains(m.getTmId().getId())) {
        var novo = defPagamentoMapper.createPagamento(
            m.getValor(),
            m.getPercentagem() != null ? BigDecimal.valueOf(m.getPercentagem()) : null,
            m.getTmId(), dataInicio, dataFim, funcionario);
        novo.setEstado(Estado.A); // reconciliar so corre em validacao positiva → fixo fica logo activo
        funcionario.getDefinicoesPagamentos().add(novo);
      }
    });
  }

  private boolean ativoOuPendente(Estado estado) {
    return estado == Estado.A || estado == Estado.P;
  }

  /**
   * Tms dos movimentos FIXOS do vínculo (tipo "REM" = salário; "PAG" = INPS/IUR/valor líquido).
   * Usado para PROTEGER estes movimentos nos syncs de subsídios/encargos (que só devem mexer
   * nos manuais). Devolve conjunto vazio se o vínculo for nulo.
   */
  public Set<Long> tmsFixosDoVinculo(Long vinculoId, String tipo) {
    if (vinculoId == null) return Set.of();
    return paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoId, tipo, Estado.A).stream()
        .filter(m -> m.getTmId() != null)
        .map(m -> m.getTmId().getId())
        .collect(Collectors.toSet());
  }
}
