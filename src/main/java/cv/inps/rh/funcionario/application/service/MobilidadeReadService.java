package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;
import cv.inps.rh.funcionario.application.queries.GetListMobilidadesQuery;
import cv.inps.rh.funcionario.application.queries.GetMobilidadeByIdQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MobilidadeReadService {

  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final MobilidadeMapper mobilidadeMapper;
  private final DomainEntityRepository domainEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListMobilidadeDTO getListMobilidade(GetListMobilidadesQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<MobilidadeEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      Join<MobilidadeEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      var estados = List.of(Estado.A, Estado.I);
      predicates.add(
          root.get("estado").in(estados)
      );

      if (StringUtils.hasText(query.getTipoMobilidade())) {
        predicates.add(cb.equal(root.get("tipoSituacao"), query.getTipoMobilidade()));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), df));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<MobilidadeEntity> page = mobilidadeEntityRepository.findAll(spec, pageable);
    var domain = domainEntityRepository.getActiveDomainAndReferenciaByCode(Domains.TIPO_MOV_LABORAL.name(), "MOBILIDADE");
    List<MobilidadeListDTO> content = page.getContent().stream().map(m -> {
      MobilidadeListDTO dto = new MobilidadeListDTO();
      dto.setId(m.getId());
      dto.setIdFuncionario(m.getFunId() != null ? m.getFunId().getId() : null);
      dto.setUuid(m.getUuid() != null ? m.getUuid().toString() : null);
      dto.setUuidFuncionario(m.getFunId() != null && m.getFunId().getUuid() != null ? m.getFunId().getUuid().toString() : null);
      dto.setDireccao(m.getInstidId() != null ? m.getInstidId().getNome() : null);
      dto.setSeccao(m.getSecaoId() != null ? m.getSecaoId().getNome() : null);
      dto.setLocalTrabalho(m.getLocalTrabId() != null ? m.getLocalTrabId().getNome() : null);
      dto.setDataInicio(DateFormatter.localDateToString(m.getDataInicio()));
      dto.setDataFim(DateFormatter.localDateToString(m.getDataFim()));
      dto.setProcessamento(null);
      dto.setEstado(m.getEstado() != null ? m.getEstado().getCode() : null);
      dto.setEstadoDesc(m.getEstado() != null ? m.getEstado().getDescription() : null);
      dto.setTipoMobilidade(m.getTipoSituacao());
      dto.setTipoMobilidadeDesc(domain.getOrDefault(m.getTipoSituacao(), m.getTipoSituacao()));

      return dto;
    }).toList();

    var wrapper = new WrapperListMobilidadeDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  @Transactional(readOnly = true)
  public MobilidadeDTO getMobilidade(GetMobilidadeByIdQuery query) {

    IdentificadorUnico id = IdentificadorUnico.from(query.getId());
    var mobilidade = mobilidadeEntityRepository.findByUuid(id.valor()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("mobilidade nao encontrada com id"+query.getId())
    );

    var mobilidadteDto = mobilidadeMapper.mobilidadeDTO(mobilidade);



    return mobilidadteDto;
  }
}
