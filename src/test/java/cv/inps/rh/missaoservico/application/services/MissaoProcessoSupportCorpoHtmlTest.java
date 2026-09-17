package cv.inps.rh.missaoservico.application.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MissaoProcessoSupportCorpoHtmlTest {

  @Test
  void textoSimplesPassaAHtmlComQuebrasDeLinha() {
    assertThat(MissaoProcessoSupport.corpoHtml("Exmo(a) Sr(a),\r\n\nMissão Nº 7/2026 <teste>"))
        .isEqualTo("Exmo(a) Sr(a),<br/><br/>Miss&atilde;o N&ordm; 7/2026 &lt;teste&gt;");
  }

  @Test
  void corpoQueJaEHtmlSegueComoEsta() {
    var html = "<p>Exmo(a) Sr(a),</p><p>Missão</p>";
    assertThat(MissaoProcessoSupport.corpoHtml(html)).isEqualTo(html);
  }

  @Test
  void nuloSegueNulo() {
    assertThat(MissaoProcessoSupport.corpoHtml(null)).isNull();
  }
}
