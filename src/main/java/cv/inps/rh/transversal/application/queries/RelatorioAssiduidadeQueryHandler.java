package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.transversal.application.dto.AssiduidadeListDTO;
import cv.inps.rh.transversal.application.dto.AssiduidadeRowDTO;
import cv.inps.rh.transversal.application.strategies.assiduidade.AssiduidadeStrategy;
import cv.inps.rh.transversal.application.strategies.assiduidade.AssiduidadeStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RelatorioAssiduidadeQueryHandler
        implements QueryHandler<RelatorioAssiduidadeQuery, ResponseEntity<AssiduidadeListDTO>> {

    private final AssiduidadeStrategyFactory strategyFactory;

    @Override
    @IgrpQueryHandler
    public ResponseEntity<AssiduidadeListDTO> handle(RelatorioAssiduidadeQuery query) {

        // 1. Validar e definir filtros padrão
        if (!StringUtils.hasText(query.getDataInicio())) {
            LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
            query.setDataInicio(DateFormatter.localDateToString(startOfYear));
        }

        if (!StringUtils.hasText(query.getDataFim())) {
            LocalDate today = LocalDate.now();
            query.setDataFim(DateFormatter.localDateToString(today));
        }

        // 2. Obter Strategy apropriado
        AssiduidadeStrategy strategy = strategyFactory.getStrategy(query.getTipoAssiduidade());

        // 3. Paginação
        int page = 0;
        int size = 10;
        try {
            if (StringUtils.hasText(query.getPageNumber())) {
                page = Integer.parseInt(query.getPageNumber());
            }
            if (StringUtils.hasText(query.getPageSize())) {
                size = Integer.parseInt(query.getPageSize());
            }
        } catch (NumberFormatException e) {
            // Log warning, use defaults
        }
        Pageable pageable = PageRequest.of(page, size);

        // 4. Executar busca
        Page<AssiduidadeRowDTO> result = strategy.filtrar(query, pageable);

        // 5. Mapear resposta
        AssiduidadeListDTO responseDTO = new AssiduidadeListDTO();
        PageMapper.fillPagination(result, responseDTO);
        responseDTO.setContent(result.getContent());

        return ResponseEntity.ok(responseDTO);
    }
}
