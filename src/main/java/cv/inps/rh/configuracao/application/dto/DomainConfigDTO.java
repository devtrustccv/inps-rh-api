package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.Estado;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@IgrpDTO
public class DomainConfigDTO {

  private Long id;
  @NotBlank(message = "The field <dominio> is required")

  private String dominio;
  @NotBlank(message = "The field <valor> is required")

  private String valor;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao;

  private String referencia;

  private Estado estado;

}

