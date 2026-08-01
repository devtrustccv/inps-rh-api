package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filtros da lista de hora extra. Todos opcionais — sem filtros, devolve tudo
 * ordenado por data de pedido descendente.
 *
 * <p>Os filtros de pessoa/estrutura ({@code funcionarioUuid}, {@code colaborador},
 * {@code direcao}, {@code seccao}, {@code ilha}) seleccionam o pedido se <em>algum</em>
 * dos seus itens corresponder, e restringem também os itens devolvidos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaHoraExtraQuery implements Query {

  private String pageNumber;
  private String pageSize;
  private Long ilha;
  private Long direcao;
  private Long seccao;

  /** Data inicial do intervalo, {@code YYYY-MM-DD}. Testa sobreposição, não contenção. */
  private String dataInicio;

  /** Data final do intervalo, {@code YYYY-MM-DD}. */
  private String dataFim;

  private String funcionarioUuid;

  /** Pesquisa parcial pelo nome do colaborador. */
  private String colaborador;

  /** Estado do pedido: {@code P} | {@code A} | {@code I}. */
  private String estado;

  /** Mês de referência, {@code YYYYMM}. */
  private String mes;

}
