package cv.inps.rh.shared.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Pesquisa de texto livre (ex.: por nome) insensível a maiúsculas e acentos.
 *
 * <p>Os dois lados são normalizados da mesma forma: o texto do utilizador aqui em Java
 * ({@link #termos}) e a coluna na BD ({@link #normalizar}). A BD é Oracle 11g com NLS_COMP=BINARY,
 * por isso não há collation {@code _AI}: o lado da BD usa {@code TRANSLATE(LOWER(col))}.
 * Os wildcards {@code %} e {@code _} do utilizador são escapados — há nomes que os contêm.
 */
public final class PesquisaTexto {

  public static final char ESCAPE = '\\';

  // Mapeamento 1:1 por posição, só minúsculas: o TRANSLATE corre sobre LOWER(col).
  private static final String COM_ACENTO = "áàâãäåéèêëíìîïóòôõöúùûüçñý";
  private static final String SEM_ACENTO = "aaaaaaeeeeiiiiooooouuuucny";

  private static final Pattern DIACRITICOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  private PesquisaTexto() {
  }

  /** Termos do texto, sem acentos e em minúsculas, sem repetidos; vazio se o texto não tiver conteúdo. */
  public static List<String> termos(String texto) {
    if (!StringUtils.hasText(texto))
      return List.of();

    var semAcento = DIACRITICOS.matcher(Normalizer.normalize(texto, Normalizer.Form.NFD)).replaceAll("");
    return Arrays.stream(semAcento.toLowerCase(Locale.ROOT).trim().split("\\s+")).distinct().toList();
  }

  /** Padrão LIKE "contém" para um termo de {@link #termos}; usar com {@link #ESCAPE}. */
  public static String contem(String termo) {
    return "%" + escapar(termo) + "%";
  }

  /** Padrão LIKE "começa por" para um termo de {@link #termos}; usar com {@link #ESCAPE}. */
  public static String comecaPor(String termo) {
    return escapar(termo) + "%";
  }

  /** Coluna normalizada do lado da BD (minúsculas, sem acentos), comparável com {@link #termos}. */
  public static Expression<String> normalizar(CriteriaBuilder cb, Expression<String> coluna) {
    return cb.function("TRANSLATE", String.class, cb.lower(coluna), cb.literal(COM_ACENTO), cb.literal(SEM_ACENTO));
  }

  private static String escapar(String termo) {
    return termo.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
