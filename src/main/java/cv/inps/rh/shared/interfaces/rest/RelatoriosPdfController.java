package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.application.constants.custom.RelatorioTemplate;
import cv.inps.rh.shared.application.dto.MinioFileDataDTO;
import cv.inps.rh.shared.application.dto.ReportHtmlDTO;
import cv.inps.rh.shared.domain.service.OrdemServicoService;
import cv.inps.rh.shared.domain.service.RelatoriosService;
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

  @GetMapping("/ordem-servico")
  public ResponseEntity<MinioFileDataDTO> ordemServicoPdf(@RequestParam RelatorioTemplate template) {
    return ResponseEntity.ok(service.ordemServico(template));
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

  @PostMapping("/os/fim-comissao-servico")
  public ResponseEntity<byte[]> fimComissaoServico(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.fimComissaoServico(htmlBody.getHtml())),
        "Fim Comissão Serviço.pdf"
    );
  }

  @GetMapping("/os/fim-comissao-servico/content")
  public ResponseEntity<ReportHtmlDTO> fimComissaoServico(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.getFimComissaoServicoContent(funcionarioId));
  }

  @PostMapping("/os/conversao-contrato")
  public ResponseEntity<byte[]> conversaoContrato(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.conversaoContrato(htmlBody.getHtml())),
        "Conversao Contrato.pdf"
    );
  }

  @GetMapping("/os/conversao-contrato/content")
  public ResponseEntity<ReportHtmlDTO> conversaoContrato(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.conversaoContratoContent(funcionarioId));
  }

  @PostMapping("/os/licensa-sem-vencimento")
  public ResponseEntity<byte[]> licensaSemVencimento(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.licensaSemVencimento(htmlBody.getHtml())),
        "Licensa Sem Vencimento.pdf"
    );
  }

  @GetMapping("/os/licensa-sem-vencimento/content")
  public ResponseEntity<ReportHtmlDTO> licensaSemVencimento(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.licensaSemVencimentoContent(funcionarioId));
  }

  @PostMapping("/os/progressao-cargo")
  public ResponseEntity<byte[]> progressaoCargo(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.progressaoCargo(htmlBody.getHtml())),
        "Progressao Cargo.pdf"
    );
  }

  @GetMapping("/os/progressao-cargo/content")
  public ResponseEntity<ReportHtmlDTO> progressaoCargo(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.progressaoCargoContent(funcionarioId));
  }

  @PostMapping("/os/progressao-categoria")
  public ResponseEntity<byte[]> progressaoCategoria(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.progressaoCategoria(htmlBody.getHtml())),
        "Progressao Categoria.pdf"
    );
  }

  @GetMapping("/os/progressao-categoria/content")
  public ResponseEntity<ReportHtmlDTO> progressaoCategoria(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.progressaoCategoriaContent(funcionarioId));
  }

  @PostMapping("/os/substituicao")
  public ResponseEntity<byte[]> substituicao(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.substituicao(htmlBody.getHtml())),
        "Substituicao.pdf"
    );
  }

  @GetMapping("/os/substituicao/content")
  public ResponseEntity<ReportHtmlDTO> substituicao(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.substituicaoContent(funcionarioId));
  }

  @PostMapping("/os/transferencia")
  public ResponseEntity<byte[]> transferencia(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.transferencia(htmlBody.getHtml())),
        "Transferencia.pdf"
    );
  }

  @GetMapping("/os/transferencia/content")
  public ResponseEntity<ReportHtmlDTO> transferencia(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.transferenciaContent(funcionarioId));
  }

  @PostMapping("/os/mobilidade-interna")
  public ResponseEntity<byte[]> mobilidadeInterna(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.mobilidadeInterna(htmlBody.getHtml())),
        "Mobilidade Interna.pdf"
    );
  }

  @GetMapping("/os/mobilidade-interna/content")
  public ResponseEntity<ReportHtmlDTO> mobilidadeInterna(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.mobilidadeInternaContent(funcionarioId));
  }

  @PostMapping("/os/requalificacao")
  public ResponseEntity<byte[]> requalificacao(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.requalificacao(htmlBody.getHtml())),
        "Requalificacao.pdf"
    );
  }

  @GetMapping("/os/requalificacao/content")
  public ResponseEntity<ReportHtmlDTO> requalificacao(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.requalificacaoContent(funcionarioId));
  }

  @PostMapping("/os/nomeacao-coordenador")
  public ResponseEntity<byte[]> nomeacaoCoordenador(@RequestBody ReportHtmlDTO htmlBody) {
    return pdfResponse(
        pdfGenerator.generate("os-general", osService.nomeacaoCoordenador(htmlBody.getHtml())),
        "Nomeacao Coordenador.pdf"
    );
  }

  @GetMapping("/os/nomeacao-coordenador/content")
  public ResponseEntity<ReportHtmlDTO> nomeacaoCoordenador(@RequestParam String funcionarioId) {
    return ResponseEntity.ok(osService.nomeacaoCoordenadorContent(funcionarioId));
  }

  private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(pdf.length)
        .body(pdf);
  }
}
