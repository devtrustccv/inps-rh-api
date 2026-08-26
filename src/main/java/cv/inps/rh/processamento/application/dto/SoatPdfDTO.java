package cv.inps.rh.processamento.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class SoatPdfDTO {

  private final String logoBase64;
  private final String dataEmissao;
  private final String referencia;
  private final String numeroApolice;
  private final String dataInicioApolice;
  private final String nomeInstituicao;
  private final String nifInstituicao;
  private final String codCae;
  private final String atividadeEconomica;
  private final String numeroCertidaoComercial;
  private final String dataValidadeCertidao;
  private final String telefone;
  private final String telemovel;
  private final String localidade;
  private final String email;
  private final String morada;
  private final String concelho;
  private final int totalPessoas;
  private final BigDecimal massaSalarialAnual;
  private final List<SoatPdfRowDTO> pessoas;
}
