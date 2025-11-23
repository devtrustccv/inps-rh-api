package cv.inps.rh.funcionario.application.service.carreira;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.queries.GetCarreiraListQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
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
public class CarreiraReadService {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final CarreiraMapper carreiraMapper;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperCarreiraListDTO list(GetCarreiraListQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).getValor();

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      Join<TiposRelacionamentoEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      Join<TiposRelacionamentoEntity, CarreiraEntity> carreira = root.join("carreiraId");

      if (StringUtils.hasText(query.getTipoCarreira())) {
        predicates.add(cb.equal(carreira.get("tipoSituacao"), query.getTipoCarreira()));
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
    Page<TiposRelacionamentoEntity> page = tiposRelacionamentoEntityRepository.findAll(spec, pageable);

    List<CarreiraListDTO> content = page.getContent().stream().map(tr -> {
      CarreiraListDTO dto = new CarreiraListDTO();
      var car = tr.getCarreiraId();
      var fun = tr.getFunId();
      var vinc = tr.getVinculoId();
      var carrPcc = tr.getCarrPccId();
      var cargo = tr.getCargoId();
      var esc = tr.getEscalaoId();
      var sitLab = tr.getSituacLaboralId();

      dto.setId(car != null ? car.getId() : null);
      dto.setUuid(car != null && car.getUuid() != null ? car.getUuid().toString() : null);
      dto.setIdFuncionario(fun != null ? fun.getId() : null);
      dto.setUuidFuncionario(fun != null && fun.getUuid() != null ? fun.getUuid().toString() : null);
      dto.setTipoCarreira(car != null ? car.getTipoSituacao() : null);
      dto.setVinculo(vinc != null ? vinc.getNome() : null);
      dto.setCarreira(carrPcc != null ? carrPcc.getNome() : null);
      dto.setCargo(cargo != null ? cargo.getNome() : null);
      dto.setEscalao(esc != null ? esc.getEscalao() : null);
      dto.setSalario(car != null && car.getSalario() != null ? car.getSalario().toString() : null);
      dto.setSituacaoLaboral(sitLab != null ? sitLab.getNome() : null);
      dto.setDataInicio(tr.getDataInicio());
      dto.setDataFim(tr.getDataFim());
      dto.setProcessamento(tr.getFlgProcessa());
      dto.setEstado(car != null && car.getEstado() != null ? car.getEstado().getCode() : null);
      dto.setEstadoDesc(car != null && car.getEstado() != null ? car.getEstado().getDescription() : null);
      return dto;
    }).toList();

    var wrapper = new WrapperCarreiraListDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;


  }
}
