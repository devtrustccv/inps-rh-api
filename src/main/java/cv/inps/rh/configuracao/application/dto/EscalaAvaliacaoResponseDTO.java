package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@IgrpDTO
public class EscalaAvaliacaoResponseDTO extends EscalaAvaliacaoRowDTO {

  private UUID uuid;
  private String estado;
}

