package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaTecnicaLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

  @NotNull
  private Integer numeroOrdem;
}

