package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.CancelarMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoCancelarRequestDTO;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteCancelarTest {

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
  void cancelarMarcaEstadoInativoECriaNotificacao() {
    var uuid = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(uuid);
    missao.setNrMissao(99L);
    missao.setEtapa("ETAPA_3_EMISSAO_REQUISICAO");
    missao.setEstado("A");

    when(missaoServicoRepository.findByUuidOrThrow(uuid)).thenReturn(missao);
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(missaoColaboradorRepository.findAllByMissaoServId_Uuid(uuid))
        .thenReturn(List.of(new MissaoColaboradorEntity()));
    when(missaoPrestadorRepository.findAllByMissaoServId_Uuid(uuid)).thenReturn(List.of(new MissaoPrestadorEntity()));
    when(missaoLogisticaRepository.findAllByMissaoServId_Uuid(uuid)).thenReturn(List.of());
    when(missaoLogisticaDetRepository.findAllByMissaoLogistId_MissaoServId_Uuid(uuid)).thenReturn(List.of());
    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(uuid)).thenReturn(List.of());

    var n1 = new NotificacaoEntity();
    n1.setEmail("a@mail");
    n1.setNomeReceptor("A");
    var n2 = new NotificacaoEntity();
    n2.setEmail("b@mail");
    n2.setNomeReceptor("B");
    when(notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_SERVICO.name(), uuid))
        .thenReturn(List.of(n1, n2));
    when(notificacaoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_SERVICO.name(), uuid))
        .thenReturn(List.of());

    var req = new MissaoCancelarRequestDTO("motivo");
    var cmd = new CancelarMissaoServicoCommand(req, uuid.toString());

    var resp = service.cancelar(cmd);

    assertNotNull(resp);
    assertEquals("I", missao.getEstado());
    verify(notificacaoRepository).saveAll(anyList());
  }
}
