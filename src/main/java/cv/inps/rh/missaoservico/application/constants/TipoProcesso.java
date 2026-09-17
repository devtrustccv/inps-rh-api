package cv.inps.rh.missaoservico.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static cv.inps.rh.missaoservico.application.constants.EtapaProcesso.*;

/**
 * Os quatro processos de uma missão — domínio TIPO_PROCESSO — e o percurso de etapas de cada um.
 *
 * <p>Decisão D2 do plano: bilhete e alojamento são contratados a um prestador (pedido de proposta
 * e requisição); o seguro escolhe a seguradora directamente na logística e a ajuda de custo é paga
 * ao colaborador, por isso começam em LOGISTICA. Daí em diante o percurso é comum.
 */
public enum TipoProcesso {

  BILHETE_PASSAGEM("Bilhete Passagem", true),
  SEGURO_VIAGEM("Seguro Viagem", false),
  AJUDA_CUSTO("Ajuda Custo", false),
  ALOJAMENTO("Alojamento", true);

  private final String descricao;
  private final List<EtapaProcesso> etapas;

  TipoProcesso(String descricao, boolean comPrestador) {
    this.descricao = descricao;
    this.etapas = comPrestador
        ? concat(List.of(PRESTADOR_SERVICO, EMISSAO_REQUISICAO), percursoComum())
        : percursoComum();
  }

  // Método e não constante: um construtor de enum não pode ler campos static do próprio enum.
  private static List<EtapaProcesso> percursoComum() {
    return List.of(LOGISTICA, VALIDACAO_UGAL, APROVACAO_RH, CABIMENTO, AUTORIZACAO, PAGAMENTO);
  }

  public String getDescricao() {
    return descricao;
  }

  /** Etapas que este processo percorre, por ordem. */
  public List<EtapaProcesso> getEtapas() {
    return etapas;
  }

  public EtapaProcesso primeiraEtapa() {
    return etapas.getFirst();
  }

  /** Se o processo passa por Prestadores Serviço e Emissão de Requisição. */
  public boolean temPrestador() {
    return etapas.contains(PRESTADOR_SERVICO);
  }

  public boolean percorre(EtapaProcesso etapa) {
    return etapas.contains(etapa);
  }

  /** Etapa seguinte no percurso; null quando {@code etapa} é a última. */
  public EtapaProcesso seguinte(EtapaProcesso etapa) {
    var i = etapas.indexOf(etapa);
    return i >= 0 && i + 1 < etapas.size() ? etapas.get(i + 1) : null;
  }

  public static TipoProcesso fromCodeOrThrow(String code) {
    return Arrays.stream(values())
        .filter(t -> t.name().equals(code))
        .findFirst()
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo de processo inválido: " + code));
  }

  private static List<EtapaProcesso> concat(List<EtapaProcesso> a, List<EtapaProcesso> b) {
    var out = new java.util.ArrayList<>(a);
    out.addAll(b);
    return List.copyOf(out);
  }
}
