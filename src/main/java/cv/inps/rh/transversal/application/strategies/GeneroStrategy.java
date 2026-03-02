package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeneroStrategy implements DimensaoStrategy {

  @Override
  public DimensaoEnum getNomeDimensao() {
    return DimensaoEnum.GENERO;
  }


  private Expression<String> getTranslatedExpression(
      Root<TiposRelacionamentoEntity> root,
      CriteriaBuilder cb) {

    Expression<String> sexo = getExpression(root);

    return cb.<String>selectCase()
        .when(cb.equal(sexo, "M"), "Masculino")
        .when(cb.equal(sexo, "F"), "Feminino")
        .otherwise("Desconhecido");
  }

  private Expression<String> getExpression(Root<TiposRelacionamentoEntity> root) {
    return root.get("funId").get("sexo");
  }

  @Override
  public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root,
      CriteriaBuilder cb) {
    return List.of(
        getTranslatedExpression(root, cb).alias("genero_nome"));
  }

  @Override
  public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root,
      CriteriaBuilder cb) {
    // Agrupamos pelo valor "cru" para compatibilidade com Oracle
    return List.of(
        getExpression(root));
  }

  @Override
  public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb, List<String> valores) {
    return getExpression(root).in(valores);
  }
}
