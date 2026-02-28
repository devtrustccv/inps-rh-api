package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.transversal.application.dto.AssiduidadeRowDTO;
import cv.inps.rh.transversal.application.queries.RelatorioAssiduidadeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssiduidadeStrategy {
    Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable);
}
