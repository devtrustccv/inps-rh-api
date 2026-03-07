package cv.inps.rh.transversal.application.queries;

import cv.inps.rh.shared.util.PdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


@Component
public class ExtrairFichaEfetividadeQueryHandler implements QueryHandler<ExtrairFichaEfetividadeQuery, ResponseEntity<?>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtrairFichaEfetividadeQueryHandler.class);

  private final PdfGenerator pdfGenerator;

  public ExtrairFichaEfetividadeQueryHandler(PdfGenerator pdfGenerator) {

    this.pdfGenerator = pdfGenerator;
  }

   @IgrpQueryHandler
  public ResponseEntity<?> handle(ExtrairFichaEfetividadeQuery query) {

    LOGGER.debug("ExtrairFichaEfetividadeQuery: {}", query);

     int mesReferencia = query.getMes();
     int anoReferencia = query.getAno();

     String logoPath = Optional.ofNullable(getClass().getResource("/static/img/inps_logo.png"))
         .map(URL::toExternalForm)
         .orElse("");

     Map<String, Object> context = Map.of(
         "direcao", "Direção Administrativa e de Recursos Humanos",
         "dataEmissao", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
         "mesReferencia", Month.of(mesReferencia).getDisplayName(TextStyle.FULL, new Locale("pt", "PT")),
         "anoReferencia", anoReferencia,
         "mesVencimento", Month.of(mesReferencia).getDisplayName(TextStyle.FULL, new Locale("pt", "PT")),
             "logoPath", logoPath
     );

     byte[] pdf = pdfGenerator.generate("efectividade", context);

     return ResponseEntity.ok()
         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=declaracao.pdf")
         .contentType(MediaType.APPLICATION_PDF)
         .body(pdf);
  }

}
