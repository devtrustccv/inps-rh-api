package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveSubmissaoServicoEmissaoRequisicaoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoRequisicaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoRequisicaoItemRequestDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteEmissaoRequisicaoTest {

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
  void salvarEmissaoRequisicaoCriaEInativaRequisicoes() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setNrMissao(1L);
    missao.setEtapa("ETAPA_2_ANALISE_RH");
    missao.setEstado("A");

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var prest = new MissaoPrestadorEntity();
    prest.setId(10L);
    prest.setEntId(10L);
    prest.setNome("Agencia");
    prest.setEmail("a@mail");
    prest.setMissaoServId(missao);

    when(missaoPrestadorRepository.findById(10L)).thenReturn(Optional.of(prest));

    var funUuid = UUID.randomUUID();
    var fun = new FuncionarioEntity();
    fun.setUuid(funUuid);

    var colab = new MissaoColaboradorEntity();
    colab.setId(20L);
    colab.setFunId(fun);
    colab.setMissaoServId(missao);

    when(missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missaoUuid, funUuid))
        .thenReturn(Optional.of(colab));
    when(missaoColaboradorRepository.findById(20L)).thenReturn(Optional.of(colab));

    var existing = new MissaoRequisicaoEntity();
    existing.setUuid(UUID.randomUUID());
    existing.setEstado("A");
    existing.setMissaoPrestId(prest);
    var otherColab = new MissaoColaboradorEntity();
    otherColab.setId(21L);
    existing.setMissaoColabId(otherColab);

    when(missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)).thenReturn(List.of(existing));
    when(missaoRequisicaoRepository.saveAll(anyList())).thenAnswer(invocation -> {
      var list = (List<MissaoRequisicaoEntity>) invocation.getArgument(0);
      long id = 100L;
      for (var e : list) {
        if (e.getId() == null) {
          e.setId(id++);
        }
        if (e.getUuid() == null) {
          e.setUuid(UUID.randomUUID());
        }
      }
      return list;
    });

    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class)))
        .thenReturn(List.of());
    when(documentoMapper.syncDocumentos(anyList(), anyList(), anyString(), any(Long.class), any(UUID.class),
        any(Long.class), eq(null)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var proposta = new AnexoReqDTO();
    proposta.setTipoDocumentoId(1L);
    proposta.setDocumento("file");

    var item = new MissaoRequisicaoItemRequestDTO(10L, true, List.of(funUuid), proposta);
    var req = new MissaoEmissaoRequisicaoRequestDTO(List.of(item), ProcessStepAction.SAVE);
    var cmd = new SaveSubmissaoServicoEmissaoRequisicaoCommand(req, missaoUuid.toString());

    var response = service.salvarEmissaoRequisicao(cmd);

    assertNotNull(response.getBody());
    assertEquals(missaoUuid.toString(), response.getBody().get("id"));
  }
}
