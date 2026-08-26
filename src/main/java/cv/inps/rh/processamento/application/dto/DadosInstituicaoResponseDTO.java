package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DadosInstituicaoResponseDTO {

  private String uuid;
  private String nome;
  private Long nif;
  private String codCae;
  private String atividadeEconomica;
  private String numCertidaoComercial;
  private LocalDate dataValidade;
  private Long telefone;
  private Long telemovel;
  private String localidade;
  private String email;
  private String morada;
  private Long concelhoId;
  private String estado;
}
