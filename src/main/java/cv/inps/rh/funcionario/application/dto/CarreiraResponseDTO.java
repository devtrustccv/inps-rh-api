/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class CarreiraResponseDTO  {

  
  
  private String tipoVinculoLaboral ;
  
  
  private String tipoCarreira ;
  
  
  private Long cargoId ;
  
  
  private Long carreiraId ;
  
  
  private Long categoriaId ;
  
  
  private Long escalaoId ;
  
  
  private String salario ;
  
  
  private String moeda ;
  
  
  private String dataInicio ;
  
  
  private String dataFim ;
  
  
  private String processaSalarioNestaCarreira ;
  
  @Valid
  private List<EncargosDescontosReqDTO> encargos = new ArrayList<>();
  
  
  private String estado ;
  
  
  private String estadoDesc ;
  
  
  private String funcionarioId ;

}