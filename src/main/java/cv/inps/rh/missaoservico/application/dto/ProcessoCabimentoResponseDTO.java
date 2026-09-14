/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Ecrãs Cabimentação e Autorização do processo. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoCabimentoResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private String estadoMissao;            // A | I | FINALIZADO
  private MissaoProcessoResponseDTO processo;
  private List<ProcessoCabimentoItemResponseDTO> itens;
  private BigDecimal valorTotal;          // soma das linhas
  private String executadoPor;
  private LocalDate dataExecucao;

}
