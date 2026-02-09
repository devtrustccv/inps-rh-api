/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FaltaReqDTO  {



  private UUID colaboradorId ;


  private String colaboradorNome ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer totalDias ;


  private String totalDeHorasAusentes ;


  private String justificar ;


  private String motivoAusencia ;


  private String parecer ;


  private UUID responsavel ;


  private String observacao ;


  private EstadoValidacao validar ;


  private String despachoRh ;


  private Long tipoJustificacao ;

  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

}
