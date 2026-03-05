/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AlertaDTO  {

  private Long id;
  private String estado;
  private String referencia;
  private String referenciaName;
  private String referenciaId;
  private String referenciaUuid;
  private String tipoAlerta;
  private String colaborador;
  private String seccao;
  private String descricao;
  private String dataRegisto;
  private Boolean flgNotificacao;

}
