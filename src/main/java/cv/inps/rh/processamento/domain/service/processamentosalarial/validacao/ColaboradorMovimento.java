package cv.inps.rh.processamento.domain.service.processamentosalarial.validacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ColaboradorMovimento {

  @JsonProperty("nome_colaborador")
  private String nomeColaborador;

  @JsonProperty("nib")
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

  @JsonProperty("numero")
  private Integer numero;

  @JsonProperty("situacao_laboral")
  private String situacaoLaboral;

  @JsonProperty("tipo_filtro")
  private String tipoFiltro;

  @JsonProperty("procsal_id")
  private Integer procsalId;

  @JsonProperty("fun_id")
  private Integer funId;
}
