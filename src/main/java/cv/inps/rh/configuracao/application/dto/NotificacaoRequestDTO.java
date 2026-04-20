/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class NotificacaoRequestDTO  {

  @NotBlank(message = "The field <assunto> is required")

  private String assunto ;
  @NotBlank(message = "The field <corpo> is required")

  private String corpo ;


  private String tipoNotificacao ;


  private String tipoNotificacaoDesc ;


  private String estado ;

}
