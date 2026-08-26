package cv.inps.rh.processamento.domain.service;

import cv.inps.rh.processamento.application.dto.DadosApoliceRequestDTO;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosApoliceEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosInstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosApoliceEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosInstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SoatEntityRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoatServiceTest {

  @Mock
  private SoatEntityRepository soatRepository;

  @Mock
  private DadosInstituicaoEntityRepository dadosInstituicaoRepository;

  @Mock
  private DadosApoliceEntityRepository dadosApoliceRepository;

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private SoatService service;

  @Test
  void shouldCreateDadosInstituicaoWhenNoRowExists() {
    var request = request("INPS", 255555550L);
    when(dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode()))
        .thenReturn(Optional.empty());
    when(dadosInstituicaoRepository.save(any(DadosInstituicaoEntity.class))).thenAnswer(invocation -> {
      var entity = invocation.getArgument(0, DadosInstituicaoEntity.class);
      entity.setId(1L);
      return entity;
    });

    var response = service.salvarDadosInstituicao(request);

    var captor = ArgumentCaptor.forClass(DadosInstituicaoEntity.class);
    verify(dadosInstituicaoRepository).save(captor.capture());
    var saved = captor.getValue();

    assertAll(
        () -> assertEquals(1L, saved.getId()),
        () -> assertFalse(saved.getUuid().isBlank()),
        () -> assertEquals(request.getNome(), saved.getNome()),
        () -> assertEquals(request.getNif(), saved.getNif()),
        () -> assertEquals(request.getCodCae(), saved.getCodCae()),
        () -> assertEquals(request.getAtividadeEconomica(), saved.getAtividadeEconomica()),
        () -> assertEquals(request.getNumCertidaoComercial(), saved.getNumCertidaoComercial()),
        () -> assertEquals(request.getDataValidade(), saved.getDataValidade()),
        () -> assertEquals(request.getTelefone(), saved.getTelefone()),
        () -> assertEquals(request.getTelemovel(), saved.getTelemovel()),
        () -> assertEquals(request.getLocalidade(), saved.getLocalidade()),
        () -> assertEquals(request.getEmail(), saved.getEmail()),
        () -> assertEquals(request.getMorada(), saved.getMorada()),
        () -> assertEquals(request.getConcelhoId(), saved.getConcelhoId()),
        () -> assertEquals(Estado.A.getCode(), saved.getEstado()),
        () -> assertEquals(saved.getUuid(), response.getUuid()),
        () -> assertEquals(request.getNome(), response.getNome()),
        () -> assertEquals(request.getNif(), response.getNif()),
        () -> assertEquals(request.getCodCae(), response.getCodCae()),
        () -> assertEquals(request.getAtividadeEconomica(), response.getAtividadeEconomica()),
        () -> assertEquals(request.getNumCertidaoComercial(), response.getNumCertidaoComercial()),
        () -> assertEquals(request.getDataValidade(), response.getDataValidade()),
        () -> assertEquals(request.getTelefone(), response.getTelefone()),
        () -> assertEquals(request.getTelemovel(), response.getTelemovel()),
        () -> assertEquals(request.getLocalidade(), response.getLocalidade()),
        () -> assertEquals(request.getEmail(), response.getEmail()),
        () -> assertEquals(request.getMorada(), response.getMorada()),
        () -> assertEquals(request.getConcelhoId(), response.getConcelhoId()),
        () -> assertEquals(Estado.A.getCode(), response.getEstado())
    );
  }

  @Test
  void shouldInactivateExistingRowAndCreateANewActiveVersion() {
    var existing = new DadosInstituicaoEntity();
    existing.setId(42L);
    existing.setUuid("existing-uuid");
    existing.setNome("Old name");
    existing.setNif(100L);
    existing.setEstado(Estado.A.getCode());

    var request = request("Updated INPS", 299999990L);
    when(dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode()))
        .thenReturn(Optional.of(existing));
    when(dadosInstituicaoRepository.saveAndFlush(existing)).thenReturn(existing);
    when(dadosInstituicaoRepository.save(any(DadosInstituicaoEntity.class)))
        .thenAnswer(invocation -> {
          var entity = invocation.getArgument(0, DadosInstituicaoEntity.class);
          if (entity.getId() == null) {
            entity.setId(43L);
          }
          return entity;
        });

    var response = service.salvarDadosInstituicao(request);

    var activeCaptor = ArgumentCaptor.forClass(DadosInstituicaoEntity.class);
    verify(dadosInstituicaoRepository).saveAndFlush(existing);
    verify(dadosInstituicaoRepository).save(activeCaptor.capture());
    var active = activeCaptor.getValue();

    assertAll(
        () -> assertEquals(42L, existing.getId()),
        () -> assertEquals("existing-uuid", existing.getUuid()),
        () -> assertEquals(Estado.I.getCode(), existing.getEstado()),
        () -> assertNotSame(existing, active),
        () -> assertEquals(43L, active.getId()),
        () -> assertNotEquals("existing-uuid", active.getUuid()),
        () -> assertEquals(Estado.A.getCode(), active.getEstado()),
        () -> assertEquals(request.getNome(), active.getNome()),
        () -> assertEquals(request.getNif(), active.getNif()),
        () -> assertEquals(request.getCodCae(), active.getCodCae()),
        () -> assertEquals(request.getAtividadeEconomica(), active.getAtividadeEconomica()),
        () -> assertEquals(request.getNumCertidaoComercial(), active.getNumCertidaoComercial()),
        () -> assertEquals(request.getDataValidade(), active.getDataValidade()),
        () -> assertEquals(request.getTelefone(), active.getTelefone()),
        () -> assertEquals(request.getTelemovel(), active.getTelemovel()),
        () -> assertEquals(request.getLocalidade(), active.getLocalidade()),
        () -> assertEquals(request.getEmail(), active.getEmail()),
        () -> assertEquals(request.getMorada(), active.getMorada()),
        () -> assertEquals(request.getConcelhoId(), active.getConcelhoId()),
        () -> assertEquals(active.getUuid(), response.getUuid()),
        () -> assertEquals(Estado.A.getCode(), response.getEstado()),
        () -> assertEquals("Updated INPS", response.getNome())
    );
  }

  @Test
  void shouldGetCurrentActiveDadosInstituicao() {
    var active = new DadosInstituicaoEntity();
    active.setId(7L);
    active.setUuid("active-uuid");
    active.setNome("INPS Active");
    active.setNif(255555550L);
    active.setTelemovel(9912345L);
    active.setEstado(Estado.A.getCode());
    when(dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode()))
        .thenReturn(Optional.of(active));

    var response = service.obterDadosInstituicaoAtual();

    assertAll(
        () -> assertEquals("active-uuid", response.getUuid()),
        () -> assertEquals("INPS Active", response.getNome()),
        () -> assertEquals(255555550L, response.getNif()),
        () -> assertEquals(9912345L, response.getTelemovel()),
        () -> assertEquals(Estado.A.getCode(), response.getEstado())
    );
  }

  @Test
  void shouldFailWhenThereIsNoActiveDadosInstituicao() {
    when(dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode()))
        .thenReturn(Optional.empty());

    assertThrows(
        IgrpResponseStatusException.class,
        () -> service.obterDadosInstituicaoAtual()
    );
  }

  @Test
  void shouldCreateActiveDadosApoliceWhenIslandHasNoPolicy() {
    var request = apoliceRequest("SAL-001", 1L);
    when(dadosApoliceRepository.findFirstByIlhaIdAndEstadoOrderByIdDesc(
        1L, Estado.A.getCode())).thenReturn(Optional.empty());
    when(dadosApoliceRepository.save(any(DadosApoliceEntity.class))).thenAnswer(invocation -> {
      var entity = invocation.getArgument(0, DadosApoliceEntity.class);
      entity.setId(1L);
      return entity;
    });

    var response = service.salvarDadosApolice(request);

    var captor = ArgumentCaptor.forClass(DadosApoliceEntity.class);
    verify(dadosApoliceRepository).save(captor.capture());
    var saved = captor.getValue();

    assertAll(
        () -> assertEquals(1L, saved.getId()),
        () -> assertFalse(saved.getUuid().isBlank()),
        () -> assertEquals(request.getNumApolice(), saved.getNumApolice()),
        () -> assertEquals(request.getIlhaId(), saved.getIlhaId()),
        () -> assertEquals(request.getDataApolice(), saved.getDataApolice()),
        () -> assertEquals(Estado.A.getCode(), saved.getEstado()),
        () -> assertEquals(saved.getUuid(), response.getUuid()),
        () -> assertEquals(request.getNumApolice(), response.getNumApolice()),
        () -> assertEquals(request.getIlhaId(), response.getIlhaId()),
        () -> assertEquals(request.getDataApolice(), response.getDataApolice()),
        () -> assertEquals(Estado.A.getCode(), response.getEstado())
    );
  }

  @Test
  void shouldInactivateIslandPolicyAndCreateNewActiveVersion() {
    var existing = apolice("old-policy", "SAL-001", 1L);
    var request = apoliceRequest("SAL-002", 1L);
    when(dadosApoliceRepository.findFirstByIlhaIdAndEstadoOrderByIdDesc(
        1L, Estado.A.getCode())).thenReturn(Optional.of(existing));
    when(dadosApoliceRepository.saveAndFlush(existing)).thenReturn(existing);
    when(dadosApoliceRepository.save(any(DadosApoliceEntity.class))).thenAnswer(invocation -> {
      var entity = invocation.getArgument(0, DadosApoliceEntity.class);
      entity.setId(2L);
      return entity;
    });

    var response = service.salvarDadosApolice(request);

    var activeCaptor = ArgumentCaptor.forClass(DadosApoliceEntity.class);
    verify(dadosApoliceRepository).saveAndFlush(existing);
    verify(dadosApoliceRepository).save(activeCaptor.capture());
    var active = activeCaptor.getValue();

    assertAll(
        () -> assertEquals(Estado.I.getCode(), existing.getEstado()),
        () -> assertNotSame(existing, active),
        () -> assertNotEquals(existing.getUuid(), active.getUuid()),
        () -> assertEquals("SAL-002", active.getNumApolice()),
        () -> assertEquals(1L, active.getIlhaId()),
        () -> assertEquals(Estado.A.getCode(), active.getEstado()),
        () -> assertEquals(active.getUuid(), response.getUuid())
    );
  }

  @Test
  void shouldListOnlyActiveDadosApoliceOrderedByIsland() {
    var sal = apolice("sal-policy", "SAL-001", 1L);
    var saoVicente = apolice("sv-policy", "SV-001", 2L);
    when(dadosApoliceRepository.findAllByEstadoOrderByIlhaIdAsc(Estado.A.getCode()))
        .thenReturn(List.of(sal, saoVicente));

    var response = service.listarDadosApoliceAtivos();

    assertAll(
        () -> assertEquals(2, response.size()),
        () -> assertEquals("sal-policy", response.get(0).getUuid()),
        () -> assertEquals(1L, response.get(0).getIlhaId()),
        () -> assertEquals("sv-policy", response.get(1).getUuid()),
        () -> assertEquals(2L, response.get(1).getIlhaId())
    );
  }

  private DadosInstituicaoRequestDTO request(String nome, Long nif) {
    return new DadosInstituicaoRequestDTO(
        nome,
        nif,
        "CAE-01",
        "Seguranca social",
        "CERT-01",
        LocalDate.of(2030, 12, 31),
        2600000L,
        9912345L,
        "Praia",
        "inps@example.cv",
        "Plateau",
        1L
    );
  }

  private DadosApoliceRequestDTO apoliceRequest(String numero, Long ilhaId) {
    return new DadosApoliceRequestDTO(
        numero,
        ilhaId,
        LocalDate.of(2026, 1, 1)
    );
  }

  private DadosApoliceEntity apolice(String uuid, String numero, Long ilhaId) {
    var entity = new DadosApoliceEntity();
    entity.setId(1L);
    entity.setUuid(uuid);
    entity.setNumApolice(numero);
    entity.setIlhaId(ilhaId);
    entity.setDataApolice(LocalDate.of(2025, 1, 1));
    entity.setEstado(Estado.A.getCode());
    return entity;
  }
}
