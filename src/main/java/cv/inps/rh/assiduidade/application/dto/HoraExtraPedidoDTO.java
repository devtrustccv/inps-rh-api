package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Nível 1 da lista de hora extra: uma linha por <strong>pedido</strong>.
 *
 * <p>O pedido é a unidade de validação ({@code POST hora-extra/{pedidoId}} valida todos
 * os registos de uma vez), por isso é também o grão da lista — antes havia uma linha por
 * registo, o que punha a lista num grão diferente da acção.
 *
 * <p>Quando há filtros de colaborador/direcção/secção, os {@code itens} trazem apenas as
 * linhas que correspondem, e {@code totalColaboradores}/{@code valorTotal} acompanham o
 * filtro. Os valores íntegros do pedido ficam em {@code totalColaboradoresPedido} e
 * {@code valorTotalPedido}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class HoraExtraPedidoDTO {

  private Long pedidoId;

  private String pedidoUuid;

  private String estado;

  private String estadoDesc;

  private String etapa;

  /** Servido a partir de {@code RH_T_PEDIDO.DATA_REGISTO} — não existe coluna DATA_PEDIDO. */
  private String dataPedido;

  private String periodoInicio;

  private String periodoFim;

  /** Meses abrangidos, formato {@code YYYYMM}. União dos meses dos itens. */
  private List<String> mesesReferencia = new ArrayList<>();

  private Long direcaoId;

  /** Nome da direcção, ou "Várias" quando o pedido abrange mais do que uma. */
  private String direcao;

  private Long seccaoId;

  private String seccao;

  /** Colaboradores que passaram o filtro. */
  private Integer totalColaboradores;

  /** Colaboradores reais do pedido, independentemente do filtro. */
  private Integer totalColaboradoresPedido;

  private Integer totalRegistos;

  /** Soma dos itens devolvidos (afectada pelo filtro). */
  private BigDecimal valorTotal;

  /** Soma íntegra do pedido. */
  private BigDecimal valorTotalPedido;

  @Valid
  private List<HoraExtraLinhaDTO> itens = new ArrayList<>();
}
