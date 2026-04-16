/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MovRowDTO {


  private String movimentoId;


  private String nomeFicheiro;


  private String funcionarioId;


  private String nomeFuncionario;


  private String movimentoRetencao;


  private String movimentoRemuneracao;


  private BigDecimal percentagem;


  private BigDecimal valor;


  private LocalDate dataInicio;


  private LocalDate dataFim;


  private String situacao;

}
