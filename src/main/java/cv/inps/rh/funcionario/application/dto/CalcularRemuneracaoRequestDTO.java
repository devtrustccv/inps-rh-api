/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CalcularRemuneracaoRequestDTO {

  @NotNull(message = "The field <salario> is required")
  private BigDecimal salario;

  @NotBlank(message = "The field <moeda> is required")
  private String moeda;

  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;

  @Valid
  @NotNull(message = "The field <subsidios> is required")
  @NotEmpty(message = "The field <subsidios> must not be empty")
  private List<SubsidioItemDTO> subsidios = new ArrayList<>();

  @Valid
  @NotNull(message = "The field <descontos> is required")
  @NotEmpty(message = "The field <descontos> must not be empty")
  private List<DescontoItemDTO> descontos = new ArrayList<>();

}
