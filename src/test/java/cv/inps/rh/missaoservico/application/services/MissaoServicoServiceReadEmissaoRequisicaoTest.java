package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetSubmissaoServicoEmissaoRequisicaoQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadEmissaoRequisicaoTest {

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

  @Mock
  private MissaoRequisicaoEntityRepository missaoRequisicaoRepository;

  @InjectMocks
  private MissaoServicoServiceRead service;

  @Test
  void getEmissaoRequisicaoAgrupaPorPrestadorEListaColaboradores() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("ETAPA_3_EMISSAO_REQUISICAO");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setUuid(UUID.randomUUID());
    prest.setNome("Prestador 1");
    prest.setEmail("p1@mail");

    var fun1 = new FuncionarioEntity();
    fun1.setId(100L);
    fun1.setUuid(UUID.randomUUID());
    fun1.setNome("C1");
    var colab1 = new MissaoColaboradorEntity();
    colab1.setId(20L);
    colab1.setUuid(UUID.randomUUID());
    colab1.setEstado("A");
    colab1.setFunId(fun1);

    var fun2 = new FuncionarioEntity();
    fun2.setId(101L);
    fun2.setUuid(UUID.randomUUID());
    fun2.setNome("C2");
    var colab2 = new MissaoColaboradorEntity();
    colab2.setId(21L);
    colab2.setUuid(UUID.randomUUID());
    colab2.setEstado("A");
    colab2.setFunId(fun2);

    var req1 = new MissaoRequisicaoEntity();
    req1.setId(30L);
    req1.setUuid(UUID.randomUUID());
    req1.setEstado("A");
    req1.setMissaoPrestId(prest);
    req1.setMissaoColabId(colab1);

    var req2 = new MissaoRequisicaoEntity();
    req2.setId(31L);
    req2.setUuid(UUID.randomUUID());
    req2.setEstado("A");
    req2.setMissaoPrestId(prest);
    req2.setMissaoColabId(colab2);

    prest.setEstado("A");
    when(missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(prest));
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of(req1, req2));

    var doc = new DocumentoEntity();
    doc.setId(1L);
    doc.setEstado(Estado.A);
    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class))).thenReturn(List.of(doc));
    when(documentoMapper.toRespDto(any(DocumentoEntity.class))).thenReturn(new AnexoRespDTO());

    var resp = service.getEmissaoRequisicao(new GetSubmissaoServicoEmissaoRequisicaoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1L, resp.getBody().getMissaoId());
    assertEquals("ETAPA_3_EMISSAO_REQUISICAO", resp.getBody().getEtapaAtual());
    assertEquals(1, resp.getBody().getRequisicoes().size());
    assertEquals(10L, resp.getBody().getRequisicoes().get(0).getMissaoPrestId());
    assertEquals(2, resp.getBody().getRequisicoes().get(0).getColaboradores().size());
    assertNotNull(resp.getBody().getRequisicoes().get(0).getProposta());
  }

  @Test
  void getEmissaoRequisicaoRetornaPrestadoresMesmoSemRequisicoes() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("ETAPA_3_EMISSAO_REQUISICAO");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setUuid(UUID.randomUUID());
    prest.setNome("Prestador 1");
    prest.setEmail("p1@mail");
    prest.setEstado("A");

    when(missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(prest));
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of());

    var resp = service.getEmissaoRequisicao(new GetSubmissaoServicoEmissaoRequisicaoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1, resp.getBody().getRequisicoes().size());
    assertEquals(10L, resp.getBody().getRequisicoes().get(0).getMissaoPrestId());
    assertEquals(0, resp.getBody().getRequisicoes().get(0).getColaboradores().size());
  }
}
