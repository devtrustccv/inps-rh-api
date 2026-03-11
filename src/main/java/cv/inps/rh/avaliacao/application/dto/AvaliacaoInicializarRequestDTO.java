package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoInicializarRequestDTO {

  @NotEmpty
  private List<UUID> funIds;

  @NotNull
  private Integer ano;

  @NotBlank
  private String semestre;

  @NotNull
  private Long institId;

  private Long seccaoId;
  private Long cargoId;
  private Long carrPccsId;
}

