/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DispensaListDTO  {



  private Long id ;


  private String uuid ;


  private String direcao ;


  private String vinculo ;


  private String dataPedido ;


  private String dataDispensa ;


  private String intervaloHoras ;


  private Integer totalHorasDireito ;


  private Integer totalHorasSolicitadas ;


  private String motivoDispensa ;


  private String estado ;


  private String estadoDesc ;

}
