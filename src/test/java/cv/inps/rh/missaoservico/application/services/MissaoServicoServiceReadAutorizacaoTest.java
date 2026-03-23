package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoAutorizacaoQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceReadAutorizacaoTest {

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
  void getAutorizacaoRetornaNomeDoColaboradorEmAjudaCusto() {
    var missaoUuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setEtapa("ETAPA_6_AUTORIZACAO_RH");
    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setNome("Prestador");

    var log = new MissaoLogisticaEntity();
    log.setId(20L);
    log.setEstado("A");
    log.setReferencia("AJUDA_CUSTO");
    log.setValorTotal(BigDecimal.valueOf(100));
    log.setCabId(99L);
    log.setEstadoCabimento("CABIMENTADO");
    log.setPrestadorServId(prest);
    log.setMissaoServId(missao);

    when(missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(log));

    var fun = new FuncionarioEntity();
    fun.setNome("Colaborador 1");
    var colab = new MissaoColaboradorEntity();
    colab.setFunId(fun);

    var det = new MissaoLogisticaDetEntity();
    det.setMissaoLogistId(log);
    det.setMissaoColabId(colab);
    det.setEstado("A");

    when(missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(anyList())).thenReturn(List.of(det));

    var resp = service.getAutorizacao(new GetMissaoServicoAutorizacaoQuery(missaoUuid.toString()));

    assertNotNull(resp.getBody());
    assertEquals(1L, resp.getBody().getMissaoId());
    assertEquals("ETAPA_6_AUTORIZACAO_RH", resp.getBody().getEtapaAtual());
    assertEquals(1, resp.getBody().getItens().size());
    assertEquals("Colaborador 1", resp.getBody().getItens().get(0).getNome());
  }
}

