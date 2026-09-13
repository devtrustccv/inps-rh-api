package cv.inps.rh.processamento.domain.service.baixamedica;

import cv.inps.rh.processamento.application.dto.BaixaMedicaDetailDTO;
import cv.inps.rh.processamento.application.dto.BaixaMedicaListDTO;
import cv.inps.rh.processamento.application.dto.PeriodoLicensaRowDTO;
import cv.inps.rh.processamento.application.queries.GetBaixaMedicaQuery;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AbonosBeneficiosDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AbonosBeneficiosEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class BaixaMedicaReadService {

  private final AbonosBeneficiosEntityRepository abonosRepository;
  private final AbonosBeneficiosDetalheEntityRepository abonosDetalheRepository;
  private final BaixaMedicaServiceWrite baixaMedicaServiceWrite;

  @Transactional(readOnly = true)
  public BaixaMedicaDetailDTO getBaixaMedica(GetBaixaMedicaQuery query) {
    var abonoUuid = UUID.fromString(query.getBaixaMedicaId());
    var baixaMedicaDetailDTO = abonosRepository.getBaixaMedica(abonoUuid)
        .map(obj -> {
          obj.setEstadodesc(ofNullable(obj.getEstado()).map(Estado::getDescription).orElse(null));
          var calculation = baixaMedicaServiceWrite.chamarProcedure(
              obj.getRelacionamentoId(),
              obj.getDataInicio(),
              obj.getDataFim(),
              obj.getParamSitId(),
              null);
          obj.setCalculo(calculation);
          return obj;
        })
        .orElseThrow(() -> IgrpResponseStatusException.notFound("AbonosBeneficiosEntity not found for uuid: " + query.getBaixaMedicaId()));

    var periodos = abonosDetalheRepository.findByAbonoBenef_Uuid(abonoUuid)
        .stream()
        .map(obj -> {
          var periodo = new PeriodoLicensaRowDTO();
          periodo.setContinuidade(null);
          periodo.setDataInicio(obj.getDataInicio());
          periodo.setDataFim(obj.getDataFim());
          periodo.setId(obj.getUuid());
          return periodo;
        })
        .toList();

    baixaMedicaDetailDTO.setPeriodos(periodos);

    return baixaMedicaDetailDTO;
  }

  public BaixaMedicaListDTO getListaBaixaMedica(GetListaBaixamedicaQuery query) {

    var pageRequest = PageRequest.of(query.getPage(), query.getSize());
    var startDate = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var endDate = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;
    var nomefuncionario = StringUtils.hasText(query.getNomeFuncionario()) ? query.getNomeFuncionario() : null;

    var pageData = abonosRepository.getListaBaixamedica(
        nomefuncionario,
        query.getDireccaoId(),
        startDate,
        endDate,
        query.getTipoAbonoBeneficioId(),
        null,
        pageRequest
    );

    pageData.getContent()
        .forEach(obj -> obj.setEstadodesc(ofNullable(obj.getEstado()).map(Estado::getDescription).orElse(null)));

    var response = new BaixaMedicaListDTO();
    PageMapper.fillPagination(pageData, response);
    response.setContent(pageData.getContent());
    return response;
  }
}
