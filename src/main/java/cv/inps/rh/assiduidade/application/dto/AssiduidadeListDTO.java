/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AssiduidadeListDTO  {


  private Long id ;


  private UUID uuid ;


  private UUID uuidFuncionairio ;


  private String nomeColaborador ;


  private String direcao ;


  private Integer totalFalta ;


  private Integer totalDias ;


  private String totalHorasTrabalhadas ;


  private String totalHorasAusentes ;


  private String totalHoraExtra ;


  private String totalHoraAlmoco ;


  private String estado ;


  private String estadoDesc ;


  private String mesReferencia ;


  private Integer ano ;


  private Integer mes ;

}
