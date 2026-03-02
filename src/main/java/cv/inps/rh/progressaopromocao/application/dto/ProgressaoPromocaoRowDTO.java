/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.progressaopromocao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProgressaoPromocaoRowDTO  {



  private String id ;


  private String progressaoPromocao ;


  private LocalDate dataReferente ;


  private String nomeColaborador ;


  private String carreira ;


  private String cargo ;


  private String escalaoDe ;


  private String escalaoPara ;


  private String observacao ;


  private Long avaliacaoMedia ;


  private String historico ;

}
