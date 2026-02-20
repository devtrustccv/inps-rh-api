package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDossierColaboradorQuery implements Query {

  @NotNull(message = "The field <direccaoId> is required")
  private Long direccaoId;
  @NotNull(message = "The field <seccaoId> is required")
  private Long seccaoId;
  @NotNull(message = "The field <cargoId> is required")
  private Long cargoId;
  @NotBlank(message = "The field <idade> is required")
  private String idade;
  @NotBlank(message = "The field <genero> is required")
  private String genero;
  @NotBlank(message = "The field <faixaEtaria> is required")
  private String faixaEtaria;
  @NotNull(message = "The field <localTrabalhoId> is required")
  private Long localTrabalhoId;
  @NotNull(message = "The field <carreiraId> is required")
  private Long carreiraId;
  @NotNull(message = "The field <escalaoId> is required")
  private Long escalaoId;
  @NotNull(message = "The field <categoriaId> is required")
  private Long categoriaId;
  @NotBlank(message = "The field <grauEscolaridade> is required")
  private String grauEscolaridade;
  @NotBlank(message = "The field <mobilidade> is required")
  private String mobilidade;
  @NotNull(message = "The field <vinculoId> is required")
  private Long vinculoId;
  @NotNull(message = "The field <situacaoLaboralId> is required")
  private Long situacaoLaboralId;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}