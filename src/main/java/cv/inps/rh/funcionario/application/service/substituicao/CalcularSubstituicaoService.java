package cv.inps.rh.funcionario.application.service.substituicao;

import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseDTO;
import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseItemDTO;
import cv.inps.rh.funcionario.application.queries.CalcularSubstituicaoQuery;
import cv.inps.rh.funcionario.domain.repository.ICalcularSubstituicaoRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalcularSubstituicaoService {

  private final ICalcularSubstituicaoRepository calcularSubstituicaoRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  @Transactional(readOnly = true)
  public CalcularSubstituicaoResponseDTO calcular(CalcularSubstituicaoQuery query) {

    LocalDate dataInicio = LocalDate.parse(query.getDataInicio());
    LocalDate dataFim    = LocalDate.parse(query.getDataFim());

    if (dataFim.isBefore(dataInicio)) {
      throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");
    }

    var tiprelDe   = tiposRelacionamentoEntityRepository.findById(query.getTiprelDeId())
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Colaborador substituto não encontrado: " + query.getTiprelDeId()));
    var tiprelPara = tiposRelacionamentoEntityRepository.findById(query.getTiprelParaId())
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Colaborador substituído não encontrado: " + query.getTiprelParaId()));

    BigDecimal valorDe   = tiprelDe.getSalario()  != null ? tiprelDe.getSalario()  : BigDecimal.ZERO;
    BigDecimal valorPara = tiprelPara.getSalario() != null ? tiprelPara.getSalario() : BigDecimal.ZERO;

    List<CalcularSubstituicaoResponseItemDTO> items = new ArrayList<>();

    YearMonth mesAtual = YearMonth.from(dataInicio);
    YearMonth mesFim   = YearMonth.from(dataFim);

    while (!mesAtual.isAfter(mesFim)) {

      LocalDate inicioMes = mesAtual.atDay(1);
      LocalDate fimMes    = mesAtual.atEndOfMonth();

      LocalDate diaInicio = dataInicio.isAfter(inicioMes) ? dataInicio : inicioMes;
      LocalDate diaFim    = dataFim.isBefore(fimMes)      ? dataFim    : fimMes;

      int nrDias = (int) (diaFim.toEpochDay() - diaInicio.toEpochDay() + 1);

      BigDecimal valorReceber = calcularSubstituicaoRepository.calcularValorReceber(nrDias, valorDe, valorPara);

      var item = new CalcularSubstituicaoResponseItemDTO();
      item.setMesAno(mesAtual.format(DateTimeFormatter.ofPattern("yyyyMM")));
      item.setNrDias(nrDias);
      item.setValorSubstituto(valorDe);
      item.setValorSubstituido(valorPara);
      item.setValorReceber(valorReceber);
      items.add(item);

      mesAtual = mesAtual.plusMonths(1);
    }

    var response = new CalcularSubstituicaoResponseDTO();
    response.setItems(items);
    return response;
  }

}
