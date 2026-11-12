package cv.inps.rh.shared.application.constants;

import java.util.Arrays;

/**
 * Estado de RH_T_NOTIFICACAO.ESTADO = valor do domínio ESTADO_NOTIFICACAO (spec Transversal, Lista de
 * Notificação). Antes cada serviço gravava texto livre ("Pendente"/"Enviado"/"Erro") e o filtro do ecrã,
 * que envia o valor do domínio, nunca encontrava nada (QA TR-20/22). ERRO acrescentado ao domínio a 23/10.
 */
public enum EstadoNotificacao {

  POR_ENVIAR("Por Enviar"),
  ENVIADO("Enviado"),
  ERRO("Erro");

  private final String descricao;

  EstadoNotificacao(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  /** Descrição para o ecrã; um código desconhecido volta tal como está. */
  public static String descricaoDe(String codigo) {
    if (codigo == null) return null;
    return Arrays.stream(values()).filter(e -> e.name().equals(codigo))
        .map(EstadoNotificacao::getDescricao).findFirst().orElse(codigo);
  }
}
