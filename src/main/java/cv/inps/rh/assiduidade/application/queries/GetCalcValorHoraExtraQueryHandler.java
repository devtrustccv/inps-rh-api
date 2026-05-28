package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.CalcValorHoraExtraDTO;
import cv.inps.rh.assiduidade.application.services.HoraExtraServiceWrite;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class GetCalcValorHoraExtraQueryHandler
    implements QueryHandler<GetCalcValorHoraExtraQuery, ResponseEntity<CalcValorHoraExtraDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCalcValorHoraExtraQueryHandler.class);

  private final HoraExtraServiceWrite horaExtraServiceWrite;
  private final FuncionarioRules funcionarioRules;

  public GetCalcValorHoraExtraQueryHandler(HoraExtraServiceWrite horaExtraServiceWrite,
                                            FuncionarioRules funcionarioRules) {
    this.horaExtraServiceWrite = horaExtraServiceWrite;
    this.funcionarioRules = funcionarioRules;
  }

  @IgrpQueryHandler
  public ResponseEntity<CalcValorHoraExtraDTO> handle(GetCalcValorHoraExtraQuery query) {

    LOGGER.debug("GetCalcValorHoraExtraQuery: {}", query);

    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(query.getFuncionarioUuid()));
    if (tiprel == null)
      throw IgrpResponseStatusException.badRequest("Colaborador sem tipo de relacionamento activo");

    var valor = horaExtraServiceWrite.calcularValorHoraExtra(
        tiprel.getId(),
        LocalDate.parse(query.getDataInicio()),
        LocalDate.parse(query.getDataFim()),
        query.getPercentagemReferente(),
        query.getHorasDiaria());

    return ResponseEntity.ok(new CalcValorHoraExtraDTO(valor));
  }

}
