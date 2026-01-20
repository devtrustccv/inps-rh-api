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
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class CarreiraNovoDTO  {

  
  
  private EstadoValidacao validar ;
  
  
  private Long tipoVinculoLaboralId ;
  
  
  private String tipoCarreira ;
  
  
  private Long carreiraId ;
  
  
  private Long categoriaId ;
  
  
  private Long escalaoReferenciaId ;
  
  
  private BigDecimal salario ;
  
  
  private String moeda ;
  
  
  private String processamentoSalarial ;
  
  @Valid
  private List<SubsidioReqDTO> subsidios = new ArrayList<>();
  
  @Valid
  private List<EncargosDescontosReqDTO> encargosDescontos = new ArrayList<>();

}