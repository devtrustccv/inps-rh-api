package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadSoatPdfQuery implements Query {

  @NotBlank(message = "The field <soatId> is required")
  private String soatId;

  @NotBlank(message = "The field <apoliceId> is required")
  private String apoliceId;
}
