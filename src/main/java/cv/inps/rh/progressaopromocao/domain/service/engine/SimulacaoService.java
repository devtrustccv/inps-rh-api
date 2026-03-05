package cv.inps.rh.progressaopromocao.domain.service.engine;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SimEvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SimEvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SimulacaoService {

  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final ParamEscalaoEntityRepository escalaoRepository;

  public void registarSimulacao(CarreiraEntity career, MediaResultado result, String type) {

    var proximoEscalao = buscarProximoEscalao(career);
    if (proximoEscalao == null)
      return;

    // TODO 04/03/2026 21:45 put missing values for commented code

    var e = new SimEvolucaoCarreiraEntity();
    //e.setFlgHistorico();
    //e.setTiprel();
    //e.setObservacao();
    e.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    e.setCarreiraIdDe(career);
    e.setEscalaoIdDe(career.getEscalaoId());
    e.setEscalaoIdPara(proximoEscalao);
    e.setDataReferente(LocalDate.now());
    e.setTipo(type); // "P" ou "M" todo put domain value here domain
    e.setAvaliacaoMedia(result.media().longValue());

    simEvolucaoCarreiraEntityRepository.save(e);
  }

  public ParamEscalaoEntity buscarProximoEscalao(CarreiraEntity career) {

    // TODO 05/03/2026 16:51 validate this

    var escalao = career.getEscalaoId().getEscalao();
    var nivel = career.getEscalaoId().getNivelReferencia();

    var nextLevel = career.getEscalaoId().getNivelReferencia() + 1;

    return escalaoRepository.findByParamCarrIdIdAndNivelReferenciaAndEstado(
            career.getCarrPccsId().getId(),
            nextLevel,
            Estado.A
        )
        .orElse(null);
  }
}
