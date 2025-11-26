/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class NovoPagamentoRequestDTO extends NovoRemuneracaoRequestDTO {

  @NotBlank(message = "The field <entidade> is required")

  private String entidade ;
  @NotNull(message = "The field <nif> is required")

  private Integer nif ;
  @NotBlank(message = "The field <banco> is required")

  private String banco ;
  @NotBlank(message = "The field <nib> is required")

  private String nib ;

}
