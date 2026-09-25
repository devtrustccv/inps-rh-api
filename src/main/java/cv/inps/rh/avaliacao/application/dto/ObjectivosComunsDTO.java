package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Os objectivos comuns de um ano, para as ações "Avaliação" e "Ver Objectivo / Avaliação".
 * Com período, as linhas trazem as medições desse período.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjectivosComunsDTO {

  private Integer ano;
  private String periodicidade;
  private String periodicidadeDescricao;

  /** O bloco "Objectivos Instituição"; {@code null} se o ano só tiver objectivos de direção. */
  private BlocoObjectivosComunsDTO instituicao;

  /** O bloco "Objectivos Direção": um por direção. */
  private List<BlocoObjectivosComunsDTO> direcoes = new ArrayList<>();

}
