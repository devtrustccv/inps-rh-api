package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Realizado e avaliação de uma linha de objectivo comum num período. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class LinhaAvaliacaoComumDTO {

  /** uuid de RH_T_AVD_OBJECTIVO, tal como vem em {@code LinhaObjectivoComumDTO.id}. */
  @NotNull(message = "The field <id> is required")
  private UUID id;

  private String realizado;
  private BigDecimal avaliacao;

}
