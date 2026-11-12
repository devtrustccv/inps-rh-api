package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ManualFuncaoRequestDTO {

  @NotNull
  private Long institId;

  private Long seccaoId;

  // Opcional: o manual deixou de ter Cargo — "Carreira / Cargo" é o carrPccsId.
  private Long cargoId;

  private Long carrPccsId;

  @NotBlank
  private String descricao;
}

