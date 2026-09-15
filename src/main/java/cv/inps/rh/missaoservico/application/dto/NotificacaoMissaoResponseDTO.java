package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Uma notificação emitida no âmbito da missão — ecrã "Ver Notificação" da Lista Missão.
 *
 * <p>As notificações da missão não ficam todas com a mesma referência: o pedido de proposta fica no
 * prestador, o envio da requisição na requisição, o aviso de logística no colaborador e o
 * cancelamento na própria missão. Este DTO nivela-as para o ecrã.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class NotificacaoMissaoResponseDTO {
  private UUID uuid;
  private String tipoNotificacao;         // MISSAO_PRESTADOR | MISSAO_REQUISICAO | MISSAO_LOGISTICA_COLABORADOR | MISSAO_CANCELAMENTO | …
  private String assunto;
  private String mensagem;
  private String email;                   // null quando é aviso no portal do colaborador
  private String nomeReceptor;
  private LocalDate dataEnvio;
  private String estado;
  private String origem;                  // a que entidade da missão está referenciada
}
