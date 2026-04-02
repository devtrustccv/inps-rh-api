package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@IgrpDTO
public class AtitudePessoalLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

  @NotBlank
  private String descricao;
}

