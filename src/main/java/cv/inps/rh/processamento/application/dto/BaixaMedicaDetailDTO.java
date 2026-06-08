/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.Estado;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class BaixaMedicaDetailDTO {

  private String estadodesc;

  private Estado estado;

  private String direcao;

  private String seccao;

  private String nome;

  private String vinculo;

  private String categoria;

  private Long tipoLicensaId;

  private String tipoLicensaNome;

  private Long motivoId;

  private String motivoNome;

  private LocalDate dataInicio;

  private LocalDate dataFim;

  private UUID id;

  @Valid
  private BaixaMedicaCalculoDTO calculo;

  private Long relacionamentoId;

  private Long paramSitId;
}
