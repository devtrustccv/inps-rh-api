package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveSubmissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceWriteSubmissaoTest {

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
  void salvarSubmissaoAtualizaColaboradoresComSoftDelete() {
    var missaoUuid = UUID.randomUUID();
    var fun1 = UUID.randomUUID();
    var fun2 = UUID.randomUUID();

    var missao = new MissaoServicoEntity();
    missao.setId(1L);
    missao.setUuid(missaoUuid);
    missao.setNrMissao(20L);

    when(missaoServicoRepository.findByUuidOrThrow(missaoUuid)).thenReturn(missao);
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var existente1 = new MissaoColaboradorEntity();
    existente1.setUuid(UUID.randomUUID());
    existente1.setEstado("A");
    var f1 = new FuncionarioEntity();
    f1.setUuid(fun1);
    f1.setNumDocumento("1");
    existente1.setFunId(f1);
    existente1.setMissaoServId(missao);

    var existente2 = new MissaoColaboradorEntity();
    existente2.setUuid(UUID.randomUUID());
    existente2.setEstado("A");
    var f2 = new FuncionarioEntity();
    f2.setUuid(fun2);
    f2.setNumDocumento("2");
    existente2.setFunId(f2);
    existente2.setMissaoServId(missao);

    when(missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid)).thenReturn(List.of(existente1, existente2));
    when(missaoColaboradorRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    var pais = new GeografiaEntity();
    pais.setId(1L);
    pais.setNome("Cabo Verde");
    when(geografiaRepository.findByIdOrThrow(1L)).thenReturn(pais);
    when(funcionarioRepository.findByUuidOrThrow(fun1)).thenReturn(f1);

    var dto = new MissaoSubmissaoRequestDTO();
    dto.setPaisDestinoId(1L);
    dto.setDescricaoDestino("Praia");
    dto.setDataInicio(LocalDate.of(2026, 3, 1));
    dto.setDataFim(LocalDate.of(2026, 3, 1));
    dto.setAutorizadoPor("Direcao");
    dto.setDataAutorizacao(LocalDate.of(2026, 2, 20));
    dto.setColaboradores(List.of(new MissaoColaboradorRequestDTO(fun1)));
    dto.setDocumentos(List.of());

    var cmd = new SaveSubmissaoServicoCommand(dto, missaoUuid.toString());
    var resp = service.salvarSubmissao(cmd);

    assertNotNull(resp.getBody());
    assertEquals(missaoUuid.toString(), resp.getBody().get("id"));
    assertEquals(20L, resp.getBody().get("nrMissao"));
  }
}

