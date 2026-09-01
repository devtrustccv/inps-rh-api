package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PdfGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(PdfGenerator.class);

  private final SpringTemplateEngine templateEngine;

  public PdfGenerator(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public byte[] generate(String templateName, Context data) {

    var html = templateEngine.process(templateName, data);

    return generateFromHtml(html);
  }

  public byte[] generateFromHtml(String html) {

    try (var out = new ByteArrayOutputStream()) {

      var renderer = new ITextRenderer();
      renderer.setDocumentFromString(html);
      renderer.layout();
      renderer.createPDF(out);

      return out.toByteArray();

    } catch (Exception e) {
      LOGGER.error("Erro ao gerar PDF", e);
      throw IgrpResponseStatusException.internalServerError("Erro ao gerar document pdf");
    }
  }

  public String generateAsString(String templateName, Context data) {
    return templateEngine.process(templateName, data);
  }

  public byte[] generate(String templateName, Map<String, Object> data) {
    var context = new Context();
    context.setVariables(data);
    return generate(templateName, context);
  }
}
