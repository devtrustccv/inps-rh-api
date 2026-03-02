package cv.inps.rh.transversal.application.service;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.dto.DossierRequestDTO;
import cv.inps.rh.transversal.application.strategies.DimensaoStrategy;
import cv.inps.rh.transversal.application.strategies.DimensaoStrategyFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DossierQueryBuilderService {

    private final DimensaoStrategyFactory strategyFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Tuple> executarQueryAgrupada(DossierRequestDTO request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<TiposRelacionamentoEntity> root = cq.from(TiposRelacionamentoEntity.class);

        // --- LÓGICA DE AGRUPADORES ---
        List<Expression<?>> groupByExpressions = new ArrayList<>();
        List<Selection<?>> selections = new ArrayList<>();

        for (String nomeAgrupador : request.getAgrupadores()) {
            DimensaoStrategy strategy = strategyFactory.getStrategy(nomeAgrupador);
            selections.addAll(strategy.getSelectExpressions(root, cb));
            groupByExpressions.addAll(strategy.getGroupByExpressions(root, cb));
        }
        selections.add(cb.count(root).alias("total"));

        // --- LÓGICA DE FILTROS ---
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("estActAdm"), 1));

        if (request.getFiltros() != null && !request.getFiltros().isEmpty()) {
            for (Map.Entry<String, List<String>> filtro : request.getFiltros().entrySet()) {
                DimensaoStrategy strategy = strategyFactory.getStrategy(filtro.getKey());
                predicates.add(strategy.getFiltroPredicate(root, cb, filtro.getValue()));
            }
        }

        // --- APLICAR À QUERY ---
        cq.multiselect(selections);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.groupBy(groupByExpressions);

        // --- EXECUTAR A QUERY ---
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
        return typedQuery.getResultList();
    }
}
