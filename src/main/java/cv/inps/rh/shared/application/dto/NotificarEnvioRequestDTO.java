package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pedido de envio do ecrã de notificação. Junta a origem do envio (que registo o motivou, e a
 * quem diz respeito) ao bloco de campos preenchido pelo utilizador.
 *
 * <p>Os campos de referência são obrigatórios porque {@code RH_T_NOTIFICACAO.REFERENCIA_ID} e
 * {@code REFERENCIA_NAME} são NOT NULL: toda a notificação fica ancorada a um registo de origem,
 * que é o que permite ao RH saber depois porque é que um email foi enviado.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class NotificarEnvioRequestDTO {

  /** Tipo de notificação; escolhe o template em RH_T_PARAM_NOTIFICACAO (domínio TIPO_ALERTA_NOTIFICACAO). */
  @NotBlank(message = "The field <tipoNotificacao> is required")
  private String tipoNotificacao;

  /** UUID do colaborador a que o registo diz respeito. Base para resolver COLABORADOR e o seu responsável. */
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

  /** Id do registo de origem (ex.: a falta, a dispensa, o pedido de férias). */
  private Long referenciaId;

  /** Tabela do registo de origem (ex.: RH_T_FALTA). */
  @NotBlank(message = "The field <referenciaName> is required")
  private String referenciaName;

  /** UUID do registo de origem, quando existe. */
  private String referenciaUuid;

  /** "Notificar?" — quando falso ou nulo, nenhum email é enviado. */
  private Boolean notificar;

  /**
   * Valores do domínio DESTINATARIO_NOTIFICACAO escolhidos no multiselect. São etiquetas
   * (COLABORADOR, RESPONSAVEL_COLABORADOR, RESPONSAVEL_REGISTO), nunca emails.
   */
  private List<String> destinatarios;

  /** Emails escolhidos no multiselect "Email do Responsável", já resolvidos pelo frontend. */
  private List<String> emailsAdicionais;

  /** Texto livre do campo "Mensagem da notificação". Vazio = usar o template configurado. */
  private String mensagem;

  public boolean deveNotificar() {
    return Boolean.TRUE.equals(notificar);
  }
}
