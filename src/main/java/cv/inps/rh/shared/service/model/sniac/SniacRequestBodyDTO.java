package cv.inps.rh.shared.service.model.sniac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SniacRequestBodyDTO {

    @JsonProperty("PesquisaDocumentoSNIAC")
    private PesquisaDocumentoSNIAC pesquisaDocumentoSNIAC;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PesquisaDocumentoSNIAC {
        @JsonProperty("P_NOME_COMPLETO")
        private String nomeCompleto;

        @JsonProperty("P_DATA_NASC")
        private String dataNasc;

        @JsonProperty("P_NIC")
        private String nic;
    }
}
