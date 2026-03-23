package cv.inps.rh.progressaopromocao.domain.service.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record CollaboratorDTO(
    String nome,
    String cargo,
    String referenciaAtual,
    String escalaoAtual,
    String referenciaNova,
    String escalaoNovo,
    String dataEfeito
) {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.of("pt", "PT"));

  public CollaboratorDTO(
      String nome,
      String cargo,
      String referenciaAtual,
      String escalaoAtual,
      String referenciaNova,
      String escalaoNovo,
      LocalDate dataEfeito
  ) {
    this(
        nome,
        cargo,
        referenciaAtual,
        escalaoAtual,
        referenciaNova,
        escalaoNovo,
        dataEfeito != null ? dataEfeito.format(FORMATTER) : null
    );
  }
}
