package cv.inps.rh.processamento.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class SubsidioFeriasDetalheDTO {

  private String nome;

  private Long funId;

  private LocalDate dataInicio;

  private String dataFim;

  private String escalaoDesc;

  @JsonIgnore
  private BigDecimal valorEscalaoBD;

  private Long mesesTrabalhados;

  @JsonIgnore
  private Long valorMesBD;

  private Long diasTrabalhados;

  private Long valorDias;

  @JsonIgnore
  private Long valorEscalaotempoBD;

  private String situacao;

  private String valorEscalao;

  private String valorMes;

  private String valorEscalaotempo;
}
