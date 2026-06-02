package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetAnaliseProcessoMissaoServicoQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadAnaliseTest {

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

  @Mock
  private MissaoPrestadorEntityRepository missaoPrestadorRepository;

  @Mock
  private NotificacaoEntityRepository notificacaoRepository;

  @InjectMocks
  private MissaoServicoServiceRead service;

  @Test
  void getAnaliseRetornaPrestadoresAtivosENotificacao() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("ANALISE");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var p1 = new MissaoPrestadorEntity();
    p1.setId(10L);
    p1.setUuid(UUID.randomUUID());
    p1.setEntId(100L);
    p1.setNome("P1");
    p1.setEmail("p1@mail");
    p1.setEstado("A");

    var p2 = new MissaoPrestadorEntity();
    p2.setId(11L);
    p2.setUuid(UUID.randomUUID());
    p2.setEntId(101L);
    p2.setNome("P2");
    p2.setEmail("p2@mail");
    p2.setEstado("I");

    when(missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(p1, p2));

    var n1 = new NotificacaoEntity();
    n1.setId(1L);
    n1.setAssunto("A1");
    n1.setMessage("M1");

    var n2 = new NotificacaoEntity();
    n2.setId(2L);
    n2.setAssunto("A2");
    n2.setMessage("M2");

    when(notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class))).thenReturn(List.of(n1, n2));

    var resp = service.getAnalise(new GetAnaliseProcessoMissaoServicoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1L, resp.getBody().getMissaoId());
    assertEquals("ANALISE", resp.getBody().getEtapaAtual());
    assertEquals(1, resp.getBody().getPrestadores().size());
    assertEquals("P1", resp.getBody().getPrestadores().get(0).getNome());
    assertNotNull(resp.getBody().getNotificacao());
    assertEquals("A2", resp.getBody().getNotificacao().getAssunto());
    assertEquals("M2", resp.getBody().getNotificacao().getCorpoEmail());
  }
}

