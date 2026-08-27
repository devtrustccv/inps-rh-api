package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DadosApoliceRequestDTO {

  @NotBlank(message = "The field <numApolice> is required")
  @Size(max = 100, message = "The field <numApolice> must have at most 100 characters")
  private String numApolice;

  @NotNull(message = "The field <ilhaId> is required")
  private Long ilhaId;

  @NotNull(message = "The field <dataApolice> is required")
  private LocalDate dataApolice;
}
