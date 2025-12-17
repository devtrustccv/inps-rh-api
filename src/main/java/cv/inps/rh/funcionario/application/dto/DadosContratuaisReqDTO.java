/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
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
public class DadosContratuaisReqDTO  {



  private Long tipoContratoId ;


  private Long cargoPosicaoId ;


  private Long direcaoId ;


  private Long seccaoId ;


  private String centroCusto ;


  private Long carreiraId ;


  private Long categoriaId ;


  private Long escalaoReferenciaId ;


  private Long tipoVinculoLaboralId ;


  private String RegimeTrabalho ;


  private BigDecimal salario ;


  private String moeda ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer duracaoMeses ;


  private Long localTrabalhoId ;


  private Long paisId ;


  private Long ilhaId ;

  @Valid
  private List<SubsidioReqDTO> subsidios = new ArrayList<>();

  @Valid
  private List<EncargosDescontosReqDTO> encargosDescontos = new ArrayList<>();

}
