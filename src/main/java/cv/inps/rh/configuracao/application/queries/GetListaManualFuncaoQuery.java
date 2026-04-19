package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaManualFuncaoQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotNull(message = "The field <cargoId> is required")
  private Long cargoId;
  @NotNull(message = "The field <carrPccsId> is required")
  private Long carrPccsId;
  @NotNull(message = "The field <institId> is required")
  private Long institId;
  @NotNull(message = "The field <seccaoId> is required")
  private Long seccaoId;
  @NotBlank(message = "The field <conteudo> is required")
  private String conteudo;

}