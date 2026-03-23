package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.constants.ProgessionPromotionType;
import cv.inps.rh.shared.infrastructure.persistence.entity.VwRhProgressaoInputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PromocaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PromocaoService.class);

  private static final double MIN_MEDIA_AVALIACOES = 4.5;
  private static final String DIRECTOR = "DIRECTOR";
  private static final String DIRECTOR_BASE = "DIRECTOR_BASE";
  private final SimulacaoService simulacaoService;

  public void simular(VwRhProgressaoInputEntity c) {

    // TODO 05/03/2026 17:41 colaborador em licença sem vencimento não deve progredir
    // TODO 05/03/2026 17:41 O colaborador não deve estar em situação laboral em que não evolui na carreira no período de progressão; deve iniciar a partir da situação laboral atual

    LOGGER.debug("-----------------------------------------------------PROMOCAO--------------------------------------------------------------------");
    LOGGER.debug("{}", c);

    if (c.getTipoCarreira().equals(DIRECTOR) || c.getTipoCarreira().equals(DIRECTOR_BASE)) {
      LOGGER.debug("Tipo de carreira não permitido para promoção <{}>", c.getTipoCarreira());
      return;
    }

    // Verifica se já existe evolução ou tempo mínimo de progressão
    if (c.getEvolucaoAtual() == null || c.getEvolucaoAtual().equals(ProgessionPromotionType.PROMOCAO.name())) {
      LOGGER.debug("Sem progressao ou evolucao atual is PROMOCAO <{}>", c.getEvolucaoAtual());
      return;
    }

    var media = c.getMedia2anos();
    if (media != null && media >= MIN_MEDIA_AVALIACOES) {
      LOGGER.debug("Media {} >= 4.5, registrando simulacao", media);
      simulacaoService.registarProgressao(c, media);
      return;
    } else
      LOGGER.debug("Media <{}> abaixo do limite", media);

    var dataProgressao = c.getDataInicio().plusYears(3);
    var atingiuTempoProgressao = dataProgressao.isBefore(LocalDate.now());
    if (atingiuTempoProgressao) {
      LOGGER.debug("Nao atingiu tempo minimo para promocao");
      return;
    }

    if (c.getAptoPorFaltas() == 0) {
      LOGGER.debug("Colaborador sem aptidao por faltas: Faltas ano atual <{}>, Faltas ano anterior <{}>", c.getFaltasAnoAtual(), c.getFaltasAnoAnterior());
      return;
    }

    if (c.getAptoPorProcessoDisciplinar() == 0) {
      LOGGER.debug("Colaborador sem aptidao por processo disciplinar: Processos ano atual <{}>, Processos ano anterior <{}>", c.getProcessoAnoAtual(), c.getProcessoAnoAnterior());
      return;
    }

    simulacaoService.registarPromocao(c, media);
  }
}
