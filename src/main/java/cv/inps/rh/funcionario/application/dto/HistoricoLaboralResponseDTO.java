/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class HistoricoLaboralResponseDTO {

  private Long id;

  private String uuid;

  private boolean situacaoAtual;

  private String ultimoMovimento;


  private String tipoSituacao;


  private String tipoContrato;


  private String vinculo;


  private String direcao;


  private String seccao;


  private String carreira;


  private String referenciaEscalao;


  private String cargo;


  private String situacaoLaboral;


  private String dataInicioFimCarreira;

  private String dataInicioFimContrato;

}
