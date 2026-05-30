package cv.inps.rh.avaliacao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.avaliacao.application.commands.DefinicaoObjetivoCommand;
import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaDefinicaoObjetivoDTO;
import cv.inps.rh.avaliacao.application.queries.GetListaAvaliacaoQuery;
import cv.inps.rh.avaliacao.application.queries.GetListaDefinicaoObjectivosQuery;
import cv.inps.rh.avaliacao.infrastructure.mappers.AvaliacaoListagemMapper;
import cv.inps.rh.avaliacao.infrastructure.mappers.AvaliacaoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

  private static final String ESTADO_ATIVO = "A";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final InstituicaoEntityRepository instituicaoRepository;
  private final SecaoEntityRepository secaoRepository;
  private final ParamCargoEntityRepository cargoRepository;
  private final ParamCarreiraEntityRepository carreiraRepository;
  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ParamManualFuncaoEntityRepository manualFuncaoRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaAvaliacaoRepository;
  private final AvaliacaoMapper avaliacaoMapper;
  private final AvaliacaoListagemMapper avaliacaoListagemMapper;

  public AvaliacaoService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository,
      FuncionarioEntityRepository funcionarioRepository,
      InstituicaoEntityRepository instituicaoRepository,
      SecaoEntityRepository secaoRepository,
      ParamCargoEntityRepository cargoRepository,
      ParamCarreiraEntityRepository carreiraRepository,
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ParamManualFuncaoEntityRepository manualFuncaoRepository,
      ParamEscalaAvaliacaoEntityRepository escalaAvaliacaoRepository,
      AvaliacaoMapper avaliacaoMapper,
      AvaliacaoListagemMapper avaliacaoListagemMapper) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
    this.funcionarioRepository = funcionarioRepository;
    this.instituicaoRepository = instituicaoRepository;
    this.secaoRepository = secaoRepository;
    this.cargoRepository = cargoRepository;
    this.carreiraRepository = carreiraRepository;
    this.objetivoDetRepository = objetivoDetRepository;
    this.manualFuncaoRepository = manualFuncaoRepository;
    this.escalaAvaliacaoRepository = escalaAvaliacaoRepository;
    this.avaliacaoMapper = avaliacaoMapper;
    this.avaliacaoListagemMapper = avaliacaoListagemMapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> definicaoObjetivos(DefinicaoObjetivoCommand command) {

    var dto = command.getDefinicaoobjectivo();

    if (!StringUtils.hasText(dto.getSemestre()) || (!"1".equals(dto.getSemestre()) && !"2".equals(dto.getSemestre()))) {
      throw IgrpResponseStatusException.badRequest("semestre deve ser '1' ou '2'");
    }

    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(dto.getAno())
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "ParamObjetivoDetEntity not found for ano: " + dto.getAno()));

    var instit = instituicaoRepository.findByIdOrThrow(dto.getInstitId());

    var secao = dto.getSeccaoId() != null
        ? secaoRepository.findByIdOrThrow(dto.getSeccaoId())
        : null;

    var cargo = dto.getCargoId() != null
        ? cargoRepository.findById(dto.getCargoId())
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "ParamCargoEntity not found for id: " + dto.getCargoId()))
        : null;

    var carreira = dto.getCarrPccsId() != null
        ? carreiraRepository.findByIdOrThrow(dto.getCarrPccsId())
        : null;

    var created = new ArrayList<String>(dto.getFunUuids().size());

    var mapParamObjectives = det.getObjetivos().stream()
        .collect(Collectors.toMap(ParamObjetivoEntity::getId, Function.identity()));

    for (var funId : dto.getFunUuids()) {

      if (avaliacaoRepository.existsByFuncionario_UuidAndAnoAndSemestre(funId, dto.getAno(), dto.getSemestre())) {
        continue;
      }

      var funcionario = funcionarioRepository.findByUuidOrThrow(funId);

      var avaliacao = new AvaliacaoEntity();
      avaliacao.setUuid(UuidCreator.getTimeOrderedEpoch());
      avaliacao.setFuncionario(funcionario);
      avaliacao.setAno(dto.getAno());
      avaliacao.setSemestre(dto.getSemestre());
      avaliacao.setInstitId(instit);
      avaliacao.setSeccaoId(secao);
      avaliacao.setCargo(cargo);
      avaliacao.setCarreira(carreira);
      avaliacao.setEstado(ESTADO_ATIVO);
      avaliacao.setPesoComportamentais(det.getPesoComportamentais());
      avaliacao.setPesoTecnica(det.getPesoTecnica());

      avaliacaoRepository.save(avaliacao);

      criarLinhasAvaliacao(
          avaliacao,
          det.getObjetivos(),
          mapParamObjectives, dto, det
      );

      created.add(avaliacao.getUuid().toString());
    }

    return ResponseEntity.ok(Map.of(
        "ids", created));
  }

  @Transactional(readOnly = true)
  public WrapperListaDefinicaoObjetivoDTO getListaDefinicaoObjectivos(GetListaDefinicaoObjectivosQuery query) {

    var pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

    Specification<AvaliacaoEntity> spec = (root, cq, cb) -> {
      if (cq.getResultType() != Long.class) {
        root.fetch("funcionario", JoinType.LEFT);
        root.fetch("institId", JoinType.LEFT);
        root.fetch("seccaoId", JoinType.LEFT);
        root.fetch("cargo", JoinType.LEFT);
        root.fetch("carreira", JoinType.LEFT);
      }

      List<Predicate> predicates = new ArrayList<>();

      if (query.getAno() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAno()));
      }
      if (StringUtils.hasText(query.getSemestre())) {
        predicates.add(cb.equal(root.get("semestre"), query.getSemestre()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));
      }
      if (query.getInstitId() != null) {
        predicates.add(cb.equal(root.get("institId").get("id"), query.getInstitId()));
      }
      if (query.getCargoId() != null) {
        predicates.add(cb.equal(root.get("cargo").get("id"), query.getCargoId()));
      }
      if (query.getCarreiraId() != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), query.getCarreiraId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = avaliacaoRepository.findAll(spec, pageable);

    var response = new WrapperListaDefinicaoObjetivoDTO();
    cv.inps.rh.shared.util.PageMapper.fillPagination(page, response);
    response.setContent(page.getContent().stream().map(avaliacaoMapper::toResumo).toList());
    return response;
  }

  @Transactional(readOnly = true)
  public WrapperListaAvaliacaoDTO getListaAvaliacao(GetListaAvaliacaoQuery query) {

    var pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    var pageable = PageRequest.of(pageNumber, pageSize);

    Specification<AvaliacaoEntity> spec = (root, cq, cb) -> {
      if (cq.getResultType() != Long.class) {
        root.fetch("funcionario", JoinType.LEFT);
        root.fetch("institId", JoinType.LEFT);
        root.fetch("seccaoId", JoinType.LEFT);
        root.fetch("cargo", JoinType.LEFT);
        root.fetch("carreira", JoinType.LEFT);
      }

      List<Predicate> predicates = new ArrayList<>();

      if (query.getAno() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAno()));
      }
      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("institId").get("id"), query.getDirecao()));
      }
      if (query.getCargo() != null) {
        predicates.add(cb.equal(root.get("cargo").get("id"), query.getCargo()));
      }
      if (query.getSeccaoId() != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), query.getSeccaoId()));
      }
      if (query.getCarreiraId() != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), query.getCarreiraId()));
      }
      if (StringUtils.hasText(query.getSemestre())) {
        predicates.add(cb.equal(root.get("semestre"), query.getSemestre()));
      }
      if (StringUtils.hasText(query.getColaborador())) {
        var raw = query.getColaborador().trim();
        try {
          predicates.add(cb.equal(root.get("funcionario").get("uuid"), UUID.fromString(raw)));
        } catch (Exception ignored) {
          try {
            predicates.add(cb.equal(root.get("funcionario").get("id"), Long.valueOf(raw)));
          } catch (Exception ignored2) {
            predicates.add(cb.like(cb.lower(root.get("funcionario").get("nome")), "%" + raw.toLowerCase() + "%"));
          }
        }
      }

      predicates.add(cb.notEqual(root.get("estado"), "E"));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var rows = avaliacaoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "ano").and(Sort.by("id")));

    var grouped = new LinkedHashMap<GroupKey, List<AvaliacaoEntity>>();

    for (var e : rows) {
      var key = new GroupKey(
          e.getAno(),
          e.getInstitId() != null ? e.getInstitId().getId() : null,
          e.getCargo() != null ? e.getCargo().getId() : null,
          e.getFuncionario() != null ? e.getFuncionario().getUuid() : null);
      grouped.computeIfAbsent(key, _ -> new ArrayList<>()).add(e);
    }

    var escala = escalaAvaliacaoRepository.findAll();

    var contentAll = grouped.values().stream().map(list -> {
      var base = list.getFirst();
      BigDecimal s1 = null;
      BigDecimal s2 = null;
      for (var a : list) {
        if ("1".equals(a.getSemestre()) && a.getAvaliacaoFinal() != null) {
          s1 = BigDecimal.valueOf(a.getAvaliacaoFinal());
        } else if ("2".equals(a.getSemestre()) && a.getAvaliacaoFinal() != null) {
          s2 = BigDecimal.valueOf(a.getAvaliacaoFinal());
        }
      }

      var estadoGrupo = resolveEstadoGrupo(list);

      var notaFinal = (s1 != null ? s1 : BigDecimal.ZERO).add(s2 != null ? s2 : BigDecimal.ZERO);
      if (s1 == null && s2 == null) {
        notaFinal = null;
      }

      var qualitativa = notaFinal != null ? resolveQualitativa(escala, notaFinal) : null;

      return avaliacaoListagemMapper.toListagem(base, estadoGrupo, s1, s2, notaFinal, qualitativa);
    }).toList();

    var start = Math.min(pageNumber * pageSize, contentAll.size());
    var end = Math.min(start + pageSize, contentAll.size());
    var page = new PageImpl<>(contentAll.subList(start, end), pageable, contentAll.size());

    var response = new WrapperListaAvaliacaoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  private void criarLinhasAvaliacao(
      AvaliacaoEntity avaliacao,
      List<ParamObjetivoEntity> params,
      Map<Long, ParamObjetivoEntity> mapParamObjectives,
      DefinicaoObjectivoDTO dto,
      ParamObjetivoDetEntity det) {
    if (params == null)
      return;

    //var manualDescricao = resolverDescricaoManual(institId, seccaoId, cargoId, carrPccsId);

    dto.getObjectivos().forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoObjectivoEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacaoObj(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      e.setObjectivos("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) ? obj.getObjectivo() : p.getDescricao());
      e.setKpi("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) ? obj.getKpi() : p.getKpi());
      e.setMeta(obj.getMeta());
      e.setPonderacao(p.getPonderacao());
      objectivoRepository.save(e);
    });

    dto.getCompetenciasComportamentais().forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoCompetenciaEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      e.setDescricao(obj.getCompetencia());
      e.setPonderacao(p.getPonderacao());
      e.setComponente(p.getComponente());
      e.setPeso(det.getPesoComportamentais());
      competenciaRepository.save(e);
    });

    dto.getCompetenciasTecnicas().forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoCompetenciaEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      e.setDescricao(obj.getCompetencia());
      e.setPonderacao(p.getPonderacao());
      e.setComponente(p.getComponente());
      e.setPeso(det.getPesoTecnica());
      competenciaRepository.save(e);
    });

    dto.getAtitudesPessoais().forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoAtitudePessoalEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      e.setDescricao(p.getDescricao());
      e.setPonderacao(p.getPonderacao());
      atitudeRepository.save(e);
    });


    /*for (var p : params) {
      if (p == null || !StringUtils.hasText(p.getComponente())) {
        continue;
      }
      if (!aplicaAoContexto(p, institId, seccaoId, cargoId, carrPccsId)) {
        continue;
      }

      if ("OBJETIVO".equalsIgnoreCase(p.getComponente())) {
        if ("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) && manualDescricao == null) {
          throw IgrpResponseStatusException.badRequest("Manual de funções não encontrado para abrangencia INDIVIDUAL");
        }
        var e = new AvaliacaoObjectivoEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacaoObj(avaliacao);
        e.setParamObjetivo(p);
        e.setNumeroOrdem(p.getNumeroOrdem());
        e.setAbrangencia(p.getAbrangencia());
        e.setObjectivos("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) && manualDescricao != null ? manualDescricao
            : p.getDescricao());
        e.setKpi(p.getKpi());
        e.setPonderacao(p.getPonderacao());
        objectivoRepository.save(e);
      } else if ("COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(p.getComponente())
          || "COMPETENCIA_TECNICA".equalsIgnoreCase(p.getComponente())) {
        if (manualDescricao == null) {
          throw IgrpResponseStatusException.badRequest("Manual de funções não encontrado para competências");
        }
        var e = new AvaliacaoCompetenciaEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacao(avaliacao);
        e.setParamObjetivo(p);
        e.setNumeroOrdem(p.getNumeroOrdem());
        e.setAbrangencia(p.getAbrangencia());
        e.setDescricao(manualDescricao);
        e.setPonderacao(p.getPonderacao());
        e.setComponente(p.getComponente());
        competenciaRepository.save(e);
      } else if ("ATITUDE_PESSOAL".equalsIgnoreCase(p.getComponente())) {
        var e = new AvaliacaoAtitudePessoalEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacao(avaliacao);
        e.setParamObjetivo(p);
        e.setAbrangencia(p.getAbrangencia());
        e.setPonderacao(p.getPonderacao());
        atitudeRepository.save(e);
      }
    }*/
  }

  private String resolverDescricaoManual(Long institId, Long seccaoId, Long cargoId, Long carrPccsId) {
    if (institId == null || cargoId == null) {
      return null;
    }

    Specification<cv.inps.rh.shared.infrastructure.persistence.entity.ParamManualFuncaoEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), ESTADO_ATIVO));
      predicates.add(cb.equal(root.get("institId").get("id"), institId));
      predicates.add(cb.equal(root.get("cargo").get("id"), cargoId));
      if (seccaoId != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), seccaoId));
      }
      if (carrPccsId != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), carrPccsId));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = manualFuncaoRepository.findAll(spec, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
    return page.hasContent() ? page.getContent().getFirst().getDescricao() : null;
  }

  private boolean aplicaAoContexto(
      ParamObjetivoEntity p,
      Long institId,
      Long seccaoId,
      Long cargoId,
      Long carrPccsId) {
    if (p.getCargo() != null && cargoId != null && !p.getCargo().getId().equals(cargoId)) {
      return false;
    }
    if (p.getCargo() != null && cargoId == null) {
      return false;
    }
    if (p.getCarreira() != null && carrPccsId != null && !p.getCarreira().getId().equals(carrPccsId)) {
      return false;
    }
    if (p.getCarreira() != null && carrPccsId == null) {
      return false;
    }

    if (StringUtils.hasText(p.getAbrangencia()) && "DIRECAO".equalsIgnoreCase(p.getAbrangencia())) {
      if (p.getInstitId() == null || !p.getInstitId().getId().equals(institId)) {
        return false;
      }
    }

    if (p.getSeccaoId() != null && seccaoId != null && !p.getSeccaoId().getId().equals(seccaoId)) {
      return false;
    }
    return p.getSeccaoId() == null || seccaoId != null;
  }

  private String resolveEstadoGrupo(List<AvaliacaoEntity> list) {
    // Ano completo: 2º semestre concluído (C) — independente do estado do 1º
    boolean sem2Concluido = list.stream()
        .anyMatch(a -> "2".equals(a.getSemestre()) && "C".equalsIgnoreCase(a.getEstado()));
    if (sem2Concluido)
      return "C";

    // Parcial: 1º semestre avaliado mas sem 2º semestre concluído
    boolean anyP = list.stream().anyMatch(a -> "P".equalsIgnoreCase(a.getEstado()));
    if (anyP)
      return "P";

    return "A";
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal notaFinal) {
    if (notaFinal == null)
      return null;
    for (var e : escala) {
      if (e == null || e.getEstado() != cv.inps.rh.shared.application.constants.Estado.A)
        continue;
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null)
        continue;
      boolean ge = notaFinal.compareTo(e.getQuantitativaDe()) >= 0;
      boolean le = notaFinal.compareTo(e.getQuantitativaAte()) <= 0;
      if (ge && le) {
        return e.getQualitativa();
      }
    }
    return null;
  }

  private record GroupKey(Integer ano, Long institId, Long cargoId, UUID funUuid) {
  }
}
