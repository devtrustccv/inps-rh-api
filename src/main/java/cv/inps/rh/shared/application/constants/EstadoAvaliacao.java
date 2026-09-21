package cv.inps.rh.shared.application.constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Estado de uma avaliação, como o ecrã da grelha o apresenta.
 *
 * <p>A spec não enumera os estados — descreve só {@code RH_T_AVD.ESTADO} — mas a grelha
 * (image16.png) mostra quatro etiquetas: RASCUNHO, PENDENTE, EM AVALIAÇÃO e CONCLUÍDO.
 * O modelo grava três códigos, por isso RASCUNHO e PENDENTE partilham o {@code A}:
 * a avaliação nasce em RASCUNHO e passa a PENDENTE quando já tem períodos por avaliar.</p>
 *
 * <p>Enquanto o negócio não confirmar a distinção, {@code A} é apresentado como PENDENTE
 * assim que existir pelo menos um período definido. Ver a nota N4 no relatório da bateria.</p>
 */
public enum EstadoAvaliacao {

  RASCUNHO("A", "Rascunho"),
  PENDENTE("A", "Pendente"),
  EM_AVALIACAO("P", "Em avaliação"),
  CONCLUIDO("C", "Concluído");

  private final String codigo;
  private final String descricao;

  EstadoAvaliacao(String codigo, String descricao) {
    this.codigo = codigo;
    this.descricao = descricao;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getDescricao() {
    return descricao;
  }

  /**
   * O estado a mostrar, a partir do código gravado e de haver ou não períodos definidos.
   *
   * @param codigo          RH_T_AVD.ESTADO
   * @param temPeriodos     se já existe algum RH_T_AVD_DETALHE
   */
  public static EstadoAvaliacao resolver(String codigo, boolean temPeriodos) {
    if (codigo == null) {
      return RASCUNHO;
    }
    return switch (codigo.trim().toUpperCase()) {
      case "C" -> CONCLUIDO;
      case "P" -> EM_AVALIACAO;
      default -> temPeriodos ? PENDENTE : RASCUNHO;
    };
  }

  public static Optional<EstadoAvaliacao> fromNome(String nome) {
    if (nome == null) {
      return Optional.empty();
    }
    var chave = nome.trim().toUpperCase().replace(' ', '_');
    return Arrays.stream(values()).filter(v -> v.name().equals(chave)).findFirst();
  }
}
