package cv.inps.rh.processamento.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DadosValidacaoDTO {

  @JsonProperty("nome_colaborador")
  private String nomeColaborador;

  private String nib;

  @JsonProperty("valor_anterior")
  private BigDecimal valorAnterior;

  @JsonProperty("valor_atual")
  private BigDecimal valorAtual;

  @JsonProperty("tipo_movimento")
  private String tipoMovimento;

  @JsonProperty("mes_anterior")
  private String mesAnterior;

  @JsonProperty("mes_atual")
  private String mesAtual;

  @JsonProperty("valor_escalao")
  private BigDecimal valorEscalao;

  private Integer numero;

  @JsonProperty("situacao")
  private String situacaoLaboral;

  @JsonProperty("tipo_filtro")
  private String tipoFiltro;

  @JsonProperty("procsal_id")
  private Integer processamentoId;

  @JsonProperty("fun_id")
  private Integer funId;
}
