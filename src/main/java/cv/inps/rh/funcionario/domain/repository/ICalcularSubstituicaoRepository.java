package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.application.queries.CalcularSubstituicaoQuery;
import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseItemDTO;

import java.util.List;

public interface ICalcularSubstituicaoRepository {

  List<CalcularSubstituicaoResponseItemDTO> calcularSubstituicao(CalcularSubstituicaoQuery query);

}
