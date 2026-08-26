package cv.inps.rh.processamento.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SoatPdfRowDTO {

  private final String nome;
  private final String tipoDocumento;
  private final String numeroDocumento;
  private final String dataValidadeDocumento;
  private final String nif;
  private final String dataNascimento;
  private final String sexo;
  private final String situacao;
  private final String profissao;
  private final String aprendizOuEstagiario;
  private final BigDecimal horasSemana;
  private final String unidadeRetribuicao;
  private final BigDecimal retribuicao;
  private final BigDecimal retribuicaoAnual;
  private final String temporariamenteEstrangeiro;
  private final String observacoes;
}
