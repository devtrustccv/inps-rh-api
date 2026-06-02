package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaDefinicaoObjectivosQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotBlank(message = "The field <semestre> is required")
  private String semestre;
  @NotBlank(message = "The field <estado> is required")
  private String estado;
  @NotNull(message = "The field <institId> is required")
  private Long institId;
  @NotNull(message = "The field <cargoId> is required")
  private Long cargoId;
  private Long carreiraId;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}
