package cv.inps.rh.processamento.application;

import cv.inps.rh.processamento.application.dto.SoatPdfDTO;
import cv.inps.rh.processamento.application.dto.SoatPdfRowDTO;
import cv.inps.rh.shared.util.PdfGenerator;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SoatPdfTemplateTest {

  @Test
  void shouldProcessThymeleafTemplateAndGeneratePdf() throws Exception {
    var resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
    var engine = new SpringTemplateEngine();
    engine.setTemplateResolver(resolver);
    var generator = new PdfGenerator(engine);

    var row = new SoatPdfRowDTO(
        "Maria Silva", "NIC", "1234567890123", "31/12/2030", "123456789",
        "01/01/1990", "Feminino", "M", "Técnica", "Não",
        new BigDecimal("44.00"), "M", new BigDecimal("85000.00"),
        new BigDecimal("1020000.00"), "Não", ""
    );
    var logo = getClass().getResourceAsStream("/static/img/inps_logo.png");
    var logoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(logo.readAllBytes());
    var data = new SoatPdfDTO(
        logoBase64, "26/08/2026", "08/2026", "SAL-001", "01/01/2026",
        "INPS", "255555550", "CAE-01", "Segurança social", "CERT-01",
        "31/12/2030", "2600000", "9912345", "Praia", "inps@example.cv",
        "Plateau", "1", 1, new BigDecimal("1020000.00"), List.of(row)
    );

    var pdf = generator.generate("soat-pdf", Map.of("soat", data));

    assertTrue(pdf.length > 1000);
    assertTrue(new String(pdf, 0, 5, StandardCharsets.US_ASCII).startsWith("%PDF-"));
  }
}
