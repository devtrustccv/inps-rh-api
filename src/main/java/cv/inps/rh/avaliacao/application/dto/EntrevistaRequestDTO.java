package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EntrevistaRequestDTO {

  @NotNull
  private LocalDate dataInicioEntrevista;

  @NotBlank
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
  private String horaInicioEntrevista;

  @NotBlank
  @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
  private String horaFimEntrevista;

  private String observacaoGeral;
  private String descricaoPlano;
}

