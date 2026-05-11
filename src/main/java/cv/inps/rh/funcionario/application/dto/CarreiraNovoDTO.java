/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String processamentoSalarial ;

  @Valid
  private List<SubsidioReqDTO> subsidios = new ArrayList<>();

  @Valid
  private List<EncargosDescontosReqDTO> encargosDescontos = new ArrayList<>();

}
