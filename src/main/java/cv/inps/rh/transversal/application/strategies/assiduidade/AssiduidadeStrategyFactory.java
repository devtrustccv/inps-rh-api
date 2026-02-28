package cv.inps.rh.transversal.application.strategies.assiduidade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssiduidadeStrategyFactory {

    private final Map<String, AssiduidadeStrategy> strategies;

    public AssiduidadeStrategy getStrategy(String tipoAssiduidade) {
        String key = switch (tipoAssiduidade) {
            case "FERIAS" -> "feriasAssiduidadeStrategy";
            case "FALTA" -> "faltaAssiduidadeStrategy";
            case "HORA_EXTRA" -> "horaExtraAssiduidadeStrategy";
            case "DISPENSA" -> "dispensaAssiduidadeStrategy";
            default -> throw new IllegalArgumentException("Invalid assiduity type: " + tipoAssiduidade);
        };
        
        return Optional.ofNullable(strategies.get(key))
                .orElseThrow(() -> new IllegalArgumentException("Strategy bean not found for key: " + key));
    }
}
