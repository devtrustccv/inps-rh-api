package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.domain.service.processamentosalarial.api.ProcessarSalarioApi;
import cv.inps.rh.processamento.domain.service.processamentosalarial.api.model.OperationOutcomeResponse;
import cv.inps.rh.shared.application.services.IAMUserProfileService;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.events.EventPublisher;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.ResourceAccessException;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProcessamentoSalarialWriteServiceCabimentarTest {

  private static final String VALIDADO_DEFINITIVO = "VALIDADO_DEFINITIVO";
  private static final String CABIMENTADO = "CABIMENTADO";
  private static final String USERNAME = "tester";

  private final ProcessamentoSalarialEntityRepository repository = mock(ProcessamentoSalarialEntityRepository.class);
  private final ApplicationAuditorAware auditorAware = mock(ApplicationAuditorAware.class);
  private final ProcessarSalarioApi api = mock(ProcessarSalarioApi.class);
  private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

  private ProcessamentoSalarialWriteService service;

  private static ProcessamentoSalarialEntity processo(Long id) {
    var processo = new ProcessamentoSalarialEntity();
    processo.setId(id);
    processo.setDataDe(LocalDate.of(2025, 9, 1));
    return processo;
  }

  private static OperationOutcomeResponse outcome(int code, String diagnostics) {
    return new OperationOutcomeResponse(new OperationOutcomeResponse.Content(
        "OperationOutcome", new OperationOutcomeResponse.Issue(code, diagnostics)));
  }

  @BeforeEach
  void setUp() {
    when(auditorAware.getCurrentSubjectName()).thenReturn(USERNAME);
    service = new ProcessamentoSalarialWriteService(
        mock(TiposRelacionamentoEntityRepository.class),
        repository,
        mock(DataSource.class),
        jdbcTemplate,
        auditorAware,
        mock(IAMUserProfileService.class),
        api,
        mock(EventPublisher.class),
        new TransactionTemplate(mock(PlatformTransactionManager.class))
    );
  }

  @Test
  void enviaADataDePagamentoDoProcedimentoEMarcaCabimentado() {
    givenProcessos(processo(1L));
    when(api.processarCabimento("1", "28-09-2025")).thenReturn(outcome(200, "OK"));

    service.cabimentar(List.of(1L));

    verifyMarcado(1L, 1);
  }

  @Test
  @SuppressWarnings("unchecked")
  void naoChamaOServicoQuandoOProcedimentoDevolveMensagem() {
    var mensagem = "O ano de orcamento nao corresponde ao ano de processamento. Favor verificar.";
    givenProcessos(processo(1L));
    when(jdbcTemplate.execute(any(ConnectionCallback.class))).thenReturn(mensagem);

    assertThatThrownBy(() -> service.cabimentar(List.of(1L)))
        .isInstanceOfSatisfying(IgrpResponseStatusException.class,
            exception -> assertThat(exception.getBody().getTitle()).isEqualTo(mensagem));

    verify(api, never()).processarCabimento(any(), any());
    verifyMarcado(1L, 0);
  }

  @Test
  void falhaSeUmProcessoNaoEstaValidadoDefinitivamenteSemChamarOServico() {
    givenProcessos();

    assertThatThrownBy(() -> service.cabimentar(List.of(1L)))
        .isInstanceOf(IgrpResponseStatusException.class);

    verify(api, never()).processarCabimento(any(), any());
  }

  @Test
  void mantemOsProcessosJaCabimentadosQuandoOSeguinteERecusado() {
    givenProcessos(processo(1L), processo(2L));
    when(api.processarCabimento("1", "28-09-2025")).thenReturn(outcome(200, "OK"));
    when(api.processarCabimento("2", "28-09-2025")).thenReturn(outcome(400, "Data invalida"));

    assertThatThrownBy(() -> service.cabimentar(List.of(1L, 2L)))
        .isInstanceOf(IgrpResponseStatusException.class);

    verifyMarcado(1L, 1);
    verifyMarcado(2L, 0);
  }

  @Test
  void respondeBadGatewayQuandoOServicoEstaIndisponivel() {
    givenProcessos(processo(1L));
    when(api.processarCabimento("1", "28-09-2025")).thenThrow(new ResourceAccessException("timeout"));

    assertThatThrownBy(() -> service.cabimentar(List.of(1L)))
        .isInstanceOfSatisfying(IgrpResponseStatusException.class,
            exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY));

    verifyMarcado(1L, 0);
  }

  private void givenProcessos(ProcessamentoSalarialEntity... processos) {
    when(repository.findByIdInAndEstado(any(), eq(VALIDADO_DEFINITIVO))).thenReturn(List.of(processos));
  }

  private void verifyMarcado(Long id, int vezes) {
    verify(repository, times(vezes)).atualizarEstadoEUtilizadores(
        eq(List.of(id)), eq(List.of(VALIDADO_DEFINITIVO)), eq(CABIMENTADO),
        isNull(), isNull(), eq(USERNAME), isNull());
  }
}
