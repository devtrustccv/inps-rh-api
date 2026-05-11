package cv.inps.rh.funcionario.application.service.recibo;

import cv.inps.rh.funcionario.application.dto.ReciboListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListReciboDTO;
import cv.inps.rh.funcionario.application.queries.GetListRecibosQuery;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoFuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository;
import cv.inps.rh.shared.util.DateFormatter;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReciboReadService {

  private final ProcessamentoFuncionarioRepository processoFuncionarioRepository;

  @Transactional(readOnly = true)
  public WrapperListReciboDTO getListRecibos(GetListRecibosQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var funcionarioUuid = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<ProcessamentoFuncionarioEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("tiprel").get("funId").get("uuid"), funcionarioUuid));

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataReferenciaDe"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataReferenciaDe"), df));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataReferenciaDe"));
    Page<ProcessamentoFuncionarioEntity> page = processoFuncionarioRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListReciboDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  private ReciboListDTO toDTO(ProcessamentoFuncionarioEntity entity) {
    var dto = new ReciboListDTO();
    dto.setId(entity.getId());
    dto.setDataReferenciaDe(entity.getDataReferenciaDe() != null
        ? DateFormatter.localDateToString(entity.getDataReferenciaDe()) : null);
    dto.setDataReferenciaAte(entity.getDataReferenciaAte() != null
        ? DateFormatter.localDateToString(entity.getDataReferenciaAte()) : null);
    dto.setDataProcessamento(entity.getDataProcessamento() != null
        ? DateFormatter.localDateToString(entity.getDataProcessamento()) : null);
    dto.setEstado(entity.getEstado());
    dto.setEstadoDesc(entity.getEstado());
    return dto;
  }

}
