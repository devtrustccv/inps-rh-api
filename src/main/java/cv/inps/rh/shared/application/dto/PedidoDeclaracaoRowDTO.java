/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoDeclaracaoRowDTO  {
  private Long id;
  private String tipoDeclaracao;
  private String efeito;
  private LocalDate dataPedido;
  private String nomeColaborador;
  private String direcao;
  private String seccao;
  private String vinculo;
  private String estadoPedido;
  private String etapa;

}
