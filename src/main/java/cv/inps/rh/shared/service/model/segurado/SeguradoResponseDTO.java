package cv.inps.rh.shared.service.model.segurado;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record SeguradoResponseDTO(

    @JacksonXmlProperty(localName = "identificacao_segurado")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<SeguradoData> segurados

) {

  public record SeguradoData(

      @JacksonXmlProperty(localName = "nomepai")
      String nomePai,

      @JacksonXmlProperty(localName = "nomemae")
      String nomeMae,

      @JacksonXmlProperty(localName = "nr_identificacao")
      String numeroIdentificacao,

      @JacksonXmlProperty(localName = "dt_nascimento")
      String dataNascimento,

      @JacksonXmlProperty(localName = "numero_pessoa")
      String numeroPessoa,

      @JacksonXmlProperty(localName = "nome_pessoa")
      String nomePessoa

  ) {
  }
}
