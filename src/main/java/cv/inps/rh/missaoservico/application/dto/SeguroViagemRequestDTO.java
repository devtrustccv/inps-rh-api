/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
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
public class SeguroViagemRequestDTO  {

  private Long entId;                     // lookup INPSSIGOF.ENTIDADES — sem FK
  private String nomeSeguradora;          // preenchido automaticamente via entId
  private List<UUID> colaboradorIds;      // multiselect — obrigatório
  private BigDecimal valor;               // obrigatório
  private AnexoReqDTO anexo;  // upload PDF — opcional
}
