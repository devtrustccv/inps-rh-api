/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DetalheXmlRequestDTO {

  @NotNull(message = "The field <fosId> is required")

  private Long fosId;


  private Long detaildId;


  private String mesReferencia;


  private String tipoRemuneracao;


  private BigDecimal remuneracao;


  private Integer diasTrabalho;


  private Long funcionarioId;


  private String numeroSegurado;


  private Integer direcaoServicoId;

}
