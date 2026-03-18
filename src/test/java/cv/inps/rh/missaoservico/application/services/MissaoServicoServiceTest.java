package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SubmeterMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissaoServicoServiceTest {

  @Mock
  private MissaoServicoEntityRepository missaoServicoRepository;

  @Mock
  private MissaoColaboradorEntityRepository missaoColaboradorRepository;

  @Mock
  private GeografiaEntityRepository geografiaRepository;

  @Mock
  private FuncionarioEntityRepository funcionarioRepository;

  @Mock
  private DocumentoEntityRepository documentoRepository;

  @Mock
  private DocumentoMapper documentoMapper;

  @InjectMocks
  private MissaoServicoService service;

  @Test
  void submeterCriaMissaoComNrMissaoSequencial() {
    var funUuid = UUID.randomUUID();

    var dto = new MissaoSubmissaoRequestDTO();
    dto.setPaisDestinoId(1L);
    dto.setDescricaoDestino("Praia");
    dto.setDataInicio(LocalDate.of(2026, 3, 1));
    dto.setDataFim(LocalDate.of(2026, 3, 3));
    dto.setAutorizadoPor("Direcao");
    dto.setDataAutorizacao(LocalDate.of(2026, 2, 20));
    dto.setColaboradores(List.of(new MissaoColaboradorRequestDTO(funUuid)));
    dto.setDocumentos(List.of());

    var command = new SubmeterMissaoServicoCommand(dto);

    var pais = new GeografiaEntity();
    pais.setId(1L);
    pais.setNome("Cabo Verde");

    var fun = new FuncionarioEntity();
    fun.setId(10L);
    fun.setUuid(funUuid);
    fun.setNumDocumento("12345");

    when(geografiaRepository.findByIdOrThrow(1L)).thenReturn(pais);
    when(funcionarioRepository.findByUuidOrThrow(funUuid)).thenReturn(fun);

    when(missaoServicoRepository.findMaxNrMissao()).thenReturn(10L);
    when(missaoServicoRepository.save(any(MissaoServicoEntity.class))).thenAnswer(invocation -> {
      var e = (MissaoServicoEntity) invocation.getArgument(0);
      e.setId(5L);
      return e;
    });

    when(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(anyString(), any(UUID.class))).thenReturn(List.of());
    when(documentoMapper.syncDocumentos(anyList(), eq(List.of()), anyString(), eq(5L), any(UUID.class), eq(1L), eq(null)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(missaoColaboradorRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    var response = service.submeter(command);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(11L, response.getBody().get("nrMissao"));
    assertNotNull(response.getBody().get("id"));
  }

  @Test
  void submeterRejeitaDatasInvalidas() {
    var dto = new MissaoSubmissaoRequestDTO();
    dto.setPaisDestinoId(1L);
    dto.setDescricaoDestino("Praia");
    dto.setDataInicio(LocalDate.of(2026, 3, 10));
    dto.setDataFim(LocalDate.of(2026, 3, 1));
    dto.setAutorizadoPor("Direcao");
    dto.setDataAutorizacao(LocalDate.of(2026, 2, 20));
    dto.setColaboradores(List.of(new MissaoColaboradorRequestDTO(UUID.randomUUID())));

    var command = new SubmeterMissaoServicoCommand(dto);

    assertThrows(IgrpResponseStatusException.class, () -> service.submeter(command));
  }
}
