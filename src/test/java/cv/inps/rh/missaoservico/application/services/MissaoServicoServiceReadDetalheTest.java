package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetDetalheMissaoServicoQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadDetalheTest {

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

  @Mock
  private MissaoColaboradorEntityRepository missaoColaboradorRepository;

  @InjectMocks
  private MissaoServicoServiceRead service;

  @Test
  void getDetalheRetornaCamposPrincipaisEAuditoria() {
    var missaoUuid = UUID.randomUUID();

    var pais = new GeografiaEntity();
    pais.setId(1L);
    pais.setNome("Cabo Verde");

    var missao = new MissaoServicoEntity();
    missao.setId(10L);
    missao.setUuid(missaoUuid);
    missao.setNrMissao(99L);
    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(1);
    missao.setDescricaoDestino("Praia");
    missao.setDataInicio(LocalDate.of(2026, 3, 1));
    missao.setDataFim(LocalDate.of(2026, 3, 3));
    missao.setNrDias(3);
    missao.setAutorizadoPor("User");
    missao.setDataAutorizacao(LocalDate.of(2026, 2, 28));
    missao.setEtapa("SUBMISSAO");
    missao.setEstado("A");
    missao.setMotivoCancelamento("X");
    missao.setCreatedDate(LocalDateTime.of(2026, 2, 28, 10, 0));
    missao.setCreatedBy("creator");
    missao.setCreatedById(1L);
    missao.setLastModifiedDate(LocalDateTime.of(2026, 3, 1, 12, 0));
    missao.setLastModifiedBy("editor");
    missao.setLastModifiedById(2L);

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var resp = service.getDetalhe(new GetDetalheMissaoServicoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(10L, resp.getBody().getId());
    assertEquals(missaoUuid, resp.getBody().getUuid());
    assertEquals(99L, resp.getBody().getNrMissao());
    assertEquals("Cabo Verde", resp.getBody().getPaisDestinoNome());
    assertEquals("NACIONAL", resp.getBody().getAmbitoMissao());
    assertEquals("SUBMISSAO", resp.getBody().getEtapa());
    assertEquals("A", resp.getBody().getEstado());
    assertEquals("X", resp.getBody().getMotivoCancelamento());
    assertNotNull(resp.getBody().getDataRegisto());
    assertNotNull(resp.getBody().getDataAlteracao());
  }
}

