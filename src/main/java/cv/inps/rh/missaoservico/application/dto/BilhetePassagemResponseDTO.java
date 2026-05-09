/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BilhetePassagemResponseDTO  {

  private Long id;
  private UUID uuid;
  private List<MissaoLogisticaDetResponseDTO> colaboradores;
  private BigDecimal valor;               // MissaoLogisticaEntity.valorTotal
  private AnexoRespDTO documento;
  private String estado;
  // referencia = 'BILHETE_PASSAGEM' (interno — não exposto)
  // moeda = 'CVE' (interno — default)

}
