package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Uma linha de objectivo comum no formulário de avaliação.
 *
 * <p>{@code id} é o uuid de RH_T_AVD_OBJECTIVO e é o que identifica a linha ao gravar a
 * avaliação — o número de ordem repete-se entre direções. {@code realizado},
 * {@code avaliacao} e {@code resultado} só vêm preenchidos quando se pede um período.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class LinhaObjectivoComumDTO {

  private String id;
  private Long paramId;
  private Integer numero;
  private String abrangencia;
  private String objectivo;
  private String kpi;
  private String meta;
  private BigDecimal ponderacao;
  private String realizado;
  private BigDecimal avaliacao;

  /** Só de leitura: RH_T_AVD_OBJECTIVO.PONDERACAO × RH_T_AVD_PERIODICIDADE.AVALIACAO. */
  private BigDecimal resultado;

}
