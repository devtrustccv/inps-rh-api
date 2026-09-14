/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Linha da sub-lista de processos na Lista Geral de missões. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoProcessoResumoDTO {

  private UUID uuid;
  private String tipoProcesso;
  private String tipoProcessoDesc;
  private String etapa;                   // link "Executar" abre o ecrã desta etapa
  private String etapaDesc;
  private String estado;                  // A | I
  private BigDecimal valorTotal;          // soma das linhas de logística activas do processo

}
