package cv.inps.rh.shared.service.model.nif;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntryDTO {

  @JsonProperty("nuNif")
  @JsonAlias("NU_NIF")
  private Long nuNif;

  @JsonProperty("nmContribuinte")
  @JsonAlias("NM_CONTRIBUINTE")
  private String nmContribuinte;

  @JsonProperty("dtNasc")
  @JsonAlias("DT_NASC")
  private String dtNasc;

  @JsonProperty("nmPai")
  @JsonAlias("NM_PAI")
  private String nmPai;

  @JsonProperty("nmMae")
  @JsonAlias("NM_MAE")
  private String nmMae;

  @JsonProperty("nuBi")
  @JsonAlias("NU_BI")
  private Long nuBi;

  @JsonProperty("nmPesquisa")
  @JsonAlias("NM_PESQUISA")
  private String nmPesquisa;

  @JsonProperty("nmPesquisaMae")
  @JsonAlias("NM_PESQUISA_MAE")
  private String nmPesquisaMae;

  @JsonProperty("nmPesquisaPai")
  @JsonAlias("NM_PESQUISA_PAI")
  private String nmPesquisaPai;
}
