package cv.inps.rh.shared.application.constants.custom;

import lombok.Getter;

@Getter
public enum RelatorioTemplate {

  CONVERSAO_CONTRATO(""),
  FIM_COMISSAO_SERVICO(""),
  LICENSA_SEM_VENCIMENTO(""),
  MOBILIDADE_INTERNA(""),
  OS_NOMEACAO(""),
  PROGRESSAO_CARGO(""),
  PROGRESSAO_CATEGORIA(""),
  REQUALIFICACAO(""),
  SUBSTITUICAO(""),
  TRANSFERENCIA("");

  private final String templateName;

  RelatorioTemplate(String templateName) {
    this.templateName = templateName;
  }
}
