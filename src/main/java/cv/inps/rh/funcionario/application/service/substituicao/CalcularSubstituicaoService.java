package cv.inps.rh.funcionario.application.service.substituicao;

import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseDTO;
import cv.inps.rh.funcionario.application.queries.CalcularSubstituicaoQuery;
import cv.inps.rh.funcionario.domain.repository.ICalcularSubstituicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalcularSubstituicaoService {

  private final ICalcularSubstituicaoRepository calcularSubstituicaoRepository;

  @Transactional(readOnly = true)
  public CalcularSubstituicaoResponseDTO calcular(CalcularSubstituicaoQuery query) {

    var items = calcularSubstituicaoRepository.calcularSubstituicao(query);

    var response = new CalcularSubstituicaoResponseDTO();
    response.setItems(items);

    return response;
  }

}
