package cv.inps.rh.shared.application.constants.custom;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Referencia {
  RENDIMENTO("Rendimento"),
  DESCONTO("Desconto"),
  REGISTO_COLABORADOR("Registo de Colaborador"),
  CONTRATO("Contrato"),
  RENOVACAO_CONTRATO("Renovação de Contrato"),
  FAMILIA("Família"),
  ESTADO_COLABORADOR("Estado do Colaborador"),
  DADOS_ACADEMICOS("Dados Académicos"),
  DADOS_BANCARIOS("Dados Bancários"),
  DADOS_PESSOAIS("Dados Pessoais"),
  MOBILIDADE("Mobilidade"),
  REGIME("Regime"),
  SITUACAO_LABORAL("Situação Laboral"),
  SUBSTITUICAO("Substituição"),
  CARREIRA("Carreira"),
  FALTA("Falta"),
  DISPENSA("Dispensa"),
  HORA_EXTRA("Hora Extra"),
  FERIA("Féria"),
  JUSTIFICAR_FALTA("Justificar Falta"),
  ORDEM_SERVICO("Ordem de Serviço"),
  BAIXA_MEDICA("Baixa Médica");

  private final String descricao;

  Referencia(String descricao) {
    this.descricao = descricao;
  }

  public static Optional<Referencia> fromString(String value) {
    if (value == null || value.isBlank()) {
      return Optional.empty();
    }

    try {
      return Optional.of(Referencia.valueOf(value.toUpperCase()));
    } catch (IllegalArgumentException | NullPointerException e) {
      return Optional.empty();
    }
  }
}
