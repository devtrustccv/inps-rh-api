package cv.inps.rh.transversal.application.strategies;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FaixaEtariaStrategy implements DimensaoStrategy {

    @Override
    public DimensaoEnum getNomeDimensao() {
        return DimensaoEnum.FAIXA_ETARIA;
    }

    // Reutiliza a lógica de cálculo de idade
    private Expression<Integer> getIdadeExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        Expression<java.time.LocalDate> dataNascimento = root.get("funId").get("dataNascimento");
        Expression<java.sql.Date> sysdate = cb.currentDate();
        Expression<Double> monthsBetween = cb.function("MONTHS_BETWEEN", Double.class, sysdate, dataNascimento);
        Expression<Number> years = cb.quot(monthsBetween, 12);
        return cb.function("FLOOR", Integer.class, years);
    }

    private Expression<Integer> getDecadaExpression(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        Expression<Integer> idade = getIdadeExpression(root, cb);
        // FLOOR(idade / 10) -> Retorna 2 para 23, 3 para 35, etc.
        return cb.function("FLOOR", Integer.class, cb.quot(idade, 10));
    }

    @Override
    public List<Selection<?>> getSelectExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        Expression<Integer> decadaBase = getDecadaExpression(root, cb);

        // Inicio da faixa: decadaBase * 10 (ex: 2 * 10 = 20)
        Expression<Integer> inicioFaixa = cb.prod(decadaBase, 10);

        // Fim da faixa: inicioFaixa + 9 (ex: 20 + 9 = 29)
        Expression<Integer> fimFaixa = cb.sum(inicioFaixa, 9);

        // Concatenar: "20" + " - " + "29"
        Expression<String> faixaString = cb.concat(
                cb.concat(inicioFaixa.as(String.class), " - "),
                fimFaixa.as(String.class));

        return List.of(faixaString.alias("faixa_etaria_nome"));
    }

    @Override
    public List<Expression<?>> getGroupByExpressions(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb) {
        // Agrupamos pela data de nascimento para evitar ORA-00979 com expressões
        // complexas.
        // O transformador fará a agregação final pelas faixas etárias.
        return List.of(root.get("funId").get("dataNascimento"));
    }

    @Override
    public Predicate getFiltroPredicate(Root<TiposRelacionamentoEntity> root, CriteriaBuilder cb,
            List<String> valores) {
        // Assume que recebe UMA string no formato "min-max" (ex: "21-31")
        // Se receber múltiplas, aplicamos OR entre os intervalos

        Predicate predicateFinal = cb.disjunction(); // Inicializa com FALSE
        Expression<Integer> idade = getIdadeExpression(root, cb);

        for (String valor : valores) {
            try {
                String[] parts = valor.split("-");
                if (parts.length == 2) {
                    int min = Integer.parseInt(parts[0].trim());
                    int max = Integer.parseInt(parts[1].trim());
                    predicateFinal = cb.or(predicateFinal, cb.between(idade, min, max));
                }
            } catch (NumberFormatException e) {
                // Ignora formatos inválidos
            }
        }

        return predicateFinal;
    }
}
