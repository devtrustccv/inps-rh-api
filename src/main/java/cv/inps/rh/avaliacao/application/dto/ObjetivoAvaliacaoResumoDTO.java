package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjetivoAvaliacaoResumoDTO {

  private Long id;
  private String uuid;
  private Integer ano;
  /** Períodos já definidos, pela ordem do ciclo. A grelha mostra-os na coluna própria. */
  private java.util.List<String> periodicidades;

  private String abrangencia;

  private Long funId;
  private java.util.UUID funUuid;
  /** Nome do colaborador. Vazio nas abrangências comuns (INPS / DIREÇÃO). */
  private String nomeColaborador;
  private Long institId;
  private String institNome;
  private Long seccaoId;
  private String seccaoNome;
  private Long cargoId;
  private String cargoNome;
  private Long carrPccsId;
  private String carrPccsNome;
  private String estado;
  /** Rótulo do estado, como a grelha o mostra. */
  private String estadoDescricao;
}

