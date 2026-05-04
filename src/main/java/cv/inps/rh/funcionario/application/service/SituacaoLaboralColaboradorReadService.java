package cv.inps.rh.funcionario.application.service;


import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.application.queries.GetSituacaoLaboralColaboradorQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SituacaoLaboralColaboradorReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public AtivarInativarColaboradorDTO execute(GetSituacaoLaboralColaboradorQuery query){

    var funcionarioPublicId = IdentificadorUnico.from(query.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var situacaoLaboral = new AtivarInativarColaboradorDTO();
    var motivoSitLabId = tiposRelacionamentoAtual.getSituacLaboralId().getMotivoSitLabId();
    if(motivoSitLabId != null) {
      situacaoLaboral.setSituacaoLaboralId(motivoSitLabId.getSituacaoId().getId());
      situacaoLaboral.setMotivoId(motivoSitLabId.getId());
    }
    situacaoLaboral.setObservacao(tiposRelacionamentoAtual.getSituacLaboralId().getObs());

    return situacaoLaboral;


  }
}
