package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import java.util.List;

public interface DimensaoStrategy {

    DimensaoEnum getNomeDimensao();

    /**
     * Define as colunas que aparecerão no SELECT da query.
     * Geralmente o ID e o NOME da dimensão.
     */
    List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb);

    /**
     * Define as colunas pelas quais a query será agrupada.
     * Geralmente o ID e o NOME da dimensão.
     */
    List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb);

    /**
     * Define o predicado (condição WHERE) para filtrar esta dimensão.
     * Geralmente atua sobre o ID da dimensão.
     */
    Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores);
}
