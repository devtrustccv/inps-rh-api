package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveMissaoServicoLogisticaCommand;
import cv.inps.rh.missaoservico.application.dto.AjudaCustoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.AlojamentoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.BilhetePassagemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaRequestDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoRequisicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteLogisticaTest {

  @Mock
  private MissaoServicoEntityRepository missaoServicoRepository;

  @Mock
  private MissaoColaboradorEntityRepository missaoColaboradorRepository;

  @Mock
  private MissaoPrestadorEntityRepository missaoPrestadorRepository;

  @Mock
  private MissaoLogisticaEntityRepository missaoLogisticaRepository;

  @Mock
  private MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;

  @Mock
  private MissaoRequisicaoEntityRepository missaoRequisicaoRepository;

  @Mock
  private GeografiaEntityRepository geografiaRepository;

  @Mock
  private FuncionarioEntityRepository funcionarioRepository;

  @Mock
  private DocumentoEntityRepository documentoRepository;

  @Mock
  private NotificacaoEntityRepository notificacaoRepository;

  @Mock
  private DocumentoMapper documentoMapper;

  @InjectMocks
  private MissaoServicoServiceWrite service;

  @Test
  void salvarLogisticaCriaBilhetePassagem() {
    var missaoUuid = UUID.randomUUID();
    var colabUuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEstado("A");

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of());
    when(missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of());

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setMissaoServId(missao);
    when(missaoPrestadorRepository.findById(10L)).thenReturn(Optional.of(prest));

    var colab = new MissaoColaboradorEntity();
    colab.setId(20L);
    colab.setUuid(colabUuid);
    colab.setMissaoServId(missao);
    when(missaoColaboradorRepository.findByUuidOrThrow(colabUuid)).thenReturn(colab);

    var req = new MissaoRequisicaoEntity();
    req.setEstado("A");
    req.setMissaoPrestId(prest);
    req.setMissaoColabId(colab);
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of(req));

    when(missaoLogisticaRepository.saveAll(anyList())).thenAnswer(invocation -> {
      var list = invocation.getArgument(0, List.class);
      long id = 100L;
      for (var o : (List<?>) list) {
        var e = (cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity) o;
        if (e.getId() == null)
          e.setId(id++);
      }
      return list;
    });
    when(missaoLogisticaDetRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    var bilhete = new BilhetePassagemRequestDTO(List.of(colabUuid), BigDecimal.valueOf(100), null);
    var dto = new MissaoLogisticaRequestDTO(List.of(bilhete), null, null, null, ProcessStepAction.SAVE);
    var cmd = new SaveMissaoServicoLogisticaCommand(dto, missaoUuid.toString());

    var response = service.salvarLogistica(cmd);

    assertNotNull(response.getBody());
    assertEquals(missaoUuid.toString(), response.getBody().get("id"));
  }

  @Test
  void salvarLogisticaCalculaAjudaCustoComAlojamentoEAlimentacao() {
    var missaoUuid = UUID.randomUUID();
    var colabUuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEstado("A");
    missao.setDataInicio(LocalDate.of(2026, 3, 1));
    missao.setDataFim(LocalDate.of(2026, 3, 3));
    missao.setNrDias(3);

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);
    when(missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of());

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setMissaoServId(missao);
    when(missaoPrestadorRepository.findById(10L)).thenReturn(Optional.of(prest));

    var colab = new MissaoColaboradorEntity();
    colab.setId(20L);
    colab.setUuid(colabUuid);
    colab.setMissaoServId(missao);
    when(missaoColaboradorRepository.findByUuidOrThrow(colabUuid)).thenReturn(colab);
    when(missaoColaboradorRepository.findById(20L)).thenReturn(Optional.of(colab));

    var reqEnt = new MissaoRequisicaoEntity();
    reqEnt.setEstado("A");
    reqEnt.setMissaoPrestId(prest);
    reqEnt.setMissaoColabId(colab);
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of(reqEnt));

    final var captured = new cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity[1];
    when(missaoLogisticaRepository.saveAll(anyList())).thenAnswer(invocation -> {
      var list = invocation.getArgument(0, List.class);
      long id = 100L;
      for (var o : (List<?>) list) {
        var e = (cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity) o;
        if (e.getId() == null) e.setId(id++);
        if ("AJUDA_CUSTO".equals(e.getReferencia())) {
          captured[0] = e;
        }
      }
      return list;
    });
    when(missaoLogisticaDetRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    var aloj = new AlojamentoRequestDTO("SIM", "Hotel", BigDecimal.valueOf(50), BigDecimal.valueOf(150), "CVE",
        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 3), 20L, null);

    var ajuda = new AjudaCustoRequestDTO(colabUuid, true, 3, BigDecimal.valueOf(300), null);

    var dto = new MissaoLogisticaRequestDTO(null, null, List.of(aloj), List.of(ajuda), ProcessStepAction.SAVE);
    var cmd = new SaveMissaoServicoLogisticaCommand(dto, missaoUuid.toString());

    var response = service.salvarLogistica(cmd);

    assertNotNull(response.getBody());
    assertEquals(missaoUuid.toString(), response.getBody().get("id"));
    assertNotNull(captured[0]);
    assertEquals(0, BigDecimal.valueOf(100).compareTo(captured[0].getValorDiario()));
    assertEquals(0, BigDecimal.valueOf(300).compareTo(captured[0].getValorTotal()));
    assertEquals(3, captured[0].getNrDias());
  }
}
