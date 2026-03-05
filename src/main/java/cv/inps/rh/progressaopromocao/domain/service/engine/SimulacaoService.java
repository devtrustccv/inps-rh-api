package cv.inps.rh.progressaopromocao.domain.service.engine;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
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

  private static final int NIVEL_MAXIMO = 16;

  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final ParamEscalaoEntityRepository escalaoRepository;

  public void registarSimulacao(CarreiraEntity career, MediaResultado result, ProgessionPromotionType type) {

    var proximoEscalao = buscarProximoEscalao(career);

    var e = new SimEvolucaoCarreiraEntity();
    //e.setTiprel();
    e.setObservacao("TODO TODO TODO");
    e.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    e.setCarreiraIdDe(career);
    e.setEscalaoIdDe(career.getEscalaoId());
    e.setEscalaoIdPara(proximoEscalao);
    e.setDataReferente(LocalDate.now());
    e.setTipo(type.name());
    e.setAvaliacaoMedia(result.media().longValue());

    simEvolucaoCarreiraEntityRepository.save(e);
  }

  public ParamEscalaoEntity buscarProximoEscalao(CarreiraEntity career) {

    var escalaoId = career.getEscalaoId();
    var nivelAtual = escalaoId.getNivelReferencia();
    var escalaoAtual = escalaoId.getEscalao();

    var proximo = calcularProximoNivel(nivelAtual, escalaoAtual);

    return escalaoRepository
        .findByNivelReferenciaAndEscalaoAndEstado(
            proximo.nivelReferencia(),
            proximo.escalao(),
            Estado.A
        );
  }

  private record ProximoNivel(Integer nivelReferencia, String escalao) {
  }

  private static ProximoNivel calcularProximoNivel(Integer nivelAtual, String escalaoAtual) {

    if (nivelAtual == NIVEL_MAXIMO && escalaoAtual.equals("F")) {
      return null; // já está no último nível
    }

    return switch (escalaoAtual) {
      case "F" -> new ProximoNivel(nivelAtual, "E");
      case "E" -> new ProximoNivel(nivelAtual, "D");
      case "D" -> new ProximoNivel(nivelAtual, "C");
      case "C" -> new ProximoNivel(nivelAtual, "B");
      case "B" -> new ProximoNivel(nivelAtual, "A");
      case "A" -> new ProximoNivel(nivelAtual + 1, "F");
      default -> throw new IllegalArgumentException("Escalão inválido: " + escalaoAtual);
    };
  }
}
