/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AlojamentoRequestDTO  {

  private String flgAlimentacao;          // "SIM" | "NAO" — radio button — obrigatório
  private String lugarHospedagem;         // obrigatório
  private BigDecimal valorDiario;         // obrigatório
  private BigDecimal valorTotal;          // obrigatório
  private String moeda;                   // dropdown — default: CVE
  private LocalDate dataInicio;           // default: dataInicio da missão — editável
  private LocalDate dataFim;              // default: dataFim da missão — editável
  // nrDias calculado no service: ChronoUnit.DAYS.between(dataInicio, dataFim)
  private UUID colaboradorId;             // MissaoColaboradorEntity.id — um colaborador por registo
  private AnexoReqDTO anexo;  // upload PDF — opcional

}
