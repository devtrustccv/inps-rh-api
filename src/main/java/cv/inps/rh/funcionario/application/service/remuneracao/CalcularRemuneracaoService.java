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

    // Remuneração Bruta = salario + Σ(subsidios) — calculado em Java conforme especificação
    BigDecimal totalSubsidios = request.getSubsidios().stream()
        .map(s -> s.getValor() != null ? s.getValor() : BigDecimal.ZERO)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal remuneracaoBruta = request.getSalario().add(totalSubsidios);

    // Chama processamento_salarial_db.CalcularDesAtual via bloco PL/SQL anónimo
    BigDecimal[] resultado = calcularRemuneracaoRepository.calcularDesAtual(request);

    BigDecimal remuneracaoLiquida = resultado[0];
    BigDecimal totalDesconto = resultado[1];

    var response = new CalcularRemuneracaoResponseDTO();
    response.setRemuneracaoBruta(remuneracaoBruta);
    response.setTotalDesconto(totalDesconto);
    response.setRemuneracaoLiquida(remuneracaoLiquida);

    return response;
  }

}
