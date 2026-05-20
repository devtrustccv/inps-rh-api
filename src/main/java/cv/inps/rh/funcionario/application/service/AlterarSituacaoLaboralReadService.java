package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.AlterarSituacaoLaboralRequest;
import cv.inps.rh.funcionario.application.queries.GetAlterarSituacaoLaboralQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlterarSituacaoLaboralReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public AlterarSituacaoLaboralRequest execute(GetAlterarSituacaoLaboralQuery query) {

    var funcionarioPublicId = IdentificadorUnico.from(query.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var dto = new AlterarSituacaoLaboralRequest();
    var sitLab = tiposRelacionamentoAtual.getSituacLaboralId();
    var motivoSitLabId = sitLab.getMotivoSitLabId();
    if (motivoSitLabId != null) {
      dto.setSituacaoLaboralId(motivoSitLabId.getSituacaoId().getId());
      dto.setMotivoId(motivoSitLabId.getId());
    }
    dto.setObservacao(sitLab.getObs());
    dto.setDataInicio(DateFormatter.localDateToString(sitLab.getDataInicio()));
    dto.setDataFim(DateFormatter.localDateToString(sitLab.getDataFim()));

    return dto;
  }
}
