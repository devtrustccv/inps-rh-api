package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVMapaPessoalEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVMapaPessoalEntityRepository;
import cv.inps.rh.shared.util.PdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExtrairMapaPessoalQueryHandler implements QueryHandler<ExtrairMapaPessoalQuery, ResponseEntity<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtrairMapaPessoalQueryHandler.class);

    private final PdfGenerator pdfGenerator;
    private final RhVMapaPessoalEntityRepository repository;

    public ExtrairMapaPessoalQueryHandler(PdfGenerator pdfGenerator,
                                          RhVMapaPessoalEntityRepository repository) {
        this.pdfGenerator = pdfGenerator;
        this.repository = repository;
    }

    @IgrpQueryHandler
    public ResponseEntity<?> handle(ExtrairMapaPessoalQuery query) {
        LOGGER.debug("ExtrairMapaPessoalQuery: {}", query);

        List<RhVMapaPessoalEntity> rows = repository.findAll();

        String logoPath = Optional.ofNullable(getClass().getResource("/static/img/inps_logo.png"))
            .map(URL::toExternalForm)
            .orElse("");

        Map<String, Object> context = Map.of(
            "dataEmissao", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            "logoPath", logoPath,
            "rows", rows
        );

        byte[] pdf = pdfGenerator.generate("mapa-pessoal", context);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mapa-pessoal.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
