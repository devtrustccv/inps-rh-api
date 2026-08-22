package cv.inps.rh.funcionario.application.service.processodisciplinar;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o PROCESSO DISCIPLINAR. Validação INSERT,
 * referenciaId = process.id (casa com a instância auditada).
 */
@Component
public class ProcessoDisciplinarValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.PROCESSO_DISCIPLINAR.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "ProcessoDisciplinarEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("numProceso", "entidade", "tpProcesso", "penaDiscp",
        "dateInicPd", "dateFimPd", "dateInicPena", "dateFimPena", "dataOrdemServ", "numOrdemServ");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "numProceso", "Nº processo",
        "entidade", "Entidade",
        "tpProcesso", "Tipo de processo",
        "penaDiscp", "Pena disciplinar",
        "dateInicPd", "Data início processo",
        "dateFimPd", "Data fim processo",
        "dateInicPena", "Data início pena",
        "dateFimPena", "Data fim pena",
        "dataOrdemServ", "Data ordem de serviço",
        "numOrdemServ", "Nº ordem de serviço");
  }
}
