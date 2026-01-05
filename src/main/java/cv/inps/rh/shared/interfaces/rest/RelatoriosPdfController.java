package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.domain.service.RelatoriosService;
import cv.inps.rh.shared.util.PdfGenerator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios/pdf")
@Tag(name = "Relatorios", description = "Módulo relatorios")
public class RelatoriosPdfController {

  private final RelatoriosService service;
  private final PdfGenerator pdf;

  public RelatoriosPdfController(RelatoriosService service, PdfGenerator pdf) {
    this.service = service;
    this.pdf = pdf;
  }

  @GetMapping("/ordem-servico")
  public ResponseEntity<byte[]> ordemServicoPdf() {
    return pdfResponse(
        pdf.generate("ordem-servico", service.ordemServico()),
        "ordem-servico.pdf"
    );
  }

  @GetMapping("/recibos-salario")
  public ResponseEntity<byte[]> recibosSalarioPdf() {
    return pdfResponse(
        pdf.generate("recibo-salario", service.recibosSalario()),
        "recibos-salario.pdf"
    );
  }

  @GetMapping("/processamento-salarios")
  public ResponseEntity<byte[]> processamentoSalariosPdf() {
    return pdfResponse(
        pdf.generate("processamento-salarios", service.processamentoSalarios()),
        "processamento-salarios.pdf"
    );
  }

  private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(pdf.length)
        .body(pdf);
  }
}
