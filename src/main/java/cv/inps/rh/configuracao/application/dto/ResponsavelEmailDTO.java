package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entrada do multiselect "Email do Responsável". Deliberadamente magra — o ecrã só precisa do
 * endereço para enviar e de um nome para mostrar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ResponsavelEmailDTO {

  private Long idResponsavel;
  private String email;
  private String nome;
  private String direcao;
  private String seccao;
}
