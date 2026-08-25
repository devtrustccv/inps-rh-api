/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@IgrpDTO
public class SoapRowResponseDTO {

  private String uuid;

  private String mesReferente;

  private LocalDateTime dataCriacao;

  private BigDecimal totalRemuneracao;

  private Long totalColaboradores;

  private String estado;

}
