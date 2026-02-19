/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoFeriaReqDTO  {



  private String validar ;


  private String obsValidacao ;


  private UUID colaborador ;

  private String colaboradorNome ;

  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer numDias ;

  private Integer numDiasPorGozar;


  private UUID substituidoPor ;


  private String obsConvinienciaServico ;


  private UUID responsavel ;

  private String responsavelNome ;

  private String obsParecer ;

  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

}
