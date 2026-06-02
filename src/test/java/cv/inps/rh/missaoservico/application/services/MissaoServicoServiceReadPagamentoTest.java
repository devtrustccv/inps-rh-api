package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoPagamentoQuery;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadPagamentoTest {

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
  void getPagamentoRetornaReferenciaEData() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("PAGAMENTO");
    missao.setEstado("A");
    missao.setReferenciaPagamento("REF-999");
    missao.setDataPagamento(LocalDate.of(2026, 3, 20));

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var resp = service.getPagamento(new GetMissaoServicoPagamentoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1L, resp.getBody().getMissaoId());
    assertEquals("PAGAMENTO", resp.getBody().getEtapaAtual());
    assertEquals("A", resp.getBody().getEstado());
    assertEquals("REF-999", resp.getBody().getReferenciaPagamento());
    assertEquals(LocalDate.of(2026, 3, 20), resp.getBody().getDataPagamento());
  }
}

