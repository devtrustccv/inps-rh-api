/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoDeclaracaoRowDTO  {
  private Long id;
  private UUID uuid;
  private String tipoDeclaracao;
  private String efeito;
  private LocalDate dataPedido;
  private String nomeColaborador;
  private UUID uuidColaborador;
  private String direcao;
  private String seccao;
  private String vinculo;
  private String estadoPedido;
  private String etapa;

}
