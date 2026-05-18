package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.AumentoListDTO;
import cv.inps.rh.processamento.application.dto.AumentoSalarialResponseDTO;
import cv.inps.rh.processamento.application.dto.ColaboradorAumentoDTO;
import cv.inps.rh.processamento.application.queries.GetColaboresAumentoSalarialQuery;
import cv.inps.rh.processamento.application.queries.GetListaAumentoSalarialQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSimulacaoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.Year;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AumentoSalarialReadService {

  private final AumentoSalarialEntityRepository aumentoSalarialEntityRepository;
  private final AumentoSimulacaoEntityRepository aumentoSimulacaoEntityRepository;

  public AumentoListDTO getProcessamentoSalarial(GetListaAumentoSalarialQuery query) {

    var pageRequest = PageRequest.of(query.getPage(), query.getSize());

    var year = Objects.nonNull(query.getAno()) ? Year.of(query.getAno()) : null;
    var startDate = Objects.nonNull(year) ? year.atDay(1) : null;
    var endDate = Objects.nonNull(year) ? year.atMonth(Month.DECEMBER).atEndOfMonth() : null;

    var page = aumentoSalarialEntityRepository.list(startDate, endDate, pageRequest);

    var response = new AumentoListDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  @Transactional(readOnly = true)
  public AumentoSalarialResponseDTO getDetalhesAumentoSalarial(String salaryIncreaseId) {

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId).orElseThrow();

    var response = new AumentoSalarialResponseDTO();
    response.setDesignacao(salaryIncrease.getDescricao());
    response.setMotivo(salaryIncrease.getMotivo());
    response.setDataReferente(salaryIncrease.getDataReferente());
    response.setPercentagem(salaryIncrease.getPercentagem());
    response.setEstado(salaryIncrease.getEstado());

    Optional.ofNullable(salaryIncrease.getPccs())
        .ifPresent(obj -> {
          response.setPccsId(obj.getUuid().toString());
          response.setPccsDescricao(obj.getDescricao());
        });

    return response;
  }

  public ColaboradorAumentoDTO getColaboradores(GetColaboresAumentoSalarialQuery query) {

    var pageRequest = PageRequest.of(query.getPage(), query.getSize());

    var page = aumentoSimulacaoEntityRepository.list(
        query.getDirecaoId(),
        query.getOrganicaId(),
        pageRequest
    );

    var response = new ColaboradorAumentoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }
}
