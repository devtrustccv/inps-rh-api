package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveAnaliseProcessoMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPrestadorDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteAnaliseTest {

  @Mock
  private MissaoServicoEntityRepository missaoServicoRepository;

  @Mock
  private MissaoColaboradorEntityRepository missaoColaboradorRepository;

  @Mock
  private MissaoPrestadorEntityRepository missaoPrestadorRepository;

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
  void salvarAnaliseAtualizaPrestadores() {
    var missaoUuid = UUID.randomUUID();
    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);

    var existente = new MissaoPrestadorEntity();
    existente.setEntId(10L);
    existente.setNome("Old");
    existente.setEmail("old@mail");
    existente.setEstado("A");
    existente.setMissaoServId(missao);

    when(missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(existente));
    when(missaoPrestadorRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(notificacaoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var analise = new MissaoAnaliseRequestDTO();
    analise.setPrestadores(List.of(
        new MissaoPrestadorDTO(10L, "New", "new@mail"),
        new MissaoPrestadorDTO(11L, "P2", "p2@mail")));
    analise.setNotificacao(new MissaoNotificacaoRequestDTO("ass", "msg"));

    var cmd = new SaveAnaliseProcessoMissaoServicoCommand(analise, missaoUuid.toString());
    var resp = service.salvarAnalise(cmd);

    assertNotNull(resp.getBody());
    assertEquals(missaoUuid.toString(), resp.getBody().get("id"));
  }
}
