package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Controlo da etapa de um processo de missão. Substitui a guarda que operava sobre a etapa única da
 * missão, com as mesmas regras:
 * <ul>
 *   <li>NEXT só é aceite se o processo já atingiu a etapa do ecrã;</li>
 *   <li>SAVE fora de ordem grava (só fica em log) — bloquear faria perder o que o utilizador preencheu;</li>
 *   <li>a etapa só avança, excepto por devolução explícita (parecer desfavorável, D3).</li>
 * </ul>
 */
@Component
public class ProcessoEtapaGuard {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoEtapaGuard.class);

  private static final String ESTADO_ATIVO = "A";

  /** Exige que o processo percorra a etapa do ecrã, esteja activo e, ao avançar, já a tenha atingido. */
  public void exigirEtapa(MissaoProcessoEntity processo, EtapaProcesso etapaEcra, boolean avancando) {
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    if (!tipo.percorre(etapaEcra)) {
      throw IgrpResponseStatusException.badRequest(
          "O processo " + tipo.name() + " não passa pela etapa " + etapaEcra.name());
    }
    if (!ESTADO_ATIVO.equals(processo.getEstado())) {
      throw IgrpResponseStatusException.badRequest(
          "O processo " + tipo.name() + " está inactivo nesta missão");
    }

    var atual = EtapaProcesso.fromCode(processo.getEtapa());
    if (atual == null || atual.ordinal() >= etapaEcra.ordinal()) {
      return;
    }
    if (avancando) {
      throw IgrpResponseStatusException.badRequest(
          "O processo " + tipo.name() + " encontra-se na etapa '" + atual.name()
              + "' — esta operação exige que já tenha atingido a etapa '" + etapaEcra.name() + "'");
    }
    LOGGER.warn("Gravação fora de ordem no processo {}: etapa atual '{}', etapa do ecrã '{}'",
        processo.getUuid(), atual, etapaEcra);
  }

  /**
   * Avança para a etapa seguinte a {@code etapaConcluida} no percurso do tipo — nunca retrocede.
   * Devolve a etapa em que o processo ficou.
   */
  public EtapaProcesso avancarApos(MissaoProcessoEntity processo, EtapaProcesso etapaConcluida) {
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    var alvo = tipo.seguinte(etapaConcluida);
    if (alvo == null) {
      alvo = etapaConcluida;
    }
    var atual = EtapaProcesso.fromCode(processo.getEtapa());
    if (atual == null || alvo.ordinal() > atual.ordinal()) {
      processo.setEtapa(alvo.name());
      return alvo;
    }
    return atual;
  }

  /** Devolve o processo a uma etapa anterior — só por decisão explícita (parecer desfavorável). */
  public void devolver(MissaoProcessoEntity processo, EtapaProcesso etapa) {
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    if (!tipo.percorre(etapa)) {
      throw new IllegalStateException("O processo " + tipo.name() + " não passa pela etapa " + etapa.name());
    }
    processo.setEtapa(etapa.name());
  }
}
