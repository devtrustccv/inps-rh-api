/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class HoraExtraDTO  {


  private Long id ;


  private UUID colaborador ;


  private String colaboradorNome ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Long horasDiaria ;


  private String percentagemReferente ;


  /** Total do período, tal como CALCULO_HORA_EXTRA o devolve. */
  private BigDecimal valorDiario ;

  // --- Repartição mensal (só de resposta) -------------------------------
  // Ao ler, uma hora extra que atravessa N meses devolve N entradas, com as
  // datas recortadas a cada mês. Ao escrever, envia-se uma só entrada com o
  // período integral.

  /** Mês de referência, {@code YYYYMM}. */
  private String mes ;

  private Integer diasUteis ;

  private Integer diasNaoUteis ;

  private BigDecimal valorDiarioUtil ;

  private BigDecimal valorDiarioNaoUtil ;

  private BigDecimal valorAcumuladoMes ;

  @Valid
  private AnexoReqDTO documento ;

}
