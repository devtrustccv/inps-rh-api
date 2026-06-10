package cv.inps.rh.configuracao.application.services;

import cv.inps.rh.configuracao.application.dto.AssociarResponsaveisRequestDTO;
import cv.inps.rh.configuracao.application.dto.ResponsaveisDirecaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.ResponsavelResponseDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListResponsaveisDTO;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Transactional
@Service
public class ResponsavelService {

  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SecaoEntityRepository secaoEntityRepository;
  private final InstituicaoEntityRepository instituicaoEntityRepository;

  public ResponsavelService(ResponsavelEntityRepository responsavelEntityRepository, FuncionarioEntityRepository funcionarioEntityRepository, SecaoEntityRepository secaoEntityRepository, InstituicaoEntityRepository instituicaoEntityRepository) {
    this.responsavelEntityRepository = responsavelEntityRepository;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.secaoEntityRepository = secaoEntityRepository;
    this.instituicaoEntityRepository = instituicaoEntityRepository;
  }

  public void saveResponsaveis(AssociarResponsaveisRequestDTO request) {
    request.getResponsaveis().forEach(row -> {
      var responsavel = ValidationUtil.isValidNumberId(row.getIdResponsavel()) ? responsavelEntityRepository.findByIdOrThrow(row.getIdResponsavel()) : new ResponsavelEntity();
      responsavel.setInstitId(instituicaoEntityRepository.findByIdOrThrow(row.getIdDirecao()));
      responsavel.setFunId(funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdFuncionario())));
      responsavel.setSecaoId(StringUtils.hasText(row.getIdSeccao()) ? secaoEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdSeccao())) : null);
      responsavel.setEmail(row.getEmail());
      responsavelEntityRepository.save(responsavel);
    });
  }

  public ResponsaveisDirecaoResponseDTO getResponsavelData(Long institutoId, @NotBlank(message = "The field <seccaoId> is required") String seccaoId) {

    var savedData = responsavelEntityRepository.findAllByInstitId_idAndSecaoId_uuid(institutoId, UUID.fromString(seccaoId));

    var arraySavedData = new ArrayList<ResponsavelResponseDTO>();

    var sectionIdsAlreadySaved = new HashSet<Long>();

    savedData.forEach(e -> {
      var obj = new ResponsavelResponseDTO();
      obj.setEmail(e.getEmail());
      obj.setIdResponsavel(e.getId());

      var section = e.getSecaoId();
      if (section != null) {
        obj.setNomeSeccao(section.getNome());
        obj.setIdSeccao(section.getId().toString());
        sectionIdsAlreadySaved.add(section.getId());
      }

      var direction = e.getInstitId();
      obj.setIdDirecao(direction.getId());
      obj.setNomeDirecao(direction.getNome());

      var funId = e.getFunId();
      obj.setIdFuncionario(funId.getUuid().toString());
      obj.setNomeFuncionario(funId.getNome());
      arraySavedData.add(obj);
    });

    var allDirectionSections = secaoEntityRepository.findAllByEstadoAndInstId_Id(Estado.A, institutoId);
    allDirectionSections.forEach(s -> {
      if (!sectionIdsAlreadySaved.contains(s.getId())) {
        sectionIdsAlreadySaved.add(s.getId());
        var obj = new ResponsavelResponseDTO();
        obj.setIdSeccao(s.getId().toString());
        obj.setNomeSeccao(s.getNome());
        arraySavedData.add(obj);
      }
    });

    return new ResponsaveisDirecaoResponseDTO(arraySavedData);
  }


  public WrapperListResponsaveisDTO getResponsaveis(GetResponsaveisQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<ResponsavelEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getNomeFuncionario())) {
        var value = "%" + query.getNomeFuncionario().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.funId).get(FuncionarioEntity_.NOME)), value));
      }

      if (StringUtils.hasText(query.getNomeInstituicao())) {
        var value = "%" + query.getNomeInstituicao().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.institId).get(InstituicaoEntity_.NOME)), value));
      }

      if (query.getIdInstituicao() != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.institId).get(InstituicaoEntity_.ID), query.getIdInstituicao()));
      }

      if (StringUtils.hasText(query.getNomeSecccao())) {
        var value = "%" + query.getNomeSecccao().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.secaoId).get(SecaoEntity_.NOME)), value));
      }

      if (query.getIdSeccao() != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.secaoId).get(SecaoEntity_.ID), query.getIdSeccao()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, ResponsavelEntity_.ID));
    var page = responsavelEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream().map(e -> {
      var dto = new ResponsavelResponseDTO();
      dto.setIdResponsavel(e.getId());

      var instit = e.getInstitId();
      dto.setIdDirecao(instit.getId());
      dto.setNomeDirecao(instit.getNome());

      var fun = e.getFunId();
      dto.setIdFuncionario(fun.getUuid().toString());
      dto.setNomeFuncionario(fun.getNome());

      var secao = e.getSecaoId();
      if (secao != null) {
        dto.setIdSeccao(secao.getId().toString());
        dto.setNomeSeccao(secao.getNome());
      }

      dto.setEmail(e.getEmail());
      return dto;
    }).toList();

    var wrapper = new WrapperListResponsaveisDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

}
