package cv.inps.rh.shared.service.model.segurado;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;

public record SeguradoRequestDTO(

    @JsonProperty("_postget_segurado_api")
    SeguradoData segurado

) {

  public record SeguradoData(

      @JsonProperty("p_nr_segurado")
      String numeroSegurado,

      @JsonProperty("p_dt_nascimento")
      @JsonSerialize(using = SeguradoLocalDateSerializer.class)
      LocalDate dataNascimento,

      @JsonProperty("p_nome_pessoa")
      String nomePessoa,

      @JsonProperty("p_tp_documento")
      String tipoDocumento,

      @JsonProperty("p_nr_identificacao")
      String numeroIdentificacao

  ) {

    public SeguradoData {
      numeroSegurado = emptyIfNull(numeroSegurado);
      nomePessoa = emptyIfNull(nomePessoa);
      tipoDocumento = emptyIfNull(tipoDocumento);
      numeroIdentificacao = emptyIfNull(numeroIdentificacao);
    }

    private static String emptyIfNull(String value) {
      return value == null ? "" : value.trim();
    }
  }
}
