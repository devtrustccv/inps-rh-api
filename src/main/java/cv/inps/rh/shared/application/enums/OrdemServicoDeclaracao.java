package cv.inps.rh.shared.application.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OrdemServicoDeclaracao {

  CONVERSAO_CONTRATO("CONVERSAO_CONTRATO", "Conversão de Contratos", "ORDEM_SERVICO"),
  LICENCA_S_VENCIMENTO("LICENCA_S_VENCIMENTO", "Licença sem Vencimento", "ORDEM_SERVICO"),
  PROGRESSAO_CARGO("PROGRESSAO_CARGO", "Progressão Cargo", "ORDEM_SERVICO"),
  PROGRESSAO_CATEGORIA("PROGRESSAO_CATEGORIA", "Progressão Categoria", "ORDEM_SERVICO"),
  SUBSTITUICAO("SUBSTITUICAO", "Substituição", "ORDEM_SERVICO"),
  TRANSFERENCIA("TRANSFERENCIA", "Transferência", "ORDEM_SERVICO"),
  MOBILIDADE_INTERNA("MOBILIDADE_INTERNA", "Mobilidade Interna", "ORDEM_SERVICO"),
  REQUALIFICACAO("REQUALIFICACAO", "Requalificação", "ORDEM_SERVICO"),
  NOMEACAO("NOMEACAO", "Nomeação", "ORDEM_SERVICO"),
  FIM_COMISSAO_SERVICO("FIM_COMISSAO_SERVICO", "Fim de Comissão de Serviço", "ORDEM_SERVICO"),

  DECLARACAO_CREDITO_HABITACAO("DECLARACAO_CREDITO_HABITACAO", "Declaração efeito crédito habitação", "DECLARACAO"),
  DECLARACAO_EMEP("DECLARACAO_EMEP", "Declaração EMEP", "DECLARACAO"),
  DECLARACAO_CREDITO_BANCARIO("DECLARACAO_CREDITO_BANCARIO", "Declaração efeitos crédito bancário", "DECLARACAO"),
  DECLARACAO_VISTOS("DECLARACAO_VISTOS", "Declaração efeitos visto", "DECLARACAO");

  private final String codigo;
  private final String descricao;
  private final String referencia;

  OrdemServicoDeclaracao(String codigo, String descricao, String referencia) {
    this.codigo = codigo;
    this.descricao = descricao;
    this.referencia = referencia;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getDescricao() {
    return descricao;
  }

  public String getReferencia() {
    return referencia;
  }

  public static Optional<OrdemServicoDeclaracao> from(String codigo, String referencia) {
    return Arrays.stream(values())
        .filter(e -> e.getCodigo().equals(codigo)
            && e.getReferencia().equals(referencia))
        .findFirst();
  }

  public static final String DOMINIO = "ORDEM_SERVICO_DECLARACAO";
}
