package cv.inps.rh.transversal.application.service;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.transversal.application.commands.ObterDossierColaboradorCommand;
import cv.inps.rh.transversal.application.constants.DimensaoEnum;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidacaoDossierService {

  public void validar(ObterDossierColaboradorCommand query) {
    validarAgrupadores(query.getDossierrequest().getAgrupadores());
    validarFiltros(query.getDossierrequest().getFiltros());
    validarCruzamentoFiltrosAgrupadores(query.getDossierrequest().getFiltros(), query.getDossierrequest().getAgrupadores());
    //validarCombinacoesRedundantes(query.getAgrupadores(), query.getFiltros());
  }

  private void validarAgrupadores(List<String> agrupadores) {

    if (!CollectionsUtils.hasItems(agrupadores)) {
      throw IgrpResponseStatusException.badRequest("Agrupadores não pode ser vazio");
    }

    long distintos = agrupadores.stream().distinct().count();
    if (distintos != agrupadores.size()) {
      throw IgrpResponseStatusException.badRequest("Agrupadores não podem ser repetidos");
    }

    for (String agrupador : agrupadores) {
      if (!DimensaoEnum.exists(agrupador)) {
        throw IgrpResponseStatusException.badRequest(
            "Agrupador inválido: '" + agrupador + "'"
        );
      }
    }
  }


  // 2. Validações dos filtros
  private void validarFiltros(Map<String, List<String>> filtros) {

    // Filtros são opcionais
    if (filtros == null || filtros.isEmpty()) return;

    for (Map.Entry<String, List<String>> filtro : filtros.entrySet()) {

      // Chave tem que ser dimensão válida
      if (!DimensaoEnum.exists(filtro.getKey())) {
        throw IgrpResponseStatusException.badRequest(
            "Filtro inválido: '" + filtro.getKey() + "'"
        );
      }

      // Lista de valores não pode ser nula ou vazia
      if (filtro.getValue() == null || filtro.getValue().isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "Valores do filtro '" + filtro.getKey() + "' não podem ser vazios"
        );
      }

      // Cada valor não pode ser nulo ou vazio
      filtro.getValue().forEach(valor -> {
        if (valor == null || valor.isBlank()) {
          throw IgrpResponseStatusException.badRequest(
              "Valor do filtro '" + filtro.getKey() + "' não pode ser nulo ou vazio"
          );
        }
      });
    }
  }

  // 3. Cruzamento filtros e agrupadores
  private void validarCruzamentoFiltrosAgrupadores(
      Map<String, List<String>> filtros,
      List<String> agrupadores) {

    if (filtros == null || filtros.isEmpty()) return;

    filtros.keySet().forEach(filtro -> {
      if (agrupadores.contains(filtro)) {
        throw IgrpResponseStatusException.badRequest(
            "A dimensão '" + filtro + "' não pode ser filtro e agrupador ao mesmo tempo"
        );
      }
    });
  }
}
