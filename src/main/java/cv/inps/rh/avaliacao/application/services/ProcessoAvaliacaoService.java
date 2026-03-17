package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.ComissaoExecutivaDTO;
import cv.inps.rh.avaliacao.application.dto.ObservacaoGeralDTO;
import cv.inps.rh.avaliacao.application.dto.ParecerColaboradorDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProcessoAvaliacaoService {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_PARCIAL = "P";
  private static final String ESTADO_CONCLUIDO = "C";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaRepository;

  public ProcessoAvaliacaoService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ParamEscalaAvaliacaoEntityRepository escalaRepository
  ) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.objetivoDetRepository = objetivoDetRepository;
    this.escalaRepository = escalaRepository;
  }

  @Transactional
  public Map<String, ?> gravarAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);

    if (dto != null && dto.getObjectivos() != null) {
      var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
      dto.getObjectivos().forEach(o -> {
        if (o == null || o.getNumero() == null) return;
        objetivos.stream()
            .filter(e -> o.getNumero().equals(e.getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> e.setMeta(o.getMeta()));
      });
    }

    recalcularAvaliacaoSemestral(avaliacao);
    atualizarEstadoSemestre(avaliacao);

    avaliacaoRepository.save(avaliacao);
    return Map.of("id", avaliacao.getUuid());
  }

  @Transactional
  public Map<String, ?> gravarAutoAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);

    if (dto != null && dto.getObjectivos() != null) {
      var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
      dto.getObjectivos().forEach(o -> {
        if (o == null || o.getNumero() == null) return;
        objetivos.stream()
            .filter(e -> o.getNumero().equals(e.getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> e.setMeta(o.getMeta()));
      });
    }

    recalcularAvaliacaoSemestral(avaliacao);

    avaliacaoRepository.save(avaliacao);
    return Map.of("id", avaliacao.getUuid());
  }

  @Transactional
  public Map<String, ?> gravarObservacaoGeral(String uuid, ObservacaoGeralDTO dto) {
    var avaliacao = load(uuid);

    if (dto != null) {
      avaliacao.setObservacaoGeral(dto.getObservacaoGeralAvaliacao());
      avaliacao.setDescricaoPlano(dto.getDescPlanoDesenvolvimento());
      avaliacao.setDataInicioEntrevista(dto.getDataInicio());
      avaliacao.setHoraInicioEntrevista(dto.getHoraInicio());
      avaliacao.setHoraFimEntrevista(dto.getHoraFim());
    }

    avaliacaoRepository.save(avaliacao);
    return Map.of("id", avaliacao.getUuid());
  }

  @Transactional
  public Map<String, ?> gravarParecerColaborador(String uuid, ParecerColaboradorDTO dto) {
    var avaliacao = load(uuid);

    if (dto != null) {
      avaliacao.setParecerColaborador(dto.getParecer());
      avaliacao.setJustificacaoMotivo(dto.getJustificar());
    }

    avaliacaoRepository.save(avaliacao);
    return Map.of("id", avaliacao.getUuid());
  }

  @Transactional
  public Map<String, ?> gravarComissaoExecutiva(String uuid, ComissaoExecutivaDTO dto) {
    var avaliacao = load(uuid);
    if (dto != null) {
      avaliacao.setObsComissaoExec(dto.getObservacao());
    }
    avaliacaoRepository.save(avaliacao);
    return Map.of("id", avaliacao.getUuid());
  }

  private AvaliacaoEntity load(String uuid) {
    try {
      return avaliacaoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido: " + uuid);
    }
  }

  private void atualizarEstadoSemestre(AvaliacaoEntity avaliacao) {
    if (!StringUtils.hasText(avaliacao.getSemestre())) {
      avaliacao.setEstado(ESTADO_ATIVO);
      return;
    }

    if ("1".equals(avaliacao.getSemestre())) {
      avaliacao.setEstado(ESTADO_PARCIAL);
      return;
    }

    if ("2".equals(avaliacao.getSemestre())) {
      var funUuid = avaliacao.getFuncionario() != null ? avaliacao.getFuncionario().getUuid() : null;
      if (funUuid != null && avaliacaoRepository.existsByFuncionario_UuidAndAnoAndSemestre(funUuid, avaliacao.getAno(), "1")) {
        avaliacao.setEstado(ESTADO_CONCLUIDO);
      } else {
        avaliacao.setEstado(ESTADO_ATIVO);
      }
      return;
    }

    avaliacao.setEstado(ESTADO_ATIVO);
  }

  private void recalcularAvaliacaoSemestral(AvaliacaoEntity avaliacao) {
    var ano = avaliacao.getAno();
    if (ano == null) return;

    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(ano).orElse(null);
    if (det == null) return;

    var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());

    var resultadoObjetivos = objetivos.stream()
        .filter(o -> o.getEstado() == null || !o.getEstado().equals("E"))
        .map(o -> multiplyPercent(o.getAvaliacao() != null ? o.getAvaliacao() : o.getAutoAvaliacao(), o.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var avaliacaoObjetivo = multiplyPercent(resultadoObjetivos, det.getPonderacaoObjetivo());

    var avaliacaoFinal = avaliacaoObjetivo;

    avaliacao.setAvaliacaoObjectivo(scale2(avaliacaoObjetivo));
    avaliacao.setAvaliacaoCompetencia(null);
    avaliacao.setAvaliacaoAtitudePess(null);

    avaliacao.setAvaliacaoFinal(avaliacaoFinal != null ? avaliacaoFinal.doubleValue() : null);

    var qualitativa = resolveQualitativa(escalaRepository.findAll(), avaliacaoFinal);
    avaliacao.setAvaliacaoQualitativa(qualitativa);
  }

  private BigDecimal multiplyPercent(BigDecimal nota, BigDecimal ponderacao) {
    if (nota == null || ponderacao == null) return BigDecimal.ZERO;
    return nota.multiply(ponderacao).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
  }

  private BigDecimal scale2(BigDecimal v) {
    if (v == null) return null;
    return v.setScale(2, RoundingMode.HALF_UP);
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal valor) {
    if (valor == null || escala == null) return null;
    for (var e : escala) {
      if (e == null) continue;
      if (e.getEstado() != cv.inps.rh.shared.application.constants.Estado.A) continue;
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null) continue;
      if (valor.compareTo(e.getQuantitativaDe()) >= 0 && valor.compareTo(e.getQuantitativaAte()) <= 0) {
        return e.getQualitativa();
      }
    }
    return null;
  }
}
