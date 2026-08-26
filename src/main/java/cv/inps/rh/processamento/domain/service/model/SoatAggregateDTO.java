package cv.inps.rh.processamento.domain.service.model;

import java.math.BigDecimal;

public record SoatAggregateDTO(Long soatId, BigDecimal totalRemuneracao, Long totalColaboradores) {
}
