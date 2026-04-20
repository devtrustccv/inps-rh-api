package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoRequestDTO;

import java.math.BigDecimal;

public interface ICalcularRemuneracaoRepository {

  BigDecimal[] calcularDesAtual(CalcularRemuneracaoRequestDTO request);

}
