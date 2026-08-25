package cv.inps.rh.processamento.domain.service;

import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.dto.SoapRowResponseDTO;
import cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.SoatEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoatService {

  private final SoatEntityRepository soatRepository;

  public WrapperListDTO list(Integer anoReferente, Integer mesReferente, Integer page, Integer size) {

    var data = soatRepository.findSoatPage(anoReferente, mesReferente, PageRequest.of(page, size));

    var ids = data.getContent().stream().map(SoatEntity::getId).toList();

    Map<Long, SoatAggregateDTO> agregadosPorId = ids.isEmpty()
        ? Map.of()
        : soatRepository.findAgregadosByIds(ids).stream()
        .collect(Collectors.toMap(SoatAggregateDTO::soatId, a -> a));

    var resultPage = data.map(s -> {
          var agregado = agregadosPorId.getOrDefault(
              s.getId(), new SoatAggregateDTO(s.getId(), BigDecimal.ZERO, 0L));
          return new SoapRowResponseDTO(
              s.getUuid(),
              "%d%02d".formatted(s.getAnoReferente(), s.getMesReferente()),
              s.getCreatedDate(),
              agregado.totalRemuneracao(),
              agregado.totalColaboradores(),
              s.getEstado()
          );
        })
        .stream()
        .toList();

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(resultPage);

    return response;
  }

}
