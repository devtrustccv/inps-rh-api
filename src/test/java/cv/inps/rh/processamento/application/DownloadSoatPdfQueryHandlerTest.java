package cv.inps.rh.processamento.application;

import cv.inps.rh.processamento.application.queries.DownloadSoatPdfQuery;
import cv.inps.rh.processamento.application.queries.DownloadSoatPdfQueryHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import cv.inps.rh.processamento.domain.service.model.SoatPdfResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DownloadSoatPdfQueryHandlerTest {

  @Test
  void shouldReturnPdfAsAttachment() {
    var service = mock(SoatService.class);
    var pdf = "%PDF-1.4".getBytes();
    when(service.gerarFicheiroSoat("soat-uuid", "policy-uuid"))
        .thenReturn(new SoatPdfResult("soat-202608-SAL-01.pdf", pdf));
    var handler = new DownloadSoatPdfQueryHandler(service);

    var response = handler.handle(new DownloadSoatPdfQuery("soat-uuid", "policy-uuid"));

    assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    assertEquals(pdf.length, response.getHeaders().getContentLength());
    assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
        .contains("soat-202608-SAL-01.pdf"));
    assertArrayEquals(pdf, response.getBody());
    verify(service).gerarFicheiroSoat("soat-uuid", "policy-uuid");
  }
}
