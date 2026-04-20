package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@IgrpDTO
public class AtitudePessoalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {

  private String abrangencia;
  private String descricao;
}

