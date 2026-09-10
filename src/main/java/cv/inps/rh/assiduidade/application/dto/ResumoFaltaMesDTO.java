package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Resumo de faltas de um mês — o painel esquerdo do ecrã "Justificar Falta".
 *
 * <p>Não é um pedido, é a leitura de um período, e por isso não tem cabeçalho de formulário:
 * os dias já justificados vêm dentro do {@link #pedidos pedido} a que pertencem, cada um com
 * o seu cabeçalho completo (o Editar abre daí, sem segunda chamada); em {@link #itensFalta}
 * ficam só os dias que ainda não têm justificação, prontos a seleccionar.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ResumoFaltaMesDTO {

  private UUID colaboradorId;

  private String nomeColaborador;

  private Integer ano;

  private Integer mes;

  /** Dias com ausência ainda sem pedido de justificação — estado "Por justificar". */
  @Valid
  private List<FaltaItemDTO> itensFalta = new ArrayList<>();

  /**
   * Um por grupo de faltas do mês (RH_T_FALTA.PEDIDO_ID), com a mesma forma que o
   * GET .../falta/justificar/pedido/{pedidoUuid} devolve.
   */
  @Valid
  private List<JustificarFaltaDTO> pedidos = new ArrayList<>();
}
