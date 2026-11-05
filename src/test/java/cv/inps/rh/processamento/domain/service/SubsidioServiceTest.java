package cv.inps.rh.processamento.domain.service;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioNatalEditarRequestDTO;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubsidioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VSubsidioFeriaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VSubsidioFeriasDetailEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VSubsidioNatalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Um subsídio processado (FLG_ATIVO_INACTIVO = PR) só pode ser consultado. */
@ExtendWith(MockitoExtension.class)
class SubsidioServiceTest {

  private static final Long ID = 1L;
  private static final String MENSAGEM = "Subsídio já processado: só pode ser consultado.";

  @Mock private SubsidioEntityRepository subsidioRepository;
  @Mock private ValidacaoEntityRepository validacaoRepository;
  @Mock private FuncionarioEntityRepository funcionarioRepository;
  @Mock private VSubsidioFeriaEntityRepository feriaRepository;
  @Mock private VSubsidioNatalEntityRepository natalRepository;
  @Mock private VSubsidioFeriasDetailEntityRepository feriaDetailRepository;
  @Mock private FuncionarioRules funcionarioRules;
  @Mock private ApplicationAuditorAware auditor;

  private SubsidioService service;

  @BeforeEach
  void setUp() {
    service = new SubsidioService(subsidioRepository, validacaoRepository, funcionarioRepository, feriaRepository,
        natalRepository, feriaDetailRepository, funcionarioRules, auditor);
  }

  private SubsidioEntity subsidio(String flg, String estado) {
    var s = new SubsidioEntity();
    s.setFlgAtivoInactivo(flg);
    s.setEstado(estado);
    s.setValorSubsidio(100L);
    s.setFun(mock(FuncionarioEntity.class));
    lenient().when(subsidioRepository.findById(ID)).thenReturn(Optional.of(s));
    lenient().when(subsidioRepository.findNatalByIdIn(anyList())).thenReturn(List.of(s));
    return s;
  }

  private void assertRecusado(org.assertj.core.api.ThrowableAssert.ThrowingCallable accao, SubsidioEntity s, String flg, String estado) {
    assertThatThrownBy(accao).isInstanceOf(IgrpResponseStatusException.class).hasMessageContaining(MENSAGEM);
    assertThat(s.getFlgAtivoInactivo()).isEqualTo(flg);
    assertThat(s.getEstado()).isEqualTo(estado);
    verify(subsidioRepository, never()).save(any());
    verify(subsidioRepository, never()).saveAll(any());
    verify(subsidioRepository, never()).saveAndFlush(any());
  }

  @Test
  void feriasProcessadoNaoAtivaNemInativa() {
    var s = subsidio("PR", "A");
    assertRecusado(() -> service.alterarEstadoSubsisioferias(ID), s, "PR", "A");
  }

  @Test
  void feriasProcessadoNaoValida() {
    var s = subsidio("PR", "P");
    assertRecusado(() -> service.validarSubsidioFerias(ID), s, "PR", "P");
  }

  @Test
  void natalProcessadoNaoAtivaNemInativa() {
    var s = subsidio("PR", "A");
    assertRecusado(() -> service.alterarEstadoSubsidioNatal(List.of(ID)), s, "PR", "A");
  }

  @Test
  void natalProcessadoNaoValida() {
    var s = subsidio("PR", "P");
    assertRecusado(() -> service.validarSubsidioNatal(List.of(ID)), s, "PR", "P");
  }

  @Test
  void natalProcessadoNaoEdita() {
    var s = subsidio("PR", "A");
    var dto = new SubsidioNatalEditarRequestDTO();
    dto.setValorSubsidio(200L);
    dto.setObservacao("motivo");
    assertRecusado(() -> service.editarSubsidioNatal(ID, dto), s, "PR", "A");
  }

  @Test
  void feriasAtivoContinuaAInativar() {
    var s = subsidio("A", "A");
    service.alterarEstadoSubsisioferias(ID);
    assertThat(s.getFlgAtivoInactivo()).isEqualTo("I");
    verify(subsidioRepository).save(s);
  }

  @Test
  void natalPendenteContinuaAValidar() {
    var s = subsidio("A", "P");
    when(auditor.getCurrentSubjectName()).thenReturn("teste");
    assertThat(service.validarSubsidioNatal(List.of(ID))).isEqualTo(1);
    assertThat(s.getEstado()).isEqualTo("A");
  }
}
