/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DadosValidacaoDTO {


  private String nomeColaborador;


  private String nib;


  private BigDecimal valorAnterior;


  private BigDecimal valorAtual;


  private String tipoMovimento;


  private String mesAnterior;


  private String mesAtual;


  private BigDecimal valorEscalao;


  private Integer numero;


  private String situacaoLaboral;


  private String tipoFiltro;


  private Integer processamentoId;


  private Integer funId;

}
