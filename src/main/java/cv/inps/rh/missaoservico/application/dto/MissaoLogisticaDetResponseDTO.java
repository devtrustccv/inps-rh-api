/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoLogisticaDetResponseDTO  {

  private Long id;                    // id de RH_T_MISSAO_LOGISTICA_DET — usado internamente
  private UUID missaoColabUuid;       // uuid de RH_T_MISSAO_COLABORADOR — usado na sincronização dos items
  private UUID funcionarioUuid;       // uuid de RH_T_FUNCIONARIOS — identificador do colaborador na API
  private String nomeColaborador;     // FuncionarioEntity.nome
  private String estado;

}
