package cv.inps.rh.funcionario.application.service.helper;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TipoRelRemPagHelper {

  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final FuncionarioRules funcionarioRules;

  /**
   * Associa ao tipo relacionamento todas as remunerações e pagamentos do funcionário
   * com estado A ou P, ignorando duplicatas já existentes na tabela.
   * Usar em registos iniciais e em validações.
   */
  public void associarNovos(TiposRelacionamentoEntity tipoRel, FuncionarioEntity saved) {
    List<TipoRelRemPagEntity> lista = new ArrayList<>();
    Set<Long> remIds = new HashSet<>();
    Set<Long> pagIds = new HashSet<>();

    if (!CollectionUtils.isEmpty(saved.getDefinicoesRenumeracoes())) {
      for (var rem : saved.getDefinicoesRenumeracoes()) {
        if (rem.getEstado() != Estado.A && rem.getEstado() != Estado.P) continue;
        if (!remIds.add(rem.getId())) continue;
        if (tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tipoRel, rem)) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tipoRel);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
    }

    if (!CollectionUtils.isEmpty(saved.getDefinicoesPagamentos())) {
      for (var pag : saved.getDefinicoesPagamentos()) {
        if (pag.getEstado() != Estado.A && pag.getEstado() != Estado.P) continue;
        if (!pagIds.add(pag.getId())) continue;
        if (tipoRelRemPagEntityRepository.existsByTiprelIdAndPagId(tipoRel, pag)) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tipoRel);
        assoc.setPagId(pag);
        assoc.setRemId(null);
        lista.add(assoc);
      }
    }

    if (!lista.isEmpty()) {
      tipoRelRemPagEntityRepository.saveAll(lista);
    }
  }

  /**
   * Ao trocar de tipo relacionamento (est_adm anterior = 0, novo = 1), copia as
   * remunerações e pagamentos ativos associados ao tipo anterior para o novo e
   * também associa os itens novos criados na mesma operação (estado P).
   * A deduplicação por ID garante que itens transferidos e novos não se sobrepõem.
   */
  public void transferirParaNovoTipoRelacionamento(
      TiposRelacionamentoEntity tipoRelAtual,
      TiposRelacionamentoEntity novoTipoRel,
      List<DefinicaoRemuneracaoEntity> novasRemuneracoes,
      List<DefPagamentoEntity> novosPagamentos) {

    List<TipoRelRemPagEntity> lista = new ArrayList<>();
    Set<Long> remIds = new HashSet<>();
    Set<Long> pagIds = new HashSet<>();

    var remuneracoesAtivas = funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId());
    if (!CollectionUtils.isEmpty(remuneracoesAtivas)) {
      for (var rem : remuneracoesAtivas) {
        if (rem == null || rem.getId() == null) continue;
        if (!remIds.add(rem.getId())) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(novoTipoRel);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
    }

    var pagamentosAtivos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId());
    if (!CollectionUtils.isEmpty(pagamentosAtivos)) {
      for (var pag : pagamentosAtivos) {
        if (pag == null || pag.getId() == null) continue;
        if (!pagIds.add(pag.getId())) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(novoTipoRel);
        assoc.setPagId(pag);
        assoc.setRemId(null);
        lista.add(assoc);
      }
    }

    if (!CollectionUtils.isEmpty(novasRemuneracoes)) {
      for (var rem : novasRemuneracoes) {
        if (rem == null || rem.getId() == null) continue;
        if (!remIds.add(rem.getId())) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(novoTipoRel);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
    }

    if (!CollectionUtils.isEmpty(novosPagamentos)) {
      for (var pag : novosPagamentos) {
        if (pag == null || pag.getId() == null) continue;
        if (!pagIds.add(pag.getId())) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(novoTipoRel);
        assoc.setPagId(pag);
        assoc.setRemId(null);
        lista.add(assoc);
      }
    }

    if (!lista.isEmpty()) {
      tipoRelRemPagEntityRepository.saveAll(lista);
    }
  }
}