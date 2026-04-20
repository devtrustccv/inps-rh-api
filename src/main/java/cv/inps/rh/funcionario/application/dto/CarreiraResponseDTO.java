/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class CarreiraResponseDTO  {

  
  
  private Long tipoContratoId ;
  
  
  private Long tipoVinculoLaboralId ;
  
  
  private String tipoVinculoLaboral ;
  
  
  private Long situacaoLaboralId ;
  
  
  private Long carreiraId ;
  
  
  private String tipoCarreira ;
  
  
  private Long cargoId ;
  
  
  private Long categoriaId ;
  
  
  private Long escalaoId ;
  
  
  private String salario ;
  
  
  private String moeda ;
  
  
  private String dataInicio ;
  
  
  private String dataFim ;
  
  
  private String processaSalarioNestaCarreira ;
  
  @Valid
  private List<EncargosDescontosReqDTO> encargosDescontos = new ArrayList<>();
  
  
  private String estado ;
  
  
  private String estadoDesc ;
  
  
  private String funcionarioId ;
  
  @Valid
  private List<SubsidioReqDTO> subsidios = new ArrayList<>();

}