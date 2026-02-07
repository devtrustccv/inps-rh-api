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
public class DispensaReqDTO  {

  
  
  private UUID colaborador ;
  
  
  private String colaboradorNome ;
  
  
  private String horasUsadasMes ;
  
  
  private String horasDisponiveis ;
  
  
  private LocalDate dataDispensa ;
  
  
  private String horaSaida ;
  
  
  private String horaEntrada ;
  
  
  private String totalHoras ;
  
  
  private String tipoMotivo ;
  
  
  private String motivo ;
  
  
  private String parecerResponsavel ;
  
  
  private String observacaoResponsavel ;
  
  
  private String validar ;
  
  
  private String ObservacaoRh ;
  
  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

}