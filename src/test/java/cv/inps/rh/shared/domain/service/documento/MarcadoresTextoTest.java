package cv.inps.rh.shared.domain.service.documento;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static cv.inps.rh.shared.domain.service.documento.MarcadoresTexto.marcadoresDe;
import static cv.inps.rh.shared.domain.service.documento.MarcadoresTexto.substituir;
import static org.junit.jupiter.api.Assertions.*;

class MarcadoresTextoTest {

  @Test
  void substituiOsMarcadoresComOsValores() {
    var out = substituir("Transfere-se ${nomeColaborador}, ${cargo}.", Map.of("nomeColaborador", "Wilson", "cargo", "Técnico"), false, null);
    assertEquals("Transfere-se Wilson, Técnico.", out);
  }

  @Test
  void valorNaoTextoUsaStringValueOf() {
    assertEquals("Período: 14 meses", substituir("Período: ${periodo} meses", Map.of("periodo", 14L), false, null));
  }

  @Test
  void semValorSaiTracoERegistaEmFalta() {
    var emFalta = new LinkedHashSet<String>();
    var valores = new HashMap<String, Object>();
    valores.put("nulo", null);
    valores.put("vazio", "  ");
    var out = substituir("${inexistente}|${nulo}|${vazio}", valores, false, emFalta);
    assertEquals("—|—|—", out);
    assertEquals(List.of("inexistente", "nulo", "vazio"), List.copyOf(emFalta));
  }

  @Test
  void valorPorDefeitoQuandoNaoHaValor() {
    var emFalta = new LinkedHashSet<String>();
    var out = substituir("Reunião: ${dataReuniao:-a definir}; ${x:-}", Map.of(), false, emFalta);
    assertEquals("Reunião: a definir; ", out);
    assertTrue(emFalta.isEmpty(), "com valor por defeito não conta como em falta");
    assertEquals("Reunião: 12/11", substituir("Reunião: ${dataReuniao:-a definir}", Map.of("dataReuniao", "12/11"), false, null));
  }

  @Test
  void cifraDuplaEscreveOMarcadorLiteral() {
    assertEquals("use ${nome} assim: Ana", substituir("use $${nome} assim: ${nome}", Map.of("nome", "Ana"), false, null));
    assertEquals(List.of("nome"), List.copyOf(marcadoresDe("use $${outro} e ${nome}")));
  }

  @Test
  void htmlEscapaSoOsValoresENaoOsAcentos() {
    var out = substituir("<p>${nome}</p>", Map.of("nome", "João & <Filhos> \"Lda\""), true, null);
    assertEquals("<p>João &amp; &lt;Filhos&gt; &quot;Lda&quot;</p>", out);
    assertEquals("<p>João & <Filhos></p>", substituir("<p>${nome}</p>", Map.of("nome", "João & <Filhos>"), false, null));
  }

  @Test
  void valoresComCaracteresEspeciaisDeRegexNaoPartem() {
    assertEquals("Valor: $100 \\ ok", substituir("Valor: ${v}", Map.of("v", "$100 \\ ok"), false, null));
  }

  @Test
  void nomesComPontoESemMarcadores() {
    assertEquals("Lisboa", substituir("${morada.cidade}", Map.of("morada.cidade", "Lisboa"), false, null));
    assertEquals("texto sem marcadores", substituir("texto sem marcadores", Map.of(), true, null));
    assertNull(substituir(null, Map.of(), false, null));
    assertEquals("—", substituir("${a}", null, false, null));
  }

  @Test
  void marcadoresDeListaPorOrdemSemRepetir() {
    assertEquals(List.of("b", "a"), List.copyOf(marcadoresDe("${b} ${a} ${b:-x}")));
    assertTrue(marcadoresDe(null).isEmpty());
  }
}
