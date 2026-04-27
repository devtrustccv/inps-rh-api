package cv.inps.rh.funcionario.application.service.helper;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoRelRemPagHelperTest {

  @Mock
  private TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  @Mock
  private FuncionarioRules funcionarioRules;

  @InjectMocks
  private TipoRelRemPagHelper helper;

  private TiposRelacionamentoEntity tipoRel;

  @BeforeEach
  void setUp() {
    tipoRel = new TiposRelacionamentoEntity();
    tipoRel.setId(1L);
  }

  // ─────────────────────────────────────────────────────────────
  // Helpers de fábrica
  // ─────────────────────────────────────────────────────────────

  private DefinicaoRemuneracaoEntity rem(Long id, Estado estado) {
    var r = new DefinicaoRemuneracaoEntity();
    r.setId(id);
    r.setEstado(estado);
    return r;
  }

  private DefPagamentoEntity pag(Long id, Estado estado) {
    var p = new DefPagamentoEntity();
    p.setId(id);
    p.setEstado(estado);
    return p;
  }

  private FuncionarioEntity funcionarioWith(List<DefinicaoRemuneracaoEntity> rems,
                                             List<DefPagamentoEntity> pags) {
    var f = new FuncionarioEntity();
    f.setDefinicoesRenumeracoes(rems);
    f.setDefinicoesPagamentos(pags);
    return f;
  }

  // ─────────────────────────────────────────────────────────────
  // associarNovos
  // ─────────────────────────────────────────────────────────────

  @Nested
  class AssociarNovos {

    @Test
    void associaRemuneracaoAtiva() {
      var r = rem(10L, Estado.A);
      var saved = funcionarioWith(List.of(r), List.of());

      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tipoRel, r)).thenReturn(false);

      helper.associarNovos(tipoRel, saved);

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      List<TipoRelRemPagEntity> salvo = captor.getValue();
      assertThat(salvo).hasSize(1);
      assertThat(salvo.getFirst().getRemId()).isEqualTo(r);
      assertThat(salvo.getFirst().getPagId()).isNull();
    }

    @Test
    void associaPagamentoPendente() {
      var p = pag(20L, Estado.P);
      var saved = funcionarioWith(List.of(), List.of(p));

      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndPagId(tipoRel, p)).thenReturn(false);

      helper.associarNovos(tipoRel, saved);

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      List<TipoRelRemPagEntity> salvo = captor.getValue();
      assertThat(salvo).hasSize(1);
      assertThat(salvo.getFirst().getPagId()).isEqualTo(p);
      assertThat(salvo.getFirst().getRemId()).isNull();
    }

    @Test
    void ignoraEstadoInativo() {
      var r = rem(10L, Estado.I);
      var saved = funcionarioWith(List.of(r), List.of());

      helper.associarNovos(tipoRel, saved);

      verify(tipoRelRemPagEntityRepository, never()).saveAll(any());
    }

    @Test
    void ignoraAssociacaoJaExistente() {
      var r = rem(10L, Estado.A);
      var saved = funcionarioWith(List.of(r), List.of());

      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tipoRel, r)).thenReturn(true);

      helper.associarNovos(tipoRel, saved);

      verify(tipoRelRemPagEntityRepository, never()).saveAll(any());
    }

    @Test
    void deduplicaRemuneracaoComMesmoId() {
      var r = rem(10L, Estado.A);
      var saved = funcionarioWith(List.of(r, r), List.of());

      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tipoRel, r)).thenReturn(false);

      helper.associarNovos(tipoRel, saved);

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void naoSalvaNadaSeListasVazias() {
      var saved = funcionarioWith(List.of(), List.of());

      helper.associarNovos(tipoRel, saved);

      verify(tipoRelRemPagEntityRepository, never()).saveAll(any());
    }

    @Test
    void associaRemuneracoesEPagamentosNumUnicoSaveAll() {
      var r = rem(10L, Estado.A);
      var p = pag(20L, Estado.P);
      var saved = funcionarioWith(List.of(r), List.of(p));

      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndRemId(tipoRel, r)).thenReturn(false);
      when(tipoRelRemPagEntityRepository.existsByTiprelIdAndPagId(tipoRel, p)).thenReturn(false);

      helper.associarNovos(tipoRel, saved);

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository, times(1)).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(2);
    }
  }

  // ─────────────────────────────────────────────────────────────
  // transferirParaNovoTipoRelacionamento
  // ─────────────────────────────────────────────────────────────

  @Nested
  class TransferirParaNovoTipoRelacionamento {

    private TiposRelacionamentoEntity tipoRelAtual;
    private TiposRelacionamentoEntity novoTipoRel;

    @BeforeEach
    void setUp() {
      tipoRelAtual = new TiposRelacionamentoEntity();
      tipoRelAtual.setId(1L);

      novoTipoRel = new TiposRelacionamentoEntity();
      novoTipoRel.setId(2L);
    }

    @Test
    void transfereRemuneracoesAtivasDoAnterior() {
      var r1 = rem(10L, Estado.A);
      var r2 = rem(11L, Estado.A);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(r1, r2));
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(), List.of());

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      List<TipoRelRemPagEntity> salvo = captor.getValue();
      assertThat(salvo).hasSize(2);
      assertThat(salvo).allMatch(a -> a.getTiprelId().equals(novoTipoRel));
      assertThat(salvo).allMatch(a -> a.getPagId() == null);
    }

    @Test
    void transferePagamentosAtivosDoAnterior() {
      var p1 = pag(20L, Estado.A);
      var p2 = pag(21L, Estado.A);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(p1, p2));

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(), List.of());

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      List<TipoRelRemPagEntity> salvo = captor.getValue();
      assertThat(salvo).hasSize(2);
      assertThat(salvo).allMatch(a -> a.getRemId() == null);
    }

    @Test
    void incluiNovosItensAlemDosTransferidos() {
      var rAtivo = rem(10L, Estado.A);
      var rNovo = rem(99L, Estado.P);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(rAtivo));
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(rNovo), List.of());

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void deduplicaQuandoMesmoItemEmAtivosENovos() {
      var r = rem(10L, Estado.A);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(r));
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());

      // mesmo item passado também como novo
      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(r), List.of());

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void naoSalvaNadaSemAtivosNemNovos() {
      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(), List.of());

      verify(tipoRelRemPagEntityRepository, never()).saveAll(any());
    }

    @Test
    void ignoraItemComIdNuloNasNovas() {
      var rSemId = rem(null, Estado.P);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of());

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(rSemId), List.of());

      verify(tipoRelRemPagEntityRepository, never()).saveAll(any());
    }

    @Test
    void todasAsAssociacoesApontamParaNovoTipoRel() {
      var r = rem(10L, Estado.A);
      var p = pag(20L, Estado.A);

      when(funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(r));
      when(funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelAtual.getId()))
          .thenReturn(List.of(p));

      helper.transferirParaNovoTipoRelacionamento(tipoRelAtual, novoTipoRel, List.of(), List.of());

      var captor = ArgumentCaptor.forClass(List.class);
      verify(tipoRelRemPagEntityRepository).saveAll(captor.capture());
      List<TipoRelRemPagEntity> salvo = captor.getValue();
      assertThat(salvo).hasSize(2);
      assertThat(salvo).allMatch(a -> a.getTiprelId().equals(novoTipoRel));
    }
  }
}
