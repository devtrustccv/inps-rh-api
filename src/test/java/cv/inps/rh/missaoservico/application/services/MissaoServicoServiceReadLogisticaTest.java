package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoLogisticaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadLogisticaTest {

  @Mock
  private MissaoServicoEntityRepository missaoServicoRepository;

  @Mock
  private MissaoLogisticaEntityRepository missaoLogisticaRepository;

  @Mock
  private MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;

  @Mock
  private DocumentoEntityRepository documentoRepository;

  @Mock
  private DocumentoMapper documentoMapper;

  @InjectMocks
  private MissaoServicoServiceRead service;

  @Test
  void getLogisticaRetornaBilheteComColaboradoresEDocumento() {
    var missaoUuid = UUID.randomUUID();
    var logUuid = UUID.randomUUID();
    var colabUuid = UUID.randomUUID();
    var funUuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("LOGISTICA");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var log = new MissaoLogisticaEntity();
    log.setId(10L);
    log.setUuid(logUuid);
    log.setEstado("A");
    log.setReferencia("BILHETE_PASSAGEM");
    log.setValorTotal(BigDecimal.valueOf(200));
    log.setMissaoServId(missao);
    when(missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(log));

    var fun = new FuncionarioEntity();
    fun.setUuid(funUuid);
    fun.setNome("F1");
    var colab = new MissaoColaboradorEntity();
    colab.setUuid(colabUuid);
    colab.setFunId(fun);

    var det = new MissaoLogisticaDetEntity();
    det.setId(100L);
    det.setEstado("A");
    det.setMissaoLogistId(log);
    det.setMissaoColabId(colab);
    when(missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(anyList())).thenReturn(List.of(det));

    var doc = new DocumentoEntity();
    doc.setId(5L);
    doc.setEstado(Estado.A);
    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class)))
        .thenReturn(List.of(doc));

    var anexo = new AnexoRespDTO();
    when(documentoMapper.toRespDto(any(DocumentoEntity.class))).thenReturn(anexo);

    var resp = service.getLogistica(new GetMissaoServicoLogisticaQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1L, resp.getBody().getMissaoId());
    assertEquals(missaoUuid, resp.getBody().getMissaoUuid());
    assertEquals(1, resp.getBody().getBilhetesPassagem().size());
    assertEquals(1, resp.getBody().getBilhetesPassagem().get(0).getColaboradores().size());
    assertEquals("F1", resp.getBody().getBilhetesPassagem().get(0).getColaboradores().get(0).getNomeColaborador());
    assertNotNull(resp.getBody().getBilhetesPassagem().get(0).getDocumento());
  }
}
