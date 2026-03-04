package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SimulacaoService {

  private final EvolucaoCarreiraEntityRepository repository;

  private final ParamEscalaoEntityRepository escalaoRepository;

  public void registarSimulacao(CarreiraEntity carreira, MediaResultado media, String tipo) {

    var proximoEscalao = buscarProximoEscalao(carreira);
    if (proximoEscalao == null) return;

    // TODO 04/03/2026 21:45 put missing values for comented code

    var e = new EvolucaoCarreiraEntity();
    //e.setTiprel();
    e.setCarreiraIdDe(carreira);
    e.setEscalaoIdDe(carreira.getEscalaoId());
    e.setEscalaoIdPara(proximoEscalao);
    e.setDataReferente(LocalDate.now());
    e.setTipo(tipo); // "P" ou "M" todo put domain value here
    e.setEstado(Estado.A.name());
    e.setAvaliacaoMedia((long) media.media());
    //e.setObservacao();

    repository.save(e);
  }

  public ParamEscalaoEntity buscarProximoEscalao(CarreiraEntity carreira) {

    var escalaoAtual = carreira.getEscalaoId();
    if (escalaoAtual == null)
      return null;

    var proximoNivel = escalaoAtual.getNivelReferencia() + 1;

    return escalaoRepository.findByParamCarrIdIdAndNivelReferenciaAndEstado(
            carreira.getCarrPccsId().getId(),
            proximoNivel,
            Estado.A
        )
        .orElse(null);
  }
}
