package cv.inps.rh.transversal.application.strategies;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DimensaoStrategyFactory {

    private final Map<String, DimensaoStrategy> strategies = new HashMap<>();
    private final List<DimensaoStrategy> strategyList;

    @PostConstruct
    public void initStrategies() {
        for (DimensaoStrategy strategy : strategyList) {
            strategies.put(strategy.getNomeDimensao().name(), strategy);
        }
    }

    public DimensaoStrategy getStrategy(String nomeDimensao) {
        DimensaoStrategy strategy = strategies.get(nomeDimensao);
        if (strategy == null) {
            throw new IllegalArgumentException("Dimensão não suportada: " + nomeDimensao);
        }
        return strategy;
    }
}
