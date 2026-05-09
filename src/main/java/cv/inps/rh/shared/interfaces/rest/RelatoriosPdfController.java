package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.application.dto.ReportHtmlDTO;
import cv.inps.rh.shared.domain.service.OrdemServicoService;
import cv.inps.rh.shared.domain.service.RelatoriosService;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.util.PdfGenerator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios/pdf")
@Tag(name = "Relatorios", description = "Módulo relatórios")
public class RelatoriosPdfController {

  private final RelatoriosService service;
  private final OrdemServicoService osService;
  private final PdfGenerator pdfGenerator;

  public RelatoriosPdfController(RelatoriosService service, OrdemServicoService osService, PdfGenerator pdfGenerator) {
    this.service = service;
    this.osService = osService;
    this.pdfGenerator = pdfGenerator;
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

  @PostMapping("/ordem-servico/{tipo}")
  public ResponseEntity<byte[]> generate(
      @PathVariable OrdemServico tipo,
      @RequestBody ReportHtmlDTO htmlBody
  ) {

    return pdfResponse(
        pdfGenerator.generate(
            "os-general",
            osService.generate(tipo, htmlBody.getHtml())
        ),
        tipo.name() + ".pdf"
    );
  }

  @GetMapping("/ordem-servico/{tipo}/content")
  public ResponseEntity<ReportHtmlDTO> content(
      @PathVariable OrdemServico tipo,
      @RequestParam String funcionarioId
  ) {

    return ResponseEntity.ok(
        osService.content(tipo, funcionarioId)
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
