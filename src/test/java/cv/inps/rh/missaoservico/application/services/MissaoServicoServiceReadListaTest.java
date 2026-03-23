package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetListaMissaoServicoQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadListaTest {

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
  void getListaRetornaPaginacaoESomasDaLogistica() {
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
    missao.setEtapa("ETAPA_4_PROCESSAMENTO_LOGISTICO");
    missao.setEstado("A");

    var page = new PageImpl<>(List.of(missao), PageRequest.of(0, 10), 1);
    when(missaoServicoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    var ac = new MissaoLogisticaEntity();
    ac.setId(100L);
    ac.setEstado("A");
    ac.setReferencia("AJUDA_CUSTO");
    ac.setValorTotal(BigDecimal.valueOf(50));
    ac.setMissaoServId(missao);

    var bp = new MissaoLogisticaEntity();
    bp.setId(101L);
    bp.setEstado("A");
    bp.setReferencia("BILHETE_PASSAGEM");
    bp.setValorTotal(BigDecimal.valueOf(150));
    bp.setMissaoServId(missao);

    when(missaoLogisticaRepository.findAll(any(Specification.class))).thenReturn(List.of(ac, bp));

    var resp = service.getLista(new GetListaMissaoServicoQuery(null, null, null, "0", "10"));

    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getPageNumber());
    assertEquals(10, resp.getBody().getPageSize());
    assertEquals(1L, resp.getBody().getTotalElements());
    assertEquals(1, resp.getBody().getTotalPages());
    assertEquals(1, resp.getBody().getContent().size());

    var row = resp.getBody().getContent().get(0);
    assertEquals(99L, row.getNrMissao());
    assertEquals("Praia", row.getDestino());
    assertEquals("Nacional", row.getNacionalInternacional());
    assertEquals("ETAPA_4_PROCESSAMENTO_LOGISTICO", row.getEtapa());
    assertEquals("PENDENTE_FATURA", row.getEstado());
    assertEquals(0, BigDecimal.valueOf(50).compareTo(row.getValorAC()));
    assertEquals(0, BigDecimal.valueOf(150).compareTo(row.getValorBP()));
  }
}
