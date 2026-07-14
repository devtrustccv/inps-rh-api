/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


  private String carreiraDesc ;


  private String tipoCarreira ;


  private Long cargoId ;


  private String cargoDesc ;


  private Long categoriaId ;


  private String categoriaDesc ;


  private Long escalaoId ;


  private String escalaoDesc ;


  private String salario ;


  private String moeda ;


  private String dataInicio ;


  private String dataFim ;


  private String processaSalarioNestaCarreira ;


  private Boolean processamento ;

  @Valid
  private List<EncargosDescontosRespDTO> encargosDescontos = new ArrayList<>();


  private String estado ;


  private String estadoDesc ;


  private String funcionarioId ;

  @Valid
  private List<SubsidioRespDTO> subsidios = new ArrayList<>();

}
