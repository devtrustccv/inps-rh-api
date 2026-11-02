package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@IgrpDTO
public class AtitudePessoalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {

  private String abrangencia;
  private String descricao;
}

