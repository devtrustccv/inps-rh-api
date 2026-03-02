package cv.inps.rh.shared.application.constants.custom;

import io.prometheus.client.SimpleCollector;

import java.util.Optional;

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
  ORDEM_SERVICO("Ordem de Serviço");

  private final String descricao;

  Referencia(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
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
