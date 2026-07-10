package cv.inps.rh.funcionario.application.service.remuneracao;

import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoResponseDTO;
import cv.inps.rh.funcionario.domain.repository.ICalcularRemuneracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CalcularRemuneracaoService {

  private final ICalcularRemuneracaoRepository calcularRemuneracaoRepository;

  @Transactional(readOnly = true)
  public CalcularRemuneracaoResponseDTO calcular(CalcularRemuneracaoRequestDTO request) {

    // Chama processamento_salarial_db.CalcularDesAtual via bloco PL/SQL anónimo.
    // A procedure devolve OUT: [0]=p_total_remun (total bruto de remunerações,
    // base + subsídios recalculados a partir de RH_TIPO_MOVIMENTOS) e
    // [1]=P_total_pagamentos (total de descontos: IUR + INPS + outros).
    BigDecimal[] resultado = calcularRemuneracaoRepository.calcularDesAtual(request);

    BigDecimal remuneracaoBruta = resultado[0];
    BigDecimal totalDesconto = resultado[1];

    // Remuneração Líquida = bruta - descontos (ambas na mesma base, vinda da procedure)
    BigDecimal remuneracaoLiquida = remuneracaoBruta.subtract(totalDesconto);

    var response = new CalcularRemuneracaoResponseDTO();
    response.setRemuneracaoBruta(remuneracaoBruta);
    response.setTotalDesconto(totalDesconto);
    response.setRemuneracaoLiquida(remuneracaoLiquida);

    return response;
  }

}
