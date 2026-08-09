/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EstabelecimentoGroupedDTO {

  @NotNull
  private Long paisId;

  private String pais;

  private List<EstabelecimentoData> estabelecimentos;

  public record EstabelecimentoData(
      String id,
      @NotBlank
      String nome
  ) {

  }
}
