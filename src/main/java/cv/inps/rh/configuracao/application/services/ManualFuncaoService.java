package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.CreateManualFuncaoCommand;
import cv.inps.rh.configuracao.application.dto.WrapperListaManualFuncaoDTO;
import cv.inps.rh.configuracao.infrastructure.mappers.ManualFuncaoMapper;
import cv.inps.rh.configuracao.application.queries.GetListaManualFuncaoQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamManualFuncaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCargoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamManualFuncaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ManualFuncaoService {

  private static final String ESTADO_ATIVO = "A";

  private final ParamManualFuncaoEntityRepository manualRepository;
  private final InstituicaoEntityRepository instituicaoRepository;
  private final SecaoEntityRepository secaoRepository;
  private final ParamCargoEntityRepository cargoRepository;
  private final ParamCarreiraEntityRepository carreiraRepository;
  private final ManualFuncaoMapper mapper;

  public ManualFuncaoService(
      ParamManualFuncaoEntityRepository manualRepository,
      InstituicaoEntityRepository instituicaoRepository,
      SecaoEntityRepository secaoRepository,
      ParamCargoEntityRepository cargoRepository,
      ParamCarreiraEntityRepository carreiraRepository,
      ManualFuncaoMapper mapper) {
    this.manualRepository = manualRepository;
    this.instituicaoRepository = instituicaoRepository;
    this.secaoRepository = secaoRepository;
    this.cargoRepository = cargoRepository;
    this.carreiraRepository = carreiraRepository;
    this.mapper = mapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> registar(CreateManualFuncaoCommand command) {

    var dto = command.getManualfuncaorequest();

    var entity = new ParamManualFuncaoEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado(ESTADO_ATIVO);
    entity.setDescricao(dto.getDescricao());

    entity.setInstitId(instituicaoRepository.findByIdOrThrow(dto.getInstitId()));

    if (dto.getSeccaoId() != null) {
      entity.setSeccaoId(secaoRepository.findByIdOrThrow(dto.getSeccaoId()));
    }

    if (dto.getCargoId() != null) {
      var cargo = cargoRepository.findById(dto.getCargoId())
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
              "ParamCargoEntity not found for id: " + dto.getCargoId()));
      entity.setCargo(cargo);
    }

    if (dto.getCarrPccsId() != null) {
      entity.setCarreira(carreiraRepository.findByIdOrThrow(dto.getCarrPccsId()));
    }

    manualRepository.save(entity);

    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", entity.getUuid()));
  }

  @Transactional(readOnly = true)
  public WrapperListaManualFuncaoDTO listar(GetListaManualFuncaoQuery query) {

    var page = Integer.parseInt(query.getPageNumber());
    var size = Integer.parseInt(query.getPageSize());
    var pageable = PageRequest.of(page, size);

    Specification<ParamManualFuncaoEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();

      predicates.add(cb.equal(root.get("estado"), ESTADO_ATIVO));

      if (query.getCargoId() != null) {
        predicates.add(cb.equal(root.get("cargo").get("id"), query.getCargoId()));
      }

      if (query.getCarrPccsId() != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), query.getCarrPccsId()));
      }

      if (query.getInstitId() != null) {
        predicates.add(cb.equal(root.get("institId").get("id"), query.getInstitId()));
      }

      if (query.getSeccaoId() != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), query.getSeccaoId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var resultPage = manualRepository.findAll(spec, pageable);

    var response = new WrapperListaManualFuncaoDTO();
    PageMapper.fillPagination(resultPage, response);
    response.setContent(resultPage.getContent().stream().map(mapper::toResponse).toList());
    return response;
  }
}
