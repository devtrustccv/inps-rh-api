package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetSubmissaoServicoProcessQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadSubmissaoTest {

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
  void getSubmissaoRetornaDadosBasicosColaboradoresEDocumentos() {
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
    missao.setCreatedDate(LocalDateTime.of(2026, 2, 28, 10, 0));
    missao.setCreatedBy("creator");
    missao.setCreatedById(1L);
    missao.setLastModifiedDate(LocalDateTime.of(2026, 3, 1, 12, 0));
    missao.setLastModifiedBy("editor");
    missao.setLastModifiedById(2L);

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var fun = new FuncionarioEntity();
    fun.setId(7L);
    fun.setUuid(UUID.randomUUID());
    fun.setNome("Colab 1");

    var colabA = new MissaoColaboradorEntity();
    colabA.setId(20L);
    colabA.setUuid(UUID.randomUUID());
    colabA.setEstado("A");
    colabA.setNumDocumento(123L);
    colabA.setFunId(fun);

    var colabI = new MissaoColaboradorEntity();
    colabI.setId(21L);
    colabI.setUuid(UUID.randomUUID());
    colabI.setEstado("I");

    when(missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(colabA, colabI));

    var docA = new DocumentoEntity();
    docA.setId(1L);
    docA.setEstado(Estado.A);

    var docE = new DocumentoEntity();
    docE.setId(2L);
    docE.setEstado(Estado.E);

    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class))).thenReturn(List.of(docA, docE));
    when(documentoMapper.toRespDto(any(DocumentoEntity.class))).thenReturn(new AnexoRespDTO());

    var resp = service.getSubmissao(new GetSubmissaoServicoProcessQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(10L, resp.getBody().getId());
    assertEquals(missaoUuid, resp.getBody().getUuid());
    assertEquals(99L, resp.getBody().getNrMissao());
    assertEquals("SUBMISSAO", resp.getBody().getEtapaAtual());
    assertEquals("NACIONAL", resp.getBody().getAmbitoMissao());
    assertEquals(1, resp.getBody().getColaboradores().size());
    assertEquals("Colab 1", resp.getBody().getColaboradores().get(0).getNomeColaborador());
    assertEquals(1, resp.getBody().getDocumentos().size());
    assertNotNull(resp.getBody().getDataRegisto());
    assertNotNull(resp.getBody().getDataAlteracao());
  }
}

