package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DadosInstituicaoRequestDTO {

  @NotBlank(message = "The field <nome> is required")
  @Size(max = 200, message = "The field <nome> must have at most 200 characters")
  private String nome;

  @NotNull(message = "The field <nif> is required")
  private Long nif;

  @Size(max = 200, message = "The field <codCae> must have at most 200 characters")
  private String codCae;

  @Size(max = 300, message = "The field <atividadeEconomica> must have at most 300 characters")
  private String atividadeEconomica;

  @Size(max = 100, message = "The field <numCertidaoComercial> must have at most 100 characters")
  private String numCertidaoComercial;

  private LocalDate dataValidade;

  private Long telefone;

  private Long telemovel;

  @Size(max = 300, message = "The field <localidade> must have at most 300 characters")
  private String localidade;

  @Email(message = "The field <email> must be a valid email address")
  @Size(max = 200, message = "The field <email> must have at most 200 characters")
  private String email;

  @Size(max = 300, message = "The field <morada> must have at most 300 characters")
  private String morada;

  private Long concelhoId;
}
