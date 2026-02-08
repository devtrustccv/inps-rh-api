package cv.inps.rh.shared.service.model.nif;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntryDTO {

  @JsonProperty("NU_NIF")
  private Long nuNif;

  @JsonProperty("NM_CONTRIBUINTE")
  private String nome;

  @JsonProperty("DT_NASC")
  private String dataNascimento;

  @JsonProperty("NM_PAI")
  private Object nomePai;

  @JsonProperty("NM_MAE")
  private String nomeMae;

  @JsonProperty("NU_BI")
  private Object nuBi;

  @JsonProperty("NM_PESQUISA")
  private String pesquisa;

  @JsonProperty("NM_PESQUISA_MAE")
  private String pesquisaMae;

  @JsonProperty("NM_PESQUISA_PAI")
  private Object pesquisaPai;
}
