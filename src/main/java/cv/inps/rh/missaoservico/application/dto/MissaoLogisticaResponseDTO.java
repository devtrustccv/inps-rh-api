/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoLogisticaResponseDTO  {

  private Long missaoId;
  private UUID missaoUuid;
  private String etapaAtual;              // campo etapa de RH_T_MISSAO_SERVICO — usado pelo frontend para activar o step correcto
  private List<BilhetePassagemResponseDTO> bilhetesPassagem;
  private List<SeguroViagemResponseDTO> segurosViagem;
  private List<AlojamentoResponseDTO> alojamentos;
  private List<AjudaCustoResponseDTO> ajudasCusto;

}
