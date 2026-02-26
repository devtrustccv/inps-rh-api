package cv.inps.rh.transversal.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class DossierResponseDTO {
    private Long totalGeral;
    private List<String> agrupadores;
    private Map<String, List<String>> filtros;
    private List<AgrupamentoDTO> resultado;
}
