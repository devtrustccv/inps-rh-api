package cv.inps.rh.shared.util;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PdfGenerator {

  private final SpringTemplateEngine templateEngine;

  public PdfGenerator(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public byte[] generate(String templateName, Map<String, Object> data) {

    var context = new Context();
    context.setVariables(data);

    var html = templateEngine.process(templateName, context);

    try (var out = new ByteArrayOutputStream()) {

      var renderer = new ITextRenderer();
      renderer.setDocumentFromString(html);
      renderer.layout();
      renderer.createPDF(out);

      return out.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Erro ao gerar PDF", e);
    }
  }
}
