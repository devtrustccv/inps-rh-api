package cv.inps.rh.progressaopromocao.domain.service.engine;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SimEvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VwRhProgressaoInputEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SimEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SimulacaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimulacaoService.class);

  private static final int NIVEL_MAXIMO = 16;

  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final ParamEscalaoEntityRepository escalaoRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public void registarProgressao(VwRhProgressaoInputEntity career, Long media) {

    var car = carreiraEntityRepository.getReferenceById(career.getCarreiraId());

    var proximoEscalao = getProximoEscalao(car, ProgessionPromotionType.PROGRESSAO);
    if (!proximoEscalao.values().stream().findFirst().orElseThrow().allowedToProgressPromote) {
      LOGGER.debug("Carreira ja se encontra no ultimo nivel para progressao");
      return;
    }

    var paramEscalao = proximoEscalao.keySet().stream().findFirst().orElseThrow();

    saveEvolution(career, media, car, paramEscalao, ProgessionPromotionType.PROGRESSAO);
  }

  private void saveEvolution(VwRhProgressaoInputEntity career, Long media, CarreiraEntity car, ParamEscalaoEntity proximoEscalao, ProgessionPromotionType type) {
    var e = new SimEvolucaoCarreiraEntity();
    e.setTiprel(tiposRelacionamentoEntityRepository.getReferenceById(career.getRelacionamentoId()));
    e.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    e.setCarreiraIdDe(car);
    e.setEscalaoIdDe(car.getEscalaoId());
    e.setEscalaoIdPara(proximoEscalao);
    e.setDataReferente(LocalDate.now());
    e.setTipo(type.name());
    e.setAvaliacaoMedia(media);

    simEvolucaoCarreiraEntityRepository.save(e);
  }

  public void registarPromocao(VwRhProgressaoInputEntity career, Long media) {

    var car = carreiraEntityRepository.getReferenceById(career.getCarreiraId());

    var proximoEscalao = getProximoEscalao(car, ProgessionPromotionType.PROMOCAO);
    if (!proximoEscalao.values().stream().findFirst().orElseThrow().allowedToProgressPromote) {
      LOGGER.debug("Carreira ja se encontra no ultimo nivel para promocao");
      return;
    }

    var paramEscalao = proximoEscalao.keySet().stream().findFirst().orElseThrow();

    saveEvolution(career, media, car, paramEscalao, ProgessionPromotionType.PROMOCAO);
  }

  private Map<ParamEscalaoEntity, ProximoNivel> getProximoEscalao(CarreiraEntity career, ProgessionPromotionType type) {

    var escalaoId = career.getEscalaoId();
    var nivelAtual = Objects.requireNonNull(escalaoId.getNivelReferencia());
    var escalaoAtual = escalaoId.getEscalao();
    var proximo = type.equals(ProgessionPromotionType.PROMOCAO) ?
        calcularProximoNivelPromocao(nivelAtual) :
        calcularProximoNivelProgressao(nivelAtual, escalaoAtual);
    LOGGER.debug("Calculando proximo escalao para nivel {} e escalao {}", nivelAtual, escalaoAtual);
    LOGGER.debug("Proximo escalao: nivel {} e escalao {}", proximo.nivelReferencia(), proximo.escalao());

    var escalo = escalaoRepository.findByNivelReferenciaAndEscalaoAndEstado(
        proximo.nivelReferencia(),
        proximo.escalao(),
        Estado.A
    ).orElseThrow(() -> IgrpResponseStatusException.notFound(
        "Nenhum escalao encontrado para nivel: %s e escalao %s".formatted(proximo.nivelReferencia(), proximo.escalao()))
    );

    return Map.of(escalo, proximo);
  }

  private ProximoNivel calcularProximoNivelPromocao(Integer nivelAtual) {

    if (nivelAtual == NIVEL_MAXIMO)
      return new ProximoNivel(nivelAtual, "F", false);

    int proximoNivel = nivelAtual + 1;

    return new ProximoNivel(proximoNivel, "F", true);
  }

  private ProximoNivel calcularProximoNivelProgressao(Integer nivelAtual, String escalaoAtual) {

    if (nivelAtual == NIVEL_MAXIMO && escalaoAtual.equals("F"))
      return new ProximoNivel(nivelAtual, escalaoAtual, false);

    return switch (escalaoAtual) {
      case "F" -> new ProximoNivel(nivelAtual, "E", true);
      case "E" -> new ProximoNivel(nivelAtual, "D", true);
      case "D" -> new ProximoNivel(nivelAtual, "C", true);
      case "C" -> new ProximoNivel(nivelAtual, "B", true);
      case "B" -> new ProximoNivel(nivelAtual, "A", true);
      case "A" -> new ProximoNivel(nivelAtual + 1, "F", true);
      default -> throw new IllegalArgumentException("Escalão inválido: " + escalaoAtual);
    };
  }

  public record ProximoNivel(
      Integer nivelReferencia,
      String escalao,
      boolean allowedToProgressPromote
  ) {
  }
}
