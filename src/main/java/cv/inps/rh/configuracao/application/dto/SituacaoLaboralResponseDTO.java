/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class SituacaoLaboralResponseDTO extends SituacaoLaboralRequestDTO {

  private String id;
  private String estadoDescricao;
  private String remuneracaoDesc;
  private String carreiraDesc;
  private String tempoServicoDesc;
  private String suspendeProgressaoPromocaoDesc;
  private String abonoBeneficioDesc;
  private String ausenciaLocalTrabalhoDesc;
  private String cessaVinculoDesc;
  private String regressaCarreiraOrigemDesc;
  private String faltaDesc;

  @Valid
  private List<SituacaoLaboralMotivoRequestDTO> associacao = new ArrayList<>();


  private String areaClassificacaoDesc;

}
