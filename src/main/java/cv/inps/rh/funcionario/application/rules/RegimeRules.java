package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RegimeRules {


  public String getDiasSemanaAgrupados(RegimeTrabalhoEntity regimeTrabalhoEntity) {
    if (regimeTrabalhoEntity == null || regimeTrabalhoEntity.getModalidades() == null)
      return null;

    return regimeTrabalhoEntity.getModalidades().stream()
        .map(m -> m.getDiasSemana() == null ? "" : m.getDiasSemana().trim())
        .filter(d -> !d.isEmpty())
        .distinct()
        .collect(Collectors.joining(", "));
  }

  public String getModalidadesAgrupadas(RegimeTrabalhoEntity regimeTrabalhoEntity) {
    if (regimeTrabalhoEntity == null || regimeTrabalhoEntity.getModalidades() == null)
      return null;

    return regimeTrabalhoEntity.getModalidades().stream()
        .map(m -> m.getModalidade() == null ? "" : m.getModalidade().trim())
        .filter(d -> !d.isEmpty())
        .distinct()
        .collect(Collectors.joining(", "));
  }

  public Integer getTotalHoras(RegimeTrabalhoEntity regimeTrabalhoEntity) {
    if (regimeTrabalhoEntity == null || regimeTrabalhoEntity.getModalidades() == null)
      return 0;

    return regimeTrabalhoEntity.getModalidades().stream()
        .map(RegimeModalidadeEntity::getNumHoras)
        .filter(Objects::nonNull)
        .mapToInt(Integer::intValue)
        .sum();
  }

}
