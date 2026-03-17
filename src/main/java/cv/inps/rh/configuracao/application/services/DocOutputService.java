package cv.inps.rh.configuracao.application.services;

import cv.inps.rh.configuracao.application.dto.DocOutputRequestDTO;
import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;
import cv.inps.rh.configuracao.infrastructure.mappers.DocOutputMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocOutputService {

  private final ParamDocOutputEntityRepository repository;
  private final ResponsavelEntityRepository responsavelRepository;
  private final DocOutputMapper mapper;

  public WrapperDocOutputListDTO findAll(String tipoDocumento, int pageNumber, int pageSize) {
    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

    Specification<ParamDocOutputEntity> spec = (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("estado"), "A")); // Regra: Listar apenas ativos

      if (StringUtils.hasText(tipoDocumento)) {
        predicates.add(cb.equal(root.get("tipoDocumento"), tipoDocumento));
      }
      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };

    Page<ParamDocOutputEntity> page = repository.findAll(spec, pageRequest);
    return mapper.toWrapper(page);
  }

  public ParamDocOutputEntity findById(String id) {
    var uuid = UUID.fromString(id);
    return repository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Documento não encontrado com o id: " + id));
  }

  @Transactional
  public ParamDocOutputEntity create(DocOutputRequestDTO request) {


    ParamDocOutputEntity entity = mapper.toEntity(request);
    entity.setTitulo(request.getTitulo());
    entity.setCorpo(request.getCorpo());
    entity.setAssinadoPor(request.getAssinadoPor());
    entity.setTipoDocumento(request.getTipoDocumento());
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado("A");

    ResponsavelEntity responsavel = null;

    if (request.getResponsavel()!=null) {
      responsavel = responsavelRepository.findByFunId_Uuid(request.getResponsavel()).orElseThrow(
          () ->
              IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + request.getResponsavel()));
      entity.setResponsavel(responsavel);
    }

    return repository.save(entity);
  }

  @Transactional
  public ParamDocOutputEntity update(String id, DocOutputRequestDTO request) {
    // Passo 1: Inativar o registo atual
    ParamDocOutputEntity oldEntity = findById(id);
    oldEntity.setEstado("I");
    repository.save(oldEntity);


    ParamDocOutputEntity newEntity = mapper.toEntity(request);
    newEntity.setTitulo(request.getTitulo());
    newEntity.setCorpo(request.getCorpo());
    newEntity.setAssinadoPor(request.getAssinadoPor());
    newEntity.setTipoDocumento(request.getTipoDocumento());
    newEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
    newEntity.setEstado("A");

    ResponsavelEntity responsavel = null;
    if (request.getResponsavel()!=null) {
      responsavel = responsavelRepository.findByFunId_Uuid(request.getResponsavel()).orElseThrow(
          () ->
              IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + request.getResponsavel()));
      newEntity.setResponsavel(responsavel);
    }

    return repository.save(newEntity);
  }
}
