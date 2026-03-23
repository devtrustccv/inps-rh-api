/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AlojamentoResponseDTO  {

  private Long id;
  private UUID uuid;
  private String flgAlimentacao;
  private String lugarHospedagem;
  private BigDecimal valorDiario;
  private BigDecimal valorTotal;
  private String moeda;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer nrDias;                 // calculado
  private MissaoLogisticaDetResponseDTO colaborador;
  private AnexoRespDTO documento;
  private String estado;
  // referencia = 'ALOJAMENTO' (interno)

}
