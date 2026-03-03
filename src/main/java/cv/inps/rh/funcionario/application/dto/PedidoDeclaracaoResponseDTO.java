/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

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
public class PedidoDeclaracaoResponseDTO{

  private Long id;
  private String tipoDeclaracao;
  private String efeito;
  private LocalDate dataPedido;
  private String etapa;
  private String estadoPedido;

  // Colaborador
  private Long funId;
  private String nomeColaborador;
  private String direcao;
  private String seccao;
  private String vinculo;

  // Declaração
  private String finalidade;
  private String entidadeDestinado;
  private String obs;

  // Análise
  private String decisaoAnalise;
  private String obsAnalise;

  // Validação
  private String validar ;
  private String entregaPorEmail ;

}
