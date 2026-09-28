package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Os objectivos de uma RH_T_AVD comum: a do INPS ou a de uma direção. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class BlocoObjectivosComunsDTO {

  private String uuid;
  private String abrangencia;
  private Long institId;
  private String institNome;
  private List<LinhaObjectivoComumDTO> objectivos = new ArrayList<>();

}
