package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AntiguidadeStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.ANTIGUIDADE;
    }

    private Expression<Integer> getExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Usar join implícito para evitar problemas de múltiplos joins
        Path<ContratoEntity> contratoPath = root.get("contrVinculoId");

        // Data de início do contrato/admissão
        Expression<LocalDate> dataInicio = contratoPath.get("dataInicio");

        // Data atual do sistema
        Expression<java.sql.Date> sysdate = cb.currentDate();

        // MONTHS_BETWEEN(sysdate, dataInicio) -> diferença em meses (Oracle function)
        Expression<Double> monthsBetween = cb.function("MONTHS_BETWEEN", Double.class, sysdate, dataInicio);

        // Dividir por 12 para obter anos
        Expression<Number> years = cb.quot(monthsBetween, 12);

        // FLOOR para arredondar para baixo (anos completos)
        return cb.function("FLOOR", Integer.class, years);
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Alias "antiguidade_nome" para mapeamento no DTO
        return List.of(getExpression(root, cb).alias("antiguidade_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Agrupar pela data de início do contrato para evitar erro ORA-00979
        // O Transformer fará a agregação final dos registros com a mesma antiguidade
        // calculada
        return List.of(root.get("contrVinculoId").get("dataInicio"));
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        // Converter valores de filtro (anos) para Integer
        List<Integer> valoresInt = valores.stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        return getExpression(root, cb).in(valoresInt);
    }
}
