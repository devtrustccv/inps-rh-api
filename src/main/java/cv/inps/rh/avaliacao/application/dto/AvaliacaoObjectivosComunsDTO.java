package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Corpo da avaliação dos objectivos comuns de um ano num período. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoObjectivosComunsDTO {

  /** Linhas do bloco "Objectivos Instituição" (abrangência INPS). */
  @Valid
  private List<LinhaAvaliacaoComumDTO> instituicao = new ArrayList<>();

  /** Linhas do bloco "Objectivos Direção" (abrangência DIRECAO), de todas as direções. */
  @Valid
  private List<LinhaAvaliacaoComumDTO> direcoes = new ArrayList<>();

}
