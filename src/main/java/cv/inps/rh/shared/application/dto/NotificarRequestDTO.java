package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Bloco de notificação do ecrã, para embutir em qualquer request que o precise.
 *
 * <p>Corresponde aos quatro campos da spec: "Notificar?", "Destinatários da notificação",
 * "Email do Responsável" e "Mensagem da notificação". Quando {@code notificar} é falso os
 * restantes campos são ignorados — o frontend esconde-os, mas o backend não confia nisso.</p>
 *
 * <p>Não confundir com o {@code NotificacaoRequestDTO} do módulo configuracao, que é o CRUD dos
 * templates em RH_T_PARAM_NOTIFICACAO.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class NotificarRequestDTO {

  /** "Notificar?" — quando falso ou nulo, nenhum email é enviado. */
  private Boolean notificar;

  /**
   * Valores do domínio DESTINATARIO_NOTIFICACAO escolhidos no multiselect. São etiquetas
   * (COLABORADOR, RESPONSAVEL_COLABORADOR, RESPONSAVEL_REGISTO), nunca emails — a resolução
   * para endereços é do backend.
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
