/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoServicoResponseDTO  {

  private Long id;
  private UUID uuid;
  private Long nrMissao;
  private Long paisDestinoId;
  private String paisDestinoNome;
  private Integer flgDestino;
  private String descricaoDestino;
  private String ambitoMissao;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer nrDias;
  private String autorizadoPor;
  private LocalDate dataAutorizacao;
  private String etapa;
  private String estado;
  private String motivoCancelamento;
  // audit
  private LocalDate dataRegisto;
  private Long userRegistoId;
  private String userRegistoName;
  private Long userAlteracaoId;
  private String userAlteracaoName;
  private LocalDate dataAlteracao;

}
