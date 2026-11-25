package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.PicagemListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaPicagemQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MovimentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
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

@Service
@RequiredArgsConstructor
public class PicagemService {

  private final MovimentoEntityRepository movimentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaPicagemDTO getListaPicagem(GetListaPicagemQuery query) {

    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    Specification<MovimentoEntity> spec = (root, cq, cb) -> {
      var predicates = new ArrayList<Predicate>();

      // ===============================
      // Nome colaborador
      // ===============================
      if (StringUtils.hasText(query.getColaborador())) {
        predicates.add(
            cb.like(
                cb.lower(root.get("nomeColaborador")),
                "%" + query.getColaborador().toLowerCase() + "%"));
      }

      // RH_MOVIMENTOS.ID_COLABORADOR é o número de colaborador (RH_T_FUNCIONARIOS.ID_COLABORADOR),
      // não o id interno. A vista não traz o uuid do funcionário (FUNCIONARIO_UUID é '' fixo), por isso
      // o filtro vai ao funcionário pelo uuid e compara o número de colaborador.
      if (StringUtils.hasText(query.getFuncionarioUuid()) && cq != null) {
        try {
          var funcUuid = java.util.UUID.fromString(query.getFuncionarioUuid());
          Subquery<Long> fun = cq.subquery(Long.class);
          var f = fun.from(FuncionarioEntity.class);
          fun.select(f.get("id")).where(
              cb.equal(f.get("uuid"), funcUuid),
              cb.equal(f.get("idColaborador"), root.get("idColaborador")));
          predicates.add(cb.exists(fun));
        } catch (IllegalArgumentException ignored) {
          // Ignore invalid UUIDs
        }
      }

      // ===============================
      // Datas
      // ===============================
      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dtMovimento"), di));
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dtMovimento"), df));
      }

      // ===============================
      // Direção / Secção / UPS
      // ===============================
      if (cq != null && (
          query.getDirecao() != null
              || query.getSeccao() != null
              || query.getUps() != null)) {

        Subquery<TiposRelacionamentoEntity> sub =
            cq.subquery(TiposRelacionamentoEntity.class);

        var tr = sub.from(TiposRelacionamentoEntity.class);
        var conds = new ArrayList<Predicate>();

        // contrato do funcionário com este número de colaborador (não o id interno)
        conds.add(cb.equal(tr.get("funId").get("idColaborador"),
            root.get("idColaborador")));

        conds.add(cb.equal(tr.get("estActAdm"), 1));

        conds.add(cb.lessThanOrEqualTo(
            tr.get("dataInicio"),
            root.get("dtMovimento")));

        conds.add(cb.or(
            cb.isNull(tr.get("dataFim")),
            cb.greaterThanOrEqualTo(
                tr.get("dataFim"),
                root.get("dtMovimento"))));

        if (query.getDirecao() != null) {
          conds.add(cb.equal(
              tr.get("mobId").get("instidId").get("id"),
              query.getDirecao()));
        }

        if (query.getSeccao() != null) {
          conds.add(cb.equal(
              tr.get("mobId").get("secaoId").get("id"),
              query.getSeccao()));
        }

        if (query.getUps() != null) {
          // upsId é um @ManyToOne para UpsEntity — tem de navegar até ao id, tal como
          // direcção e secção acima. Comparar a associação com um Long dava ORA/500.
          conds.add(cb.equal(
              tr.get("mobId").get("localTrabId").get("upsId").get("id"),
              query.getUps()));
        }

        sub.select(tr).where(cb.and(conds.toArray(new Predicate[0])));
        predicates.add(cb.exists(sub));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(
        pageNumber,
        pageSize,
        Sort.by(Sort.Direction.DESC, "dtMovimento"));

    Page<MovimentoEntity> page =
        movimentoEntityRepository.findAll(spec, pageable);

    var content = page.getContent()
        .stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaPicagemDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  // ======================================================
  // DTO
  // ======================================================
  private PicagemListDTO toDTO(MovimentoEntity e) {

    var dto = new PicagemListDTO();
    dto.setId(e.getId() != null ? e.getId().toString() : null);
    dto.setNomeColaborador(e.getNomeColaborador());
    dto.setData(DateFormatter.localDateToString(e.getDtMovimento()));

    if (isEntrada(e)) {
      dto.setHoraEntrada(
          e.getHoraMovimento() + " - " + e.getLocalMovimento());
    }

    if (isSaida(e)) {
      dto.setHoraSaida(
          e.getHoraMovimento() + " - " + e.getLocalMovimento());
    }

    return dto;
  }


  private boolean isEntrada(MovimentoEntity e) {
    return ("ENTRADA".equalsIgnoreCase(e.getTpMovimento()))
        || ("Entrada".equalsIgnoreCase(e.getTpMovimentoDesc()))
        || (e.getInOutMode() != null && e.getInOutMode() == 0);
  }

  private boolean isSaida(MovimentoEntity e) {
    return ("SAIDA".equalsIgnoreCase(e.getTpMovimento()))
        || ("Saída".equalsIgnoreCase(e.getTpMovimentoDesc()))
        || (e.getInOutMode() != null && e.getInOutMode() == 1);
  }
}
