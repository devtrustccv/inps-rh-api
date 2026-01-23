package cv.inps.rh.funcionario.application.service.helper;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoMovimentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TipoMovimentoHelper {

  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;

  public TipoMovimentoEntity getTipoMovimentoEntitySalario() {
    return tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("SALL", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento SALARIO nao encontrado."));
  }

  public TipoMovimentoEntity getTipoMovimentoEntityInps() {
    return tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("INPS", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento INPS nao encontrado."));
  }

  public TipoMovimentoEntity getTipoMovimentoEntityIur() {
    return tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("IUR", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento IUR nao encontrado."));
  }

  public TipoMovimentoEntity getTipoMovimentoEntityFaltaDesconto() {
    return tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("FALT", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento FALTA nao encontrado."));
  }

  public TipoMovimentoEntity getTipoMovimentoEntityHoraExtra() {
    return tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("HEXT", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento HORA EXTRA nao encontrado."));
  }

}
