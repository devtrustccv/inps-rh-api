package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;

import java.util.Map;

public interface OrdemServicoProvider {

  OrdemServico getTipo();

  Map<String, Object> buildVariables(String funcionarioId);

}
