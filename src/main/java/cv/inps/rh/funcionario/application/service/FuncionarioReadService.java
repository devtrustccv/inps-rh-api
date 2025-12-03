package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;


  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    Specification<FuncionarioEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      if (StringUtils.hasText(query.getNome())) {
        var nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome + "%"));
      }

      Join<FuncionarioEntity, TiposRelacionamentoEntity> tr = root.join("tiposrelacionamentos", jakarta.persistence.criteria.JoinType.LEFT);

      if (query.getDireccao() != null) {
        Join<TiposRelacionamentoEntity, InstituicaoEntity> dir = tr.join("institId", jakarta.persistence.criteria.JoinType.LEFT);
        predicates.add(cb.equal(dir.get("id"), query.getDireccao()));
      }
      if (query.getSeccao() != null) {
        Join<TiposRelacionamentoEntity, SecaoEntity> sec = tr.join("seccaoId", jakarta.persistence.criteria.JoinType.LEFT);
        predicates.add(cb.equal(sec.get("id"), query.getSeccao()));
      }
      if (query.getTipoVinculoLaboral() != null) {
        Join<TiposRelacionamentoEntity, ParamVinculoEntity> vinc = tr.join("vinculoId", jakarta.persistence.criteria.JoinType.LEFT);
        predicates.add(cb.equal(vinc.get("id"), query.getTipoVinculoLaboral()));
      }

      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estadoValidacao"), query.getEstado()));
      }

      predicates.add(cb.equal(tr.get("estActAdm"), 1));

      Join<TiposRelacionamentoEntity, ContratoEntity> con = tr.join("contratoId", jakarta.persistence.criteria.JoinType.LEFT);
      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(con.get("dataInicio"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(con.get("dataInicio"), df));
      }

      if (cq != null) {
        cq.distinct(true);
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<FuncionarioEntity> page = funcionarioEntityRepository.findAll(spec, pageable);

    List<FuncionarioListDTO> content = page.getContent().stream().map(entity -> {
      var dto = new FuncionarioListDTO();
      dto.setId(entity.getId());
      dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
      dto.setNome(entity.getNome());

      var trAtual = funcionarioRules.getTipoRelacionamentoAtual(entity);
      if (trAtual != null) {
        dto.setCargo(trAtual.getCargoId() != null ? trAtual.getCargoId().getNome() : null);
        dto.setDireccao(trAtual.getInstitId() != null ? trAtual.getInstitId().getNome() : null);
        dto.setSeccao(trAtual.getSeccaoId() != null ? trAtual.getSeccaoId().getNome() : null);
        var carreira = trAtual.getCarrPccId() != null ? trAtual.getCarrPccId().getNome() : null;
        var categoria = trAtual.getCategoriaId() != null ? trAtual.getCategoriaId().getNome() : null;
        dto.setCarreiraCategoria(carreira != null && categoria != null ? carreira + "/" + categoria : carreira);
        var contrato = trAtual.getContrVinculoId();
        dto.setDataInicio(contrato != null ? DateFormatter.localDateToString(contrato.getDataInicio()) : null);
      }

      dto.setEstadoRegisto(entity.getEstado() != null ? entity.getEstado().getCode() : null);
      dto.setEstadoColaborador(entity.getEstadoValidacao());
      return dto;
    }).toList();

    WrapperListaFuncionarioDTO wrapper = new WrapperListaFuncionarioDTO();
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
