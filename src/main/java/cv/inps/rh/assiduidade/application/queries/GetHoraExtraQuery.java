package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetHoraExtraQuery implements Query {

  @NotBlank(message = "The field <horaExtraId> is required")
  private String horaExtraId;

}