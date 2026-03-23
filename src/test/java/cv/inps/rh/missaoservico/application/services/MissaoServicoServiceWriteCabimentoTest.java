package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveMissaoServicoCabimentoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoItemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoRequestDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteCabimentoTest {

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
  void salvarCabimentoAtualizaCabIdEAnexo() {
    var missaoUuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEstado("A");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var logUuid = UUID.randomUUID();
    var log = new MissaoLogisticaEntity();
    log.setId(10L);
    log.setUuid(logUuid);
    log.setEstado("A");
    log.setMissaoServId(missao);

    when(missaoLogisticaRepository.findById(10L)).thenReturn(Optional.of(log));
    when(missaoLogisticaRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class))).thenReturn(List.of());
    when(documentoMapper.syncDocumentos(anyList(), anyList(), anyString(), any(Long.class), any(UUID.class), any(Long.class), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var anexo = new AnexoReqDTO();
    anexo.setTipoDocumentoId(1L);
    anexo.setDocumento("fatura");

    var item = new MissaoCabimentoItemRequestDTO(10L, true, 99L, anexo);
    var dto = new MissaoCabimentoRequestDTO(List.of(item), ProcessStepAction.NEXT);
    var cmd = new SaveMissaoServicoCabimentoCommand(dto, missaoUuid.toString());

    var resp = service.salvarCabimento(cmd);

    assertNotNull(resp.getBody());
    assertEquals(missaoUuid.toString(), resp.getBody().get("id"));
    assertEquals(99L, log.getCabId());
    assertEquals("CABIMENTADO", log.getEstadoCabimento());
  }
}

