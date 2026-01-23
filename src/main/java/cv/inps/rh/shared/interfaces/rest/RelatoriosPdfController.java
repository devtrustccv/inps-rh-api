package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.application.dto.MinioFileDataDTO;
import cv.inps.rh.shared.domain.service.RelatoriosService;
import cv.inps.rh.shared.util.PdfGenerator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios/pdf")
@Tag(name = "Relatorios", description = "Módulo relatórios")
public class RelatoriosPdfController {

  private final RelatoriosService service;
  private final PdfGenerator pdfGenerator;

  public RelatoriosPdfController(RelatoriosService service, PdfGenerator pdfGenerator) {
    this.service = service;
    this.pdfGenerator = pdfGenerator;
  }

  @GetMapping("/ordem-servico")
  public ResponseEntity<MinioFileDataDTO> ordemServicoPdf() {
    return ResponseEntity.ok(service.ordemServico());
  }

  @GetMapping("/recibos-salario")
  public ResponseEntity<byte[]> recibosSalarioPdf() {
    return pdfResponse(pdfGenerator.generate("recibo-salario", service.recibosSalario()), "recibos-salario.pdf");
  }

  @GetMapping("/processamento-salarios")
  public ResponseEntity<byte[]> processamentoSalariosPdf(@RequestParam Long processamentoId, @RequestParam String tipo) {
    return pdfResponse(
        pdfGenerator.generate("processamento-salarios", service.processamentoSalarios(processamentoId, tipo)),
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
