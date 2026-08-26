package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DadosApoliceResponseDTO {

  private String uuid;
  private String numApolice;
  private Long ilhaId;
  private LocalDate dataApolice;
  private String estado;
}
