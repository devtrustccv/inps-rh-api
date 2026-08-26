package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.constants.SoatStatus;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoRequestDTO;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoResponseDTO;
import cv.inps.rh.processamento.application.dto.SoapRowResponseDTO;
import cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosInstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosInstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SoatEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoatService {

  private final SoatEntityRepository soatRepository;
  private final DadosInstituicaoEntityRepository dadosInstituicaoRepository;
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public WrapperListDTO list(Integer anoReferente, Integer mesReferente, Integer page, Integer size) {

    var data = soatRepository.findSoatPage(anoReferente, mesReferente, PageRequest.of(page, size));

    var ids = data.getContent().stream().map(SoatEntity::getId).toList();

    Map<Long, SoatAggregateDTO> agregadosPorId = ids.isEmpty()
        ? Map.of()
        : soatRepository.findAgregadosByIds(ids).stream()
        .collect(Collectors.toMap(SoatAggregateDTO::soatId, a -> a));

    var soatStatus = SoatStatus.codeDescriptionMap();

    var resultPage = data.map(s -> {
          var agregado = agregadosPorId.getOrDefault(
              s.getId(), new SoatAggregateDTO(s.getId(), BigDecimal.ZERO, 0L));

          return new SoapRowResponseDTO(
              s.getUuid(),
              "%d%02d".formatted(s.getAnoReferente(), s.getMesReferente()),
              s.getCreatedDate(),
              agregado.totalRemuneracao(),
              agregado.totalColaboradores(),
              soatStatus.getOrDefault(s.getEstado(), s.getEstado())
          );
        })
        .stream()
        .toList();

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(resultPage);

    return response;
  }

  public void finalizarSoat(String uuid) {
    var soat = soatRepository.findByUuidOrThrow(uuid);
    soat.setEstado(SoatStatus.F.getCode());
    soatRepository.save(soat);
  }

  @Transactional
  public void criarSoat(Integer ano, Integer mes) {
    entityManager.createStoredProcedureQuery("RH_PK_GERA_SOAT_DB.REGISTAR_LISTA_SOAT")
        .registerStoredProcedureParameter("P_ANO", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("P_MES", String.class, ParameterMode.IN)
        .setParameter("P_ANO", ano)
        .setParameter("P_MES", "%02d".formatted(mes))
        .execute();
  }

  @Transactional
  public synchronized DadosInstituicaoResponseDTO salvarDadosInstituicao(DadosInstituicaoRequestDTO request) {

    dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .ifPresent(atual -> {
          atual.setEstado(Estado.I.getCode());
          dadosInstituicaoRepository.saveAndFlush(atual);
        });

    var novo = new DadosInstituicaoEntity();
    novo.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    novo.setEstado(Estado.A.getCode());
    apply(request, novo);

    return toResponse(dadosInstituicaoRepository.save(novo));
  }

  @Transactional(readOnly = true)
  public DadosInstituicaoResponseDTO obterDadosInstituicaoAtual() {
    return dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .map(this::toResponse)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Active institution data not found"));
  }

  private void apply(DadosInstituicaoRequestDTO request, DadosInstituicaoEntity entity) {
    entity.setNome(request.getNome());
    entity.setNif(request.getNif());
    entity.setCodCae(request.getCodCae());
    entity.setAtividadeEconomica(request.getAtividadeEconomica());
    entity.setNumCertidaoComercial(request.getNumCertidaoComercial());
    entity.setDataValidade(request.getDataValidade());
    entity.setTelefone(request.getTelefone());
    entity.setLocalidade(request.getLocalidade());
    entity.setEmail(request.getEmail());
    entity.setMorada(request.getMorada());
    entity.setConcelhoId(request.getConcelhoId());
  }

  private DadosInstituicaoResponseDTO toResponse(DadosInstituicaoEntity entity) {
    return new DadosInstituicaoResponseDTO(
        entity.getUuid(),
        entity.getNome(),
        entity.getNif(),
        entity.getCodCae(),
        entity.getAtividadeEconomica(),
        entity.getNumCertidaoComercial(),
        entity.getDataValidade(),
        entity.getTelefone(),
        entity.getLocalidade(),
        entity.getEmail(),
        entity.getMorada(),
        entity.getConcelhoId(),
        entity.getEstado()
    );
  }

}
