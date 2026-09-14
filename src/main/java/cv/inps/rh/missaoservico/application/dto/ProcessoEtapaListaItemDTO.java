/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/** Linha do ecrã "Lista Etapa Missão": um processo numa etapa. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoEtapaListaItemDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private String nacionalInternacional;   // "Nacional" | "Internacional"
  private String destino;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private UUID processoUuid;
  private String tipoProcesso;
  private String tipoProcessoDesc;
  private String etapa;
  private String etapaDesc;

}
