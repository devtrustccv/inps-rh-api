package cv.inps.rh.progressaopromocao.domain.service;

import cv.inps.rh.progressaopromocao.application.dto.ListaProgressaoPromocaoDTO;
import cv.inps.rh.progressaopromocao.application.queries.GetListaProgressaPromocaoQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class ProgressaoPromocaoReadService {

  private final EvolucaoCarreiraEntityRepository evolucaoCarreiraEntityRepository;

  public ListaProgressaoPromocaoDTO getProgressaoPromocaoData(GetListaProgressaPromocaoQuery query) {

    var dataDe = DateFormatter.stringToLocalDate(query.getDataDe());
    var dataAte = DateFormatter.stringToLocalDate(query.getDataAte());
    var pageable = PageRequest.of(
        Integer.parseInt(query.getPage()),
        Integer.parseInt(query.getSize()),
        Sort.by(Sort.Direction.DESC, "dataReferente")
    );

    var page = evolucaoCarreiraEntityRepository.findProgressaoPromocaoWithFilters(
        query.getProgressaoPromocao(),
        dataDe,
        dataAte,
        ofNullable(query.getColaborador()).map(String::trim).orElse(null),
        StringUtils.hasText(query.getCarreiraId()) ? UUID.fromString(query.getCarreiraId()) : null,
        pageable
    );

    var data = new ListaProgressaoPromocaoDTO();
    PageMapper.fillPagination(page, data);
    data.setContent(page.getContent());
    return data;
  }
}
