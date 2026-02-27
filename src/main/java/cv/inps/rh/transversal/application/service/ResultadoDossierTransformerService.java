package cv.inps.rh.transversal.application.service;

import cv.inps.rh.transversal.application.dto.AgrupamentoDTO;
import cv.inps.rh.transversal.application.dto.DossierResponseDTO;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResultadoDossierTransformerService {

    public DossierResponseDTO transformar(List<Tuple> resultados, List<String> agrupadores) {

        DossierResponseDTO response = new DossierResponseDTO();
        response.setAgrupadores(agrupadores);

        // Iniciar o processo de transformação recursiva
        List<AgrupamentoDTO> resultadoHierarquico = construirHierarquia(resultados, agrupadores);
        response.setResultado(resultadoHierarquico);

        // Calcular o total geral a partir do primeiro nível do resultado
        long totalGeral = resultadoHierarquico.stream()
                .mapToLong(AgrupamentoDTO::getTotal)
                .sum();
        response.setTotalGeral(totalGeral);

        return response;
    }

    private List<AgrupamentoDTO> construirHierarquia(List<Tuple> resultados, List<String> agrupadores) {
        // Condição de paragem da recursão
        if (agrupadores.isEmpty() || resultados.isEmpty()) {
            return new ArrayList<>();
        }

        String agrupadorAtual = agrupadores.getFirst();
        // O alias do ID é sempre o nome do agrupador + "_id"
        String aliasId = agrupadorAtual.toLowerCase() + "_id";
        String aliasNome = agrupadorAtual.toLowerCase() + "_nome";

        // Verifica se o alias de ID existe na tupla. Se não, usa o de nome.
        boolean hasIdAlias = resultados.getFirst().getElements().stream()
                .anyMatch(elem -> elem.getAlias().equalsIgnoreCase(aliasId));

        String aliasAgrupamento = hasIdAlias ? aliasId : aliasNome;

        // Agrupa os resultados pelo valor da dimensão atual
        Map<Object, List<Tuple>> grupos = resultados.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(aliasAgrupamento)));

        List<AgrupamentoDTO> listaAgrupamentos = new ArrayList<>();

        for (Map.Entry<Object, List<Tuple>> entry : grupos.entrySet()) {
            AgrupamentoDTO dto = new AgrupamentoDTO();
            dto.setDimensao(agrupadorAtual);
            
            // Correção: Obter como Object e converter para String para suportar tipos numéricos (ex: idade)
            Object valorObj = entry.getValue().getFirst().get(aliasNome);
            dto.setValor(valorObj != null ? String.valueOf(valorObj) : null);

            // Soma o total para este grupo específico
            long totalGrupo = entry.getValue().stream()
                    .mapToLong(tuple -> tuple.get("total", Long.class))
                    .sum();
            dto.setTotal(totalGrupo);

            // --- RECURSÃO ---
            List<String> subAgrupadores = agrupadores.subList(1, agrupadores.size());
            dto.setSubAgrupamentos(construirHierarquia(entry.getValue(), subAgrupadores));

            listaAgrupamentos.add(dto);
        }

        return listaAgrupamentos;
    }
}
