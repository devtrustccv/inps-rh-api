package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filtros da lista de Gestão de Falta. Todos opcionais.
 *
 * <p>O período pode ser dado por {@code mes}/{@code ano} ou por
 * {@code dataInicio}/{@code dataFim}; quando ambos vêm, as datas mandam.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaMovimentosResumidosQuery implements Query {

  private String pageSize;
  private String pageNumber;
  private String colaborador;
  private String funcionarioUuid;
  private Integer mes;
  private Integer ano;

  /** CONFORME | INJUSTIFICADA | JUSTIFICADA | PENDENTE. */
  private String estado;

  private Long ilha;
  private Long direcao;
  private Long seccao;

  /** SIPSGLOBAL.GLB_T_UPS.ID, via RH_T_PARAM_LOCAL_TRAB.UPS_ID. */
  private Long ups;

  /** {@code YYYY-MM-DD}. */
  private String dataInicio;

  /** {@code YYYY-MM-DD}. */
  private String dataFim;

}
