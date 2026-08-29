/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class NotificacaoInfoDTO  {

  private Long id;
  private String tipoNotificacao;
  private String assunto;
  private String corpo;
  private String nomeReceptor;
  private UUID funcionarioId;
  private String email;
  private LocalDate dataEnvio;
  private String estado;

}
