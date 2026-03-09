package cv.inps.rh.shared.service.model.bi;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BiEntryDTO {

    @JsonProperty("nome")
    @JsonAlias("NOME")
    private String nome;

    @JsonProperty("bi")
    @JsonAlias("BI")
    private String bi;

    @JsonProperty("sexo")
    @JsonAlias("SEXO")
    private String sexo;

    @JsonProperty("dtNasc")
    @JsonAlias("DT_NASC")
    private String dtNasc;

    @JsonProperty("nomeMae")
    @JsonAlias("NOME_MAE")
    private String nomeMae;

    @JsonProperty("nomePai")
    @JsonAlias("NOME_PAI")
    private String nomePai;

    @JsonProperty("dtEmissao")
    @JsonAlias("DT_EMISSAO")
    private String dtEmissao;

    @JsonProperty("emissor")
    @JsonAlias("EMISSOR")
    private String emissor;

    @JsonProperty("estadoCivil")
    @JsonAlias("ESTADO_CIVIL")
    private String estadoCivil;

    @JsonProperty("natConcelho")
    @JsonAlias("NAT_CONCELHO")
    private String natConcelho;

    @JsonProperty("residencia")
    @JsonAlias("RESIDENCIA")
    private String residencia;

    @JsonProperty("dtValidade")
    @JsonAlias("DT_VALIDADE")
    private String dtValidade;

    @JsonProperty("natCodRni")
    @JsonAlias("NAT_COD_RNI")
    private String natCodRni;
}
