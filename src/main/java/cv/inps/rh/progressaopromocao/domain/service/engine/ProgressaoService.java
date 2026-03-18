package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.shared.infrastructure.persistence.entity.VwRhProgressaoInputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@Service
@RequiredArgsConstructor
public class ProgressaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressaoService.class);

  private static final double MIN_MEDIA_AVALIACOES = 3.0;

  // todo anexar ordem servico registar para cada colaborador, colocar ficheiro
  // todo param situacao laboral flag evolui na carreira, deve calcular o tempo em que esteve nessa situacao e subtrair no tempo geral
  // todo tabela situacao situacao laboral, tem id situacao, ligado ku vinculo, funcionario

  private final SimulacaoService simulacaoService;

  public void simular(VwRhProgressaoInputEntity c) {

    LOGGER.debug("\n--------------------------------------------------PROGRESSAO-------------------------------------------------------------------------");
    LOGGER.debug("{}", c);

    var dataMinProgressao = c.getDataInicio().plusYears(c.getTempoMinProgressaoAnos());
    var atingiuTempoProgressao = dataMinProgressao.isBefore(LocalDate.now());
    if (!atingiuTempoProgressao) {
      LOGGER.debug("Nao atingiu tempo minimo para progressao");
      return;
    }

    var media = c.getMedia3anos();
    if (media != null && media >= MIN_MEDIA_AVALIACOES) {
      LOGGER.debug("Media {} >= 3.0, registrando simulacao", media);
      simulacaoService.registarProgressao(c, media);
      return;
    }

    LOGGER.debug("Media <{}> abaixo do limite", media);
  }
}
