/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BaixaMedicaCalculoDTO  {


  private String descSobre ;


  private String diasDireito ;


  private String diasDescRh ;


  private String diasNdescRh ;


  private String msgError ;


  private List<BaixaMedicaFaltaMensalDTO> faltasMensais = new ArrayList<>();

}
