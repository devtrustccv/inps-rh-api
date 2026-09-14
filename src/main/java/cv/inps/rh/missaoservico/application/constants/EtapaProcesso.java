package cv.inps.rh.missaoservico.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Arrays;

/**
 * Etapas de um processo de missão, pela ordem do fluxo — espelha o domínio TIPO_PROCESSO_ETAPA
 * (referência MISSAO_SERVICO). A ordem da declaração é a ordem do processo.
 */
public enum EtapaProcesso {

  PRESTADOR_SERVICO("Prestador Serviço"),
  EMISSAO_REQUISICAO("Emissão Requisição"),
  LOGISTICA("Processamento Logístico"),
  VALIDACAO_UGAL("Validação UGAL"),
  APROVACAO_RH("Aprovação RH"),
  CABIMENTO("Cabimento"),
  AUTORIZACAO("Autorização"),
  PAGAMENTO("Pagamento");

  private final String descricao;

  EtapaProcesso(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  /** Etapa a partir do código gravado; null se o código for nulo ou desconhecido. */
  public static EtapaProcesso fromCode(String code) {
    if (code == null)
      return null;
    return Arrays.stream(values()).filter(e -> e.name().equals(code)).findFirst().orElse(null);
  }

  public static EtapaProcesso fromCodeOrThrow(String code) {
    var etapa = fromCode(code);
    if (etapa == null) {
      throw IgrpResponseStatusException.badRequest("Etapa inválida: " + code);
    }
    return etapa;
  }
}
