package cv.inps.rh.avaliacao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.avaliacao.application.commands.InitAvaliacaoCommand;
import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaDefinicaoObjetivoDTO;
import cv.inps.rh.avaliacao.application.queries.GetListaDefinicaoObjectivosQuery;
import cv.inps.rh.avaliacao.infrastructure.mappers.AvaliacaoMapper;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoAtitudePessoalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoCompetenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCargoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamManualFuncaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
  private final AvaliacaoMapper avaliacaoMapper;

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
      AvaliacaoMapper avaliacaoMapper) {
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
    this.avaliacaoMapper = avaliacaoMapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> initAvaliacao(InitAvaliacaoCommand command) {

    var dto = command.getAvaliacaoinicializarrequest();

    if (!StringUtils.hasText(dto.getSemestre()) || (!"1".equals(dto.getSemestre()) && !"2".equals(dto.getSemestre()))) {
      throw IgrpResponseStatusException.badRequest("semestre deve ser '1' ou '2'");
    }

    if (CollectionUtils.isEmpty(dto.getFunIds())) {
      throw IgrpResponseStatusException.badRequest("funIds não pode estar vazio");
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

    var created = new ArrayList<String>(dto.getFunIds().size());

    for (var funId : dto.getFunIds()) {

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

      criarLinhasAvaliacao(avaliacao, det.getObjetivos(), dto.getInstitId(), dto.getSeccaoId(), dto.getCargoId(),
          dto.getCarrPccsId());

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

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = avaliacaoRepository.findAll(spec, pageable);

    var response = new WrapperListaDefinicaoObjetivoDTO();
    cv.inps.rh.shared.util.PageMapper.fillPagination(page, response);
    response.setContent(page.getContent().stream().map(avaliacaoMapper::toResumo).toList());
    return response;
  }

  private void criarLinhasAvaliacao(
      AvaliacaoEntity avaliacao,
      List<ParamObjetivoEntity> params,
      Long institId,
      Long seccaoId,
      Long cargoId,
      Long carrPccsId) {
    if (params == null)
      return;

    var manualDescricao = resolverDescricaoManual(institId, seccaoId, cargoId, carrPccsId);

    for (var p : params) {
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
    }
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



  public MediaResultado calcularMedia(FuncionarioEntity fun, int anos) {

    var evaluations = avaliacaoRepository.findUltimasAvaliacoes(
        fun.getId(),
        PageRequest.of(0, anos)
    );
    if (evaluations.size() < anos)
      return MediaResultado.invalido();

    var media = evaluations.stream()
        .mapToDouble(AvaliacaoEntity::getAvaliacaoFinal)
        .average()
        .orElse(0);

    var abaixo50 = evaluations.stream().anyMatch(a -> a.getAvaliacaoFinal() < 50);

    var elegivelProgressao = media >= 60 && !abaixo50;

    var elegivelPromocao = media >= 90;

    return new MediaResultado(media, elegivelProgressao, elegivelPromocao);
  }
}
