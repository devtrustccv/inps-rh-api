/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoProcessoResponseDTO {

  private Long id;
  private UUID uuid;
  private String tipoProcesso;            // BILHETE_PASSAGEM | SEGURO_VIAGEM | AJUDA_CUSTO | ALOJAMENTO
  private String tipoProcessoDesc;
  private String etapa;                   // domínio TIPO_PROCESSO_ETAPA
  private String etapaDesc;
  private String estado;                  // A | I

}
