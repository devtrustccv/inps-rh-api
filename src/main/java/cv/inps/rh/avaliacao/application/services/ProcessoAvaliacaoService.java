package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.ComissaoExecutivaDTO;
import cv.inps.rh.avaliacao.application.dto.ObservacaoGeralDTO;
import cv.inps.rh.avaliacao.application.dto.ParecerColaboradorDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import org.springframework.http.ResponseEntity;
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
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;
  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaRepository;

  public ProcessoAvaliacaoService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository,
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ParamEscalaAvaliacaoEntityRepository escalaRepository) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
    this.objetivoDetRepository = objetivoDetRepository;
    this.escalaRepository = escalaRepository;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> gravarAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);

    var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
    if (dto != null && dto.getObjectivos() != null && objetivos != null) {
      dto.getObjectivos().forEach(o -> {
        if (o == null || o.getNumero() == null)
          return;
        objetivos.stream()
            .filter(e -> e != null && o.getNumero().equals(e.getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> {
              e.setRealizado(o.getRealizado());
              e.setAvaliacao(o.getAvaliacao() != null ? BigDecimal.valueOf(o.getAvaliacao()) : null);
            });
      });
      objectivoRepository.saveAll(objetivos);
    }

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    if (dto != null && competencias != null) {
      if (dto.getCompetenciasComportamentais() != null) {
        dto.getCompetenciasComportamentais().forEach(c -> {
          if (c == null || c.getNumeroOrdem() == null)
            return;
          competencias.stream()
              .filter(e -> e != null
                  && "COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(e.getComponente())
                  && c.getNumeroOrdem().equals(e.getNumeroOrdem()))
              .findFirst()
              .ifPresent(e -> e
                  .setAvaliacaoProcessual(c.getAvaliacao() != null ? BigDecimal.valueOf(c.getAvaliacao()) : null));
        });
      }
      if (dto.getCompetenciasTecnicas() != null) {
        dto.getCompetenciasTecnicas().forEach(c -> {
          if (c == null || c.getNumeroOrdem() == null)
            return;
          competencias.stream()
              .filter(e -> e != null
                  && "COMPETENCIA_TECNICA".equalsIgnoreCase(e.getComponente())
                  && c.getNumeroOrdem().equals(e.getNumeroOrdem()))
              .findFirst()
              .ifPresent(e -> e
                  .setAvaliacaoProcessual(c.getAvaliacao() != null ? BigDecimal.valueOf(c.getAvaliacao()) : null));
        });
      }
      competenciaRepository.saveAll(competencias);
    }

    var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    if (dto != null && dto.getAtitudesPessoais() != null && atitudes != null) {
      dto.getAtitudesPessoais().forEach(a -> {
        if (a == null || a.getNumeroOrdem() == null)
          return;
        atitudes.stream()
            .filter(e -> e != null
                && e.getParamObjetivo() != null
                && a.getNumeroOrdem().equals(e.getParamObjetivo().getNumeroOrdem()))
            .findFirst()
            .ifPresent(
                e -> e.setAvaliacaoProcessual(a.getAvaliacao() != null ? BigDecimal.valueOf(a.getAvaliacao()) : null));
      });
      atitudeRepository.saveAll(atitudes);
    }

    recalcularAvaliacaoSemestral(avaliacao);
    atualizarEstadoSemestre(avaliacao);

    avaliacaoRepository.save(avaliacao);
    return ResponseEntity.ok(Map.of("id", avaliacao.getUuid()));
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> gravarAutoAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);

    var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
    if (dto != null && dto.getObjectivos() != null && objetivos != null) {
      dto.getObjectivos().forEach(o -> {
        if (o == null || o.getNumero() == null)
          return;
        objetivos.stream()
            .filter(e -> e != null && o.getNumero().equals(e.getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> {
              var realizado = o.getRealizado() != null ? o.getRealizado() : null;
              var avaliacaoNota = o.getAvaliacao() != null ? o.getAvaliacao() : null;
              e.setAutoRealizado(realizado);
              e.setAutoAvaliacao(avaliacaoNota != null ? BigDecimal.valueOf(avaliacaoNota) : null);
            });
      });
      objectivoRepository.saveAll(objetivos);
    }

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    if (dto != null && competencias != null) {
      if (dto.getCompetenciasComportamentais() != null) {
        dto.getCompetenciasComportamentais().forEach(c -> {
          if (c == null || c.getNumeroOrdem() == null)
            return;
          competencias.stream()
              .filter(e -> e != null
                  && "COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(e.getComponente())
                  && c.getNumeroOrdem().equals(e.getNumeroOrdem()))
              .findFirst()
              .ifPresent(e -> {
                var nota = c.getAvaliacao() != null ? c.getAvaliacao() : null;
                e.setAutoAvaliacao(nota != null ? BigDecimal.valueOf(nota) : null);
              });
        });
      }
      if (dto.getCompetenciasTecnicas() != null) {
        dto.getCompetenciasTecnicas().forEach(c -> {
          if (c == null || c.getNumeroOrdem() == null)
            return;
          competencias.stream()
              .filter(e -> e != null
                  && "COMPETENCIA_TECNICA".equalsIgnoreCase(e.getComponente())
                  && c.getNumeroOrdem().equals(e.getNumeroOrdem()))
              .findFirst()
              .ifPresent(e -> {
                var nota = c.getAvaliacao() != null ? c.getAvaliacao() : null;
                e.setAutoAvaliacao(nota != null ? BigDecimal.valueOf(nota) : null);
              });
        });
      }
      competenciaRepository.saveAll(competencias);
    }

    var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    if (dto != null && dto.getAtitudesPessoais() != null && atitudes != null) {
      dto.getAtitudesPessoais().forEach(a -> {
        if (a == null || a.getNumeroOrdem() == null)
          return;
        atitudes.stream()
            .filter(e -> e != null
                && e.getParamObjetivo() != null
                && a.getNumeroOrdem().equals(e.getParamObjetivo().getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> {
              var nota = a.getAvaliacao() != null ? a.getAvaliacao() : null;
              e.setAutoAvaliacao(nota != null ? BigDecimal.valueOf(nota) : null);
            });
      });
      atitudeRepository.saveAll(atitudes);
    }
    return ResponseEntity.ok(Map.of("id", avaliacao.getUuid()));
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
      if (funUuid != null
          && avaliacaoRepository.existsByFuncionario_UuidAndAnoAndSemestre(funUuid, avaliacao.getAno(), "1")) {
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
    if (ano == null)
      return;

    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(ano).orElse(null);
    if (det == null)
      return;

    var objetivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());

    var resultadoObjetivos = objetivos.stream()
        .filter(o -> o.getEstado() == null || !o.getEstado().equals("E"))
        .map(o -> multiplyPercent(o.getAvaliacao(), o.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var avaliacaoObjetivo = multiplyPercent(resultadoObjetivos, det.getPonderacaoObjetivo());

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    var resultadoComport = (competencias == null
        ? List.<cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity>of()
        : competencias)
        .stream()
        .filter(c -> c != null && (c.getEstado() == null || !"E".equalsIgnoreCase(c.getEstado())))
        .filter(c -> "COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(c.getComponente()))
        .map(c -> multiplyPercent(c.getAvaliacaoProcessual(), c.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var resultadoTec = (competencias == null
        ? List.<cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity>of()
        : competencias)
        .stream()
        .filter(c -> c != null && (c.getEstado() == null || !"E".equalsIgnoreCase(c.getEstado())))
        .filter(c -> "COMPETENCIA_TECNICA".equalsIgnoreCase(c.getComponente()))
        .map(c -> multiplyPercent(c.getAvaliacaoProcessual(), c.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var resultadoCompetencias = resultadoComport.add(resultadoTec);

    var avaliacaoCompetencia = multiplyPercent(resultadoCompetencias, det.getPonderacaoCompetencia());

    var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    var resultadoAtitudes = (atitudes == null
        ? List.<cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity>of()
        : atitudes)
        .stream()
        .filter(a -> a != null && (a.getEstado() == null || !"E".equalsIgnoreCase(a.getEstado())))
        .map(a -> multiplyPercent(a.getAvaliacaoProcessual(), a.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    var avaliacaoAtitude = multiplyPercent(resultadoAtitudes, det.getPonderacaoAtitudePess());

    var avaliacaoFinal = avaliacaoObjetivo
        .add(avaliacaoCompetencia)
        .add(avaliacaoAtitude);

    avaliacao.setAvaliacaoObjectivo(scale2(avaliacaoObjetivo));
    avaliacao.setAvaliacaoCompetencia(scale2(avaliacaoCompetencia));
    avaliacao.setAvaliacaoAtitudePess(scale2(avaliacaoAtitude));

    avaliacao.setAvaliacaoFinal(avaliacaoFinal != null ? avaliacaoFinal.doubleValue() : null);

    var qualitativa = resolveQualitativa(escalaRepository.findAll(), avaliacaoFinal);
    avaliacao.setAvaliacaoQualitativa(qualitativa);
  }

  private BigDecimal multiplyPercent(BigDecimal nota, BigDecimal ponderacao) {
    if (nota == null || ponderacao == null)
      return BigDecimal.ZERO;
    return nota.multiply(ponderacao).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
  }

  private BigDecimal scale2(BigDecimal v) {
    if (v == null)
      return null;
    return v.setScale(2, RoundingMode.HALF_UP);
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal valor) {
    if (valor == null || escala == null)
      return null;
    for (var e : escala) {
      if (e == null)
        continue;
      if (e.getEstado() != cv.inps.rh.shared.application.constants.Estado.A)
        continue;
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null)
        continue;
      if (valor.compareTo(e.getQuantitativaDe()) >= 0 && valor.compareTo(e.getQuantitativaAte()) <= 0) {
        return e.getQualitativa();
      }
    }
    return null;
  }
}
