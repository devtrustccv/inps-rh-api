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
   * Associa ao novo tipo relacionamento APENAS a lista de remunerações/pagamentos indicada,
   * SEM copiar as associações do tiprel anterior.
   *
   * <p>Usar quando a inactivação/derivação dos movimentos antigos é DEFERIDA para a validação
   * (ex.: novo contrato): copiar as ativas do tiprel anterior traria o salário antigo (ainda
   * activo na criação), que o {@code reconciliar} inactiva no validar — deixando uma associação
   * órfã em RH_T_TIPREL_REM_PAG. Aqui associam-se só os novos pendentes (subsídios/encargos do
   * DTO); o salário certo é ligado no validar via reconciliar + associarNovos.</p>
   */
  public void associarLista(
      TiposRelacionamentoEntity novoTipoRel,
      List<DefinicaoRemuneracaoEntity> remuneracoes,
      List<DefPagamentoEntity> pagamentos) {

    List<TipoRelRemPagEntity> lista = new ArrayList<>();
    Set<Long> remIds = new HashSet<>();
    Set<Long> pagIds = new HashSet<>();

    if (!CollectionUtils.isEmpty(remuneracoes)) {
      for (var rem : remuneracoes) {
        if (rem == null || rem.getId() == null) continue;
        if (!remIds.add(rem.getId())) continue;
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(novoTipoRel);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
    }

    if (!CollectionUtils.isEmpty(pagamentos)) {
      for (var pag : pagamentos) {
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

  /**
   * Ao trocar de tipo relacionamento (est_adm anterior = 0, novo = 1), copia para o novo os
   * movimentos do tipo anterior que estão em estado A e AINDA EM VIGOR (não terminados: DATA_FIM
   * nula ou >= hoje) e também associa os itens novos criados na mesma operação (estado P). A
   * dedup por ID garante que itens transferidos e novos não se sobrepõem.
   *
   * <p>Filtro "não-terminado" (todos os fluxos de cópia — mobilidade/carreira/histórico/renovação):
   * um movimento já expirado (ex.: diferença de substituição de mês passado, subsídio manual cuja
   * DATA_FIM já passou) NÃO transita para o novo relacionamento.</p>
   */
  public void transferirParaNovoTipoRelacionamento(
      TiposRelacionamentoEntity tipoRelAtual,
      TiposRelacionamentoEntity novoTipoRel,
      List<DefinicaoRemuneracaoEntity> novasRemuneracoes,
      List<DefPagamentoEntity> novosPagamentos) {

    List<TipoRelRemPagEntity> lista = new ArrayList<>();
    Set<Long> remIds = new HashSet<>();
    Set<Long> pagIds = new HashSet<>();
    var hoje = java.time.LocalDate.now();

    var remuneracoesAtivas = funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId());
    if (!CollectionUtils.isEmpty(remuneracoesAtivas)) {
      for (var rem : remuneracoesAtivas) {
        if (rem == null || rem.getId() == null) continue;
        if (rem.getDataFim() != null && rem.getDataFim().isBefore(hoje)) continue; // expirado → não transita
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
        if (pag.getDataFim() != null && pag.getDataFim().isBefore(hoje)) continue; // expirado → não transita
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