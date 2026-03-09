package cv.inps.rh.shared.service.model.sniac;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SniacEntryDTO {

  @JsonProperty("idCivil")
  @JsonAlias("ID_CIVIL")
  private String idCivil;

  @JsonProperty("idNascimento")
  @JsonAlias("ID_NASCIMENTO")
  private Long idNascimento;

  @JsonProperty("nomeProprio")
  @JsonAlias("NOME_PROPRIO")
  private String nomeProprio;

  @JsonProperty("nomeApelido")
  @JsonAlias("NOME_APELIDO")
  private String nomeApelido;

  @JsonProperty("nomeCompleto")
  @JsonAlias("NOME_COMPLETO")
  private String nomeCompleto;

  @JsonProperty("nomePaiProprio")
  @JsonAlias("NOME_PAI_PROPRIO")
  private String nomePaiProprio;

  @JsonProperty("nomePaiApelido")
  @JsonAlias("NOME_PAI_APELIDO")
  private String nomePaiApelido;

  @JsonProperty("nomeMaeProprio")
  @JsonAlias("NOME_MAE_PROPRIO")
  private String nomeMaeProprio;

  @JsonProperty("nomeMaeApelido")
  @JsonAlias("NOME_MAE_APELIDO")
  private String nomeMaeApelido;

  @JsonProperty("dataNasc")
  @JsonAlias("DATA_NASC")
  private String dataNasc;

  @JsonProperty("nacionalidadeId")
  @JsonAlias("NACIONALIDADE_ID")
  private Long nacionalidadeId;

  @JsonProperty("nacionalidade")
  @JsonAlias("NACIONALIDADE")
  private String nacionalidade;

  @JsonProperty("naturalidadeId")
  @JsonAlias("NATURALIDADE_ID")
  private Long naturalidadeId;

  @JsonProperty("naturalidade")
  @JsonAlias("NATURALIDADE")
  private String naturalidade;

  @JsonProperty("estadoCivil")
  @JsonAlias("ESTADO_CIVIL")
  private String estadoCivil;

  @JsonProperty("sexo")
  @JsonAlias("SEXO")
  private String sexo;

  @JsonProperty("dtEmissao")
  @JsonAlias("DT_EMISSAO")
  private String dtEmissao;

  @JsonProperty("dtValidade")
  @JsonAlias("DT_VALIDADE")
  private String dtValidade;

  @JsonProperty("numDocumento")
  @JsonAlias("NUM_DOCUMENTO")
  private String numDocumento;

  @JsonProperty("idTpDoc")
  @JsonAlias("id_tp_doc")
  private String idTpDoc;

  @JsonProperty("pais")
  @JsonAlias("PAIS")
  private Long pais;

  @JsonProperty("localidade")
  @JsonAlias("LOCALIDADE")
  private String localidade;

  @JsonProperty("telemovel")
  @JsonAlias("TELEMOVEL")
  private Long telemovel;

  @JsonProperty("email")
  @JsonAlias("EMAIL")
  private String email;

  @JsonProperty("altura")
  @JsonAlias("ALTURA")
  private Double altura;

  @JsonProperty("freguesiaId")
  @JsonAlias("FREGUESIA_ID")
  private Long freguesiaId;

  @JsonProperty("localidadeId")
  @JsonAlias("LOCALIDADE_ID")
  private Long localidadeId;

  @JsonProperty("concelhoId")
  @JsonAlias("CONCELHO_ID")
  private String concelhoId;

  @JsonProperty("ilhaId")
  @JsonAlias("ILHA_ID")
  private String ilhaId;

  @JsonProperty("face")
  @JsonAlias("FACE")
  private String face;

  @JsonProperty("emissorDoc")
  @JsonAlias("EMISSOR_DOC")
  private String emissorDoc;

  @JsonProperty("emissorDescricao")
  @JsonAlias("EMISSOR_DESCRICAO")
  private String emissorDescricao;

  @JsonProperty("grayscale")
  @JsonAlias("GRAYSCALE")
  private String grayscale;

  @JsonProperty("fingerLeft")
  @JsonAlias("FINGER_LEFT")
  private String fingerLeft;

  @JsonProperty("fingerRight")
  @JsonAlias("FINGER_RIGHT")
  private String fingerRight;

  @JsonProperty("signature")
  @JsonAlias("SIGNATURE")
  private String signature;

  @JsonProperty("fingerPositionLeft")
  @JsonAlias("FINGER_POSITION_LEFT")
  private Integer fingerPositionLeft;

  @JsonProperty("fingerPositionRight")
  @JsonAlias("FINGER_POSITION_RIGHT")
  private Integer fingerPositionRight;

  @JsonProperty("dtRecebido")
  @JsonAlias("DT_RECEBIDO")
  private String dtRecebido;

  @JsonProperty("dtEntrega")
  @JsonAlias("DT_ENTREGA")
  private String dtEntrega;

  @JsonProperty("nrProcesso")
  @JsonAlias("NR_PROCESSO")
  private String nrProcesso;

  @JsonProperty("morada")
  @JsonAlias("MORADA")
  private String morada;

  @JsonProperty("tipoMorada")
  @JsonAlias("TIPO_MORADA")
  private String tipoMorada;

  @JsonProperty("localidadeNome")
  @JsonAlias("LOCALIDADE_NOME")
  private String localidadeNome;

  @JsonProperty("localNascimentoId")
  @JsonAlias("LOCAL_NASCIMENTO_ID")
  private Long localNascimentoId;
}
