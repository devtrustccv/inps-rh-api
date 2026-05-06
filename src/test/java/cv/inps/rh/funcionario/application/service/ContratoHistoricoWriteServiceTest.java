package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoHistoricoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoHistoricoWriteServiceTest {

  @Mock
  private ContratoHistoricoEntityRepository repository;

  @InjectMocks
  private ContratoHistoricoWriteService service;

  // ─────────────────────────────── registrarNovo ────────────────────────────

  @Test
  void registrarNovo_criaHistoricoComVersao1EstadoPEDatasDoContrato() {
    var contrato = contratoComId(10L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var resultado = service.registrarNovo(contrato);

    assertEquals(1, resultado.getVersao());
    assertEquals(Estado.P, resultado.getEstado());
    assertEquals("NOVO_CONTRATO", resultado.getObs());
    assertEquals(LocalDate.of(2024, 1, 1), resultado.getDataInicio());
    assertEquals(LocalDate.of(2024, 12, 31), resultado.getDataFim());
    assertSame(contrato, resultado.getContratoId());
  }

  @Test
  void registrarNovo_persisteExatamenteUmaVez() {
    var contrato = contratoComId(10L, LocalDate.now(), null);
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    service.registrarNovo(contrato);

    verify(repository, times(1)).save(any(ContratoHistoricoEntity.class));
  }

  // ─────────────────────────── registrarRenovacaoPendente ───────────────────

  @Test
  void registrarRenovacaoPendente_incrementaVersaoDoUltimoHistorico() {
    var contrato = contratoComId(5L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    var historicoExistente = new ContratoHistoricoEntity();
    historicoExistente.setVersao(2);
    when(repository.findTopByContratoId_IdOrderByVersaoDesc(5L))
        .thenReturn(Optional.of(historicoExistente));
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var dto = renovacaoDto(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), 12);
    var resultado = service.registrarRenovacaoPendente(contrato, dto);

    assertEquals(3, resultado.getVersao());
    assertEquals(Estado.P, resultado.getEstado());
    assertEquals("RENOVACAO", resultado.getObs());
    assertEquals(LocalDate.of(2024, 1, 1), resultado.getDataInicio());
    assertEquals(LocalDate.of(2024, 12, 31), resultado.getDataFim());
    assertEquals(12, resultado.getDuracao());
  }

  @Test
  void registrarRenovacaoPendente_usaVersao2QuandoNaoExisteHistoricoAnterior() {
    var contrato = contratoComId(7L, LocalDate.now(), null);
    when(repository.findTopByContratoId_IdOrderByVersaoDesc(7L)).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var dto = renovacaoDto(LocalDate.now(), LocalDate.now().plusMonths(6), 6);
    var resultado = service.registrarRenovacaoPendente(contrato, dto);

    assertEquals(2, resultado.getVersao());
  }

  // ─────────────────────────────── aplicarEstado ────────────────────────────

  @Test
  void aplicarEstado_transicionaHistoricoPendenteParaEstadoDado() {
    var contrato = contratoComId(3L, LocalDate.now(), null);
    var historicoPendente = new ContratoHistoricoEntity();
    historicoPendente.setEstado(Estado.P);
    when(repository.findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(3L, Estado.P))
        .thenReturn(Optional.of(historicoPendente));

    service.aplicarEstado(contrato, Estado.A);

    assertEquals(Estado.A, historicoPendente.getEstado());
    verify(repository).save(historicoPendente);
  }

  @Test
  void aplicarEstado_naoFazNadaQuandoNaoExisteHistoricoPendente() {
    var contrato = contratoComId(3L, LocalDate.now(), null);
    when(repository.findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(3L, Estado.P))
        .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.aplicarEstado(contrato, Estado.I));
    verify(repository, never()).save(any());
  }

  // ──────────────────────────── transicionarEstado ──────────────────────────

  @Test
  void transicionarEstado_atualizaContratoSituacaoLaboralEHistorico() {
    var situacaoPendente = new SituacaoLaboralEntity();
    situacaoPendente.setEstado(Estado.P);
    var contrato = contratoComSituacoes(2L, List.of(situacaoPendente));

    var historicoPendente = new ContratoHistoricoEntity();
    historicoPendente.setEstado(Estado.P);
    when(repository.findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(2L, Estado.P))
        .thenReturn(Optional.of(historicoPendente));

    service.transicionarEstado(contrato, Estado.A);

    assertEquals(Estado.A, contrato.getEstado());
    assertEquals(Estado.A, situacaoPendente.getEstado());
    assertEquals(Estado.A, historicoPendente.getEstado());
  }

  @Test
  void transicionarEstado_semSituacaoLaboralPendente_naoLancaExcecao() {
    var situacaoAprovada = new SituacaoLaboralEntity();
    situacaoAprovada.setEstado(Estado.A);
    var contrato = contratoComSituacoes(4L, List.of(situacaoAprovada));

    when(repository.findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(4L, Estado.P))
        .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.transicionarEstado(contrato, Estado.I));
    assertEquals(Estado.I, contrato.getEstado());
    assertEquals(Estado.A, situacaoAprovada.getEstado());
  }

  @Test
  void transicionarEstado_semSituacoesLaborais_naoLancaExcecao() {
    var contrato = contratoComSituacoes(6L, new ArrayList<>());
    when(repository.findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(6L, Estado.P))
        .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> service.transicionarEstado(contrato, Estado.A));
    assertEquals(Estado.A, contrato.getEstado());
  }

  // ─────────────────────────────── helpers ──────────────────────────────────

  private ContratoEntity contratoComId(Long id, LocalDate inicio, LocalDate fim) {
    var c = new ContratoEntity();
    c.setId(id);
    c.setDataInicio(inicio);
    c.setDataFim(fim);
    c.setSituacoesLaborais(new ArrayList<>());
    return c;
  }

  private ContratoEntity contratoComSituacoes(Long id, List<SituacaoLaboralEntity> situacoes) {
    var c = new ContratoEntity();
    c.setId(id);
    c.setDataInicio(LocalDate.now());
    c.setSituacoesLaborais(new ArrayList<>(situacoes));
    return c;
  }

  private RenovarContratoReqDTO renovacaoDto(LocalDate inicio, LocalDate fim, int duracao) {
    var dto = new RenovarContratoReqDTO();
    dto.setDataInicio(inicio);
    dto.setDataFim(fim);
    dto.setDuracaoMeses(duracao);
    return dto;
  }
}
