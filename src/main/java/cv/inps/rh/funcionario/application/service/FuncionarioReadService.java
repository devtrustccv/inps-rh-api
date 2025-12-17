package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
public class FuncionarioReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    int pageNumber = Integer.parseInt(query.getPageNumber());
    int pageSize = Integer.parseInt(query.getPageSize());

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // join com funcionario
      Join<TiposRelacionamentoEntity, FuncionarioEntity> f = root.join(
          TiposRelacionamentoEntity_.funId, JoinType.INNER
      );

      // filtro pelo nome
      if (StringUtils.hasText(query.getNome())) {
        String nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(f.get(FuncionarioEntity_.nome)), "%" + nome + "%"));
      }

      // join e filtro direcção
      if (query.getDireccao() != null) {
        Join<TiposRelacionamentoEntity, InstituicaoEntity> dir = root.join(
            TiposRelacionamentoEntity_.institId, JoinType.LEFT
        );
        predicates.add(cb.equal(dir.get(InstituicaoEntity_.id), query.getDireccao()));
      }

      // join e filtro secção
      if (query.getSeccao() != null) {
        Join<TiposRelacionamentoEntity, SecaoEntity> sec = root.join(
            TiposRelacionamentoEntity_.seccaoId, JoinType.LEFT
        );
        predicates.add(cb.equal(sec.get(SecaoEntity_.id), query.getSeccao()));
      }

      // estado colaborador
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(f.get(FuncionarioEntity_.estadoValidacao), query.getEstado()));
      }

      // join e filtro contrato
      Join<TiposRelacionamentoEntity, ContratoEntity> con = root.join(
          TiposRelacionamentoEntity_.contrVinculoId, JoinType.LEFT
      );
      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(con.get(ContratoEntity_.dataInicio), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(con.get(ContratoEntity_.dataInicio), df));
      }
      if (query.getTipoVinculoLaboral() != null) {
        Join<ContratoEntity, ParamVinculoEntity> vinc = con.join(
            ContratoEntity_.vinculoId, JoinType.LEFT
        );
        predicates.add(cb.equal(vinc.get(ParamVinculoEntity_.id), query.getTipoVinculoLaboral()));
      }

      // apenas relacionamento atual
      predicates.add(cb.equal(root.get(TiposRelacionamentoEntity_.estActAdm), 1));

      if (cq != null) cq.distinct(true);

      return cb.and(predicates.toArray(Predicate[]::new));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<TiposRelacionamentoEntity> page = tiposRelacionamentoEntityRepository.findAll(spec, pageable);

    List<FuncionarioListDTO> content = page.getContent().stream().map(tr -> {
      FuncionarioEntity f = tr.getFunId();
      ContratoEntity contrato = tr.getContrVinculoId();

      FuncionarioListDTO dto = new FuncionarioListDTO();
      dto.setId(f.getId());
      dto.setUuid(f.getUuid() != null ? f.getUuid().toString() : null);
      dto.setNome(f.getNome());

      dto.setCargo(tr.getCargoId() != null ? tr.getCargoId().getNome() : null);
      dto.setDireccao(tr.getInstitId() != null ? tr.getInstitId().getNome() : null);
      dto.setSeccao(tr.getSeccaoId() != null ? tr.getSeccaoId().getNome() : null);

      String carreira = tr.getCarrPccId() != null ? tr.getCarrPccId().getNome() : null;
      String categoria = tr.getCategoriaId() != null ? tr.getCategoriaId().getNome() : null;
      dto.setCarreiraCategoria(carreira != null && categoria != null ? carreira + "/" + categoria : carreira);

      dto.setDataInicio(contrato != null ? DateFormatter.localDateToString(contrato.getDataInicio()) : null);

      dto.setEstadoRegisto(f.getEstado() != null ? f.getEstado().getCode() : null);
      dto.setEstadoRegistoDesc(f.getEstado() != null ? f.getEstado().getDescription() : null);

      dto.setEstadoColaborador(f.getEstadoValidacao());
      dto.setEstadoColaboradorDesc(ValidationUtil.getEnum(Estado.class, f.getEstadoValidacao())
          .map(Estado::getDescription).orElse(null));

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
