package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IdadeStrategy implements DimensaoStrategy {

  @Override
  public DimensaoEnum getNomeDimensao() {
    return DimensaoEnum.IDADE;
  }

  private Expression<Integer> getExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
    // Cálculo de idade para Oracle: FLOOR(MONTHS_BETWEEN(SYSDATE, DATA_NASCIMENTO)
    // / 12)
    Expression<java.time.LocalDate> dataNascimento = root.get("funId").get("dataNascimento");
    Expression<java.sql.Date> sysdate = cb.currentDate();

    // MONTHS_BETWEEN retorna a diferença em meses
    Expression<Double> monthsBetween = cb.function("MONTHS_BETWEEN", Double.class, sysdate, dataNascimento);

    // Dividir por 12 para ter anos
    Expression<Number> years = cb.quot(monthsBetween, 12);

    // FLOOR para obter a idade completa (arredondar para baixo)
    return cb.function("FLOOR", Integer.class, years);
  }

  @Override
  public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
    // Usamos alias "idade_nome" para que o transformador consiga ler o valor
    return List.of(getExpression(root, cb).alias("idade_nome"));
  }

  @Override
  public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
    // Agrupamos pela data de nascimento para evitar problemas com parâmetros no
    // GROUP BY (ORA-00979).
    // O Transformer encarrega-se de somar os totais das várias datas que resultam
    // na mesma idade.
    return List.of(root.get("funId").get("dataNascimento"));
  }

  @Override
  public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
    // Converter valores string para Integer para comparar com a idade calculada
    List<Integer> valoresInt = valores.stream()
        .map(Integer::valueOf)
        .collect(Collectors.toList());
    return getExpression(root, cb).in(valoresInt);
  }
}
