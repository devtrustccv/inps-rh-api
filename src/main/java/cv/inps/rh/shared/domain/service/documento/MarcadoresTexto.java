package cv.inps.rh.shared.domain.service.documento;

import org.springframework.web.util.HtmlUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Substituição de marcadores num texto — sem Spring, sem BD, para poder ser testada e usada em qualquer lado.
 *
 * <ul>
 *   <li>{@code ${nome}} → valor de {@code valores.get("nome")} (o nome aceita letras, números, "_" e ".").</li>
 *   <li>{@code ${nome:-texto}} → "texto" quando não há valor para "nome".</li>
 *   <li>Sem valor e sem valor por defeito → {@link #SEM_VALOR}, e o nome fica em {@code emFalta}.</li>
 *   <li>{@code $${nome}} → o texto literal {@code ${nome}} (para escrever um marcador sem o substituir).</li>
 * </ul>
 */
public final class MarcadoresTexto {

  /** O que sai no lugar de um marcador sem valor (igual ao que as Ordens de Serviço já mostravam). */
  public static final String SEM_VALOR = "—";

  private static final Pattern MARCADOR = Pattern.compile("(\\$?)\\$\\{([A-Za-z0-9_.]+)(?::-([^}]*))?}");

  private MarcadoresTexto() {
  }

  /** Nomes dos marcadores usados no texto, pela ordem em que aparecem (sem os escapados com $$). */
  public static Set<String> marcadoresDe(String texto) {
    if (texto == null || texto.isEmpty()) return Collections.emptySet();
    var out = new LinkedHashSet<String>();
    var m = MARCADOR.matcher(texto);
    while (m.find())
      if (m.group(1).isEmpty()) out.add(m.group(2));
    return out;
  }

  /**
   * Substitui os marcadores de {@code texto}.
   *
   * @param valores  nome → valor (o valor é convertido com {@code String.valueOf}; null ou vazio conta como sem valor)
   * @param html     escapar os valores para HTML (o texto em si não é tocado — pode ter HTML próprio)
   * @param emFalta  recebe os nomes que ficaram sem valor (pode ser null)
   */
  public static String substituir(String texto, Map<String, ?> valores, boolean html, Set<String> emFalta) {
    if (texto == null) return null;
    var m = MARCADOR.matcher(texto);
    var sb = new StringBuilder(texto.length() + 64);
    while (m.find()) {
      String substituto;
      if (!m.group(1).isEmpty()) {
        substituto = m.group(0).substring(1); // $${x} → ${x}
      } else {
        var nome = m.group(2);
        var valor = valores != null ? valores.get(nome) : null;
        var texto0 = valor == null ? null : String.valueOf(valor);
        if (texto0 == null || texto0.isBlank()) {
          texto0 = m.group(3) != null ? m.group(3) : SEM_VALOR;
          if (m.group(3) == null && emFalta != null) emFalta.add(nome);
        }
        // Só <, >, &, " e ' — os acentos ficam como estão (o gerador de PDF não conhece &atilde; e afins).
        substituto = html ? HtmlUtils.htmlEscape(texto0, "UTF-8") : texto0;
      }
      m.appendReplacement(sb, Matcher.quoteReplacement(substituto));
    }
    m.appendTail(sb);
    return sb.toString();
  }
}
