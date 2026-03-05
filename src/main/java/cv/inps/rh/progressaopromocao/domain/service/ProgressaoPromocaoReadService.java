package cv.inps.rh.progressaopromocao.domain.service;

import cv.inps.rh.progressaopromocao.application.dto.ProgressaoPromocaoRowDTO;
import cv.inps.rh.progressaopromocao.application.queries.GetHistoricoProgressaPromocaoQuery;
import cv.inps.rh.progressaopromocao.application.queries.GetListaSimulacaoProgressaPromocaoQuery;
import cv.inps.rh.progressaopromocao.application.queries.GetListaValidacaoProgressaPromocaoQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SimEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class ProgressaoPromocaoReadService {

  private final EvolucaoCarreiraEntityRepository evolucaoCarreiraEntityRepository;
  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final ValEvolucaoCarreiraEntityRepository valEvolucaoCarreiraEntityRepository;

  public Page<ProgressaoPromocaoRowDTO> getHistoricoProgressaoPromocao(GetHistoricoProgressaPromocaoQuery query) {
    return evolucaoCarreiraEntityRepository.findProgressaoPromocaoWithFilters(
        query.getProgressaoPromocao(),
        DateFormatter.stringToLocalDate(query.getDataDe()),
        DateFormatter.stringToLocalDate(query.getDataAte()),
        ofNullable(query.getColaborador()).map(String::trim).orElse(null),
        StringUtils.hasText(query.getCarreiraId()) ? UUID.fromString(query.getCarreiraId()) : null,
        buildPageable(query.getPage(), query.getSize())
    );
  }

  public Page<ProgressaoPromocaoRowDTO> getSimulacaoProgressaoPromocaoSimulacao(GetListaSimulacaoProgressaPromocaoQuery query) {
    return simEvolucaoCarreiraEntityRepository.findProgressaoPromocaoWithFilters(
        query.getProgressaoPromocao(),
        DateFormatter.stringToLocalDate(query.getDataDe()),
        DateFormatter.stringToLocalDate(query.getDataAte()),
        ofNullable(query.getColaborador()).map(String::trim).orElse(null),
        StringUtils.hasText(query.getCarreiraId()) ? UUID.fromString(query.getCarreiraId()) : null,
        buildPageable(query.getPage(), query.getSize())
    );
  }

  public Page<ProgressaoPromocaoRowDTO> getValidacaoProgressaoPromocao(GetListaValidacaoProgressaPromocaoQuery query) {
    return valEvolucaoCarreiraEntityRepository.findProgressaoPromocaoWithFilters(
        query.getProgressaoPromocao(),
        DateFormatter.stringToLocalDate(query.getDataDe()),
        DateFormatter.stringToLocalDate(query.getDataAte()),
        ofNullable(query.getColaborador()).map(String::trim).orElse(null),
        StringUtils.hasText(query.getCarreiraId()) ? UUID.fromString(query.getCarreiraId()) : null,
        buildPageable(query.getPage(), query.getSize())
    );
  }

  private Pageable buildPageable(String page, String size) {
    return PageRequest.of(
        Integer.parseInt(page),
        Integer.parseInt(size),
        Sort.by(Sort.Direction.DESC, "dataReferente")
    );
  }
}
