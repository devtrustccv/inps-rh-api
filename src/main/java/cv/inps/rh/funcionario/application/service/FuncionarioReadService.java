package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    Specification<FuncionarioEntity> spec = (root, cq, cb) -> {

      List<Predicate> predicates = new java.util.ArrayList<>();
      if (StringUtils.hasText(query.getNome())) {
        var nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(root.get(FuncionarioEntity_.nome)), "%" + nome + "%"));
      }

      Join<FuncionarioEntity, TiposRelacionamentoEntity> tr = root.join(FuncionarioEntity_.tiposrelacionamentos, jakarta.persistence.criteria.JoinType.LEFT);

      if (query.getDireccao() != null) {
        Join<TiposRelacionamentoEntity, InstituicaoEntity> dir = tr.join(TiposRelacionamentoEntity_.institId, jakarta.persistence.criteria.JoinType.LEFT);
        predicates.add(cb.equal(dir.get(InstituicaoEntity_.id), query.getDireccao()));
      }
      if (query.getSeccao() != null) {
        Join<TiposRelacionamentoEntity, SecaoEntity> sec = tr.join(TiposRelacionamentoEntity_.seccaoId, jakarta.persistence.criteria.JoinType.LEFT);
        predicates.add(cb.equal(sec.get(SecaoEntity_.id), query.getSeccao()));
      }

      if (StringUtils.hasText(query.getEstado()))
        predicates.add(cb.equal(root.get(FuncionarioEntity_.estadoValidacao), query.getEstado()));

      Join<TiposRelacionamentoEntity, ContratoEntity> con = tr.join(TiposRelacionamentoEntity_.contrVinculoId, jakarta.persistence.criteria.JoinType.LEFT);
      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(con.get(ContratoEntity_.dataInicio), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(con.get(ContratoEntity_.dataInicio), df));
      }

      if (query.getTipoVinculoLaboral() != null)
        predicates.add(cb.equal(con.join(ContratoEntity_.vinculoId).get(ParamVinculoEntity_.id), query.getTipoVinculoLaboral()));

      if (cq != null)
        cq.distinct(true);

      predicates.add(cb.equal(tr.get(TiposRelacionamentoEntity_.estActAdm), 1));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<FuncionarioEntity> page = funcionarioEntityRepository.findAll(spec, pageable);

    List<FuncionarioListDTO> content = page.getContent().stream().map(entity -> {
      var dto = new FuncionarioListDTO();
      dto.setId(entity.getId());
      dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
      dto.setNome(entity.getNome());

      var trAtual = funcionarioRules.getTipoRelacionamentoAtual(entity.getUuid());
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
      dto.setEstadoRegistoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);

      dto.setEstadoColaborador(entity.getEstadoValidacao());
      dto.setEstadoColaboradorDesc(ValidationUtil.getEnum(Estado.class, entity.getEstadoValidacao()).map(Estado::getDescription)
          .orElse(null));



      return dto;
    }).toList();

    var wrapper = new WrapperListaFuncionarioDTO();
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
