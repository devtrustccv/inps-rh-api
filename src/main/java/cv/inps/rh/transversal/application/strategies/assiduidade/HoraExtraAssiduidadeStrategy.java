package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.transversal.application.dto.AssiduidadeRowDTO;
import cv.inps.rh.transversal.application.queries.RelatorioAssiduidadeQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("horaExtraAssiduidadeStrategy")
@RequiredArgsConstructor
public class HoraExtraAssiduidadeStrategy implements AssiduidadeStrategy {

    private final HoraExtraEntityRepository horaExtraRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ---------------------------------------------------------------------
        // 1. COUNT QUERY (Distinct Funcionario)
        // ---------------------------------------------------------------------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<HoraExtraEntity> rootCount = countQuery.from(HoraExtraEntity.class);

        // Joins para Count
        Join<HoraExtraEntity, TiposRelacionamentoEntity> relJoinCount = rootCount.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoinCount = relJoinCount.join("funId", JoinType.INNER);

        List<Predicate> countPredicates = buildPredicates(query, cb, rootCount, relJoinCount, funcJoinCount);

        countQuery.select(cb.countDistinct(funcJoinCount.get("id")));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        if (totalElements == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // ---------------------------------------------------------------------
        // 2. MAIN QUERY
        // ---------------------------------------------------------------------
        CriteriaQuery<Tuple> mainQuery = cb.createTupleQuery();
        Root<HoraExtraEntity> rootMain = mainQuery.from(HoraExtraEntity.class);

        // Joins
        Join<HoraExtraEntity, TiposRelacionamentoEntity> relJoin = rootMain.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoin = relJoin.join("funId", JoinType.INNER);

        // Joins de Estrutura
        Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);
        Join<MobilidadeEntity, DirecaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT);
        Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT);

        List<Predicate> mainPredicates = buildPredicates(query, cb, rootMain, relJoin, funcJoin);

        // Select
        mainQuery.multiselect(
                funcJoin.get("id").alias("funId"),
                funcJoin.get("nome").alias("funNome"),
                instJoin.get("nome").alias("dirNome"),
                secaoJoin.get("nome").alias("secNome"),
                cb.sum(rootMain.get("horasDiarias")).alias("totalHoras"),
                cb.min(rootMain.get("dataInicio")).alias("minInicio"),
                cb.max(rootMain.get("dataFim")).alias("maxFim"));

        // Group By
        mainQuery.groupBy(
                funcJoin.get("id"),
                funcJoin.get("nome"),
                instJoin.get("id"),
                instJoin.get("nome"),
                secaoJoin.get("id"),
                secaoJoin.get("nome"));

        mainQuery.where(mainPredicates.toArray(new Predicate[0]));
        mainQuery.orderBy(cb.asc(funcJoin.get("nome")));

        // Executar
        List<Tuple> results = entityManager.createQuery(mainQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // ---------------------------------------------------------------------
        // 3. MAP TO DTO
        // ---------------------------------------------------------------------
        List<AssiduidadeRowDTO> dtos = results.stream().map(t -> {
            AssiduidadeRowDTO dto = new AssiduidadeRowDTO();

            dto.setColaborador(t.get("funNome", String.class));
            dto.setDireccao(t.get("dirNome", String.class));
            dto.setSeccao(t.get("secNome", String.class));

            Number total = t.get("totalHoras", Number.class);
            dto.setNumHorasExtras(total != null ? total.longValue() : 0L);

            LocalDate min = t.get("minInicio", LocalDate.class);
            LocalDate max = t.get("maxFim", LocalDate.class);
            String periodo = "";
            if (min != null)
                periodo += DateFormatter.localDateToString(min);
            if (max != null)
                periodo += " a " + DateFormatter.localDateToString(max);
            dto.setPeriodo(periodo);

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, totalElements);
    }

    private List<Predicate> buildPredicates(
            RelatorioAssiduidadeQuery query,
            CriteriaBuilder cb,
            Root<HoraExtraEntity> root,
            Join<HoraExtraEntity, TiposRelacionamentoEntity> relJoin,
            Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoin) {

        List<Predicate> predicates = new ArrayList<>();

        // Filtro Relacionamento Atual
        predicates.add(cb.equal(relJoin.get("estActAdm"), 1));

        if (query.getDireccaoId() != null || query.getSeccaoId() != null) {
            Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);

            if (query.getDireccaoId() != null) {
                predicates.add(cb.equal(mobJoin.get("instidId").get("id"), query.getDireccaoId()));
            }
            if (query.getSeccaoId() != null) {
                predicates.add(cb.equal(mobJoin.get("secaoId").get("id"), query.getSeccaoId()));
            }
        }

        if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim())) {
            LocalDate inicio = DateFormatter.stringToLocalDate(query.getDataInicio());
            LocalDate fim = DateFormatter.stringToLocalDate(query.getDataFim());
            predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), fim));
            predicates.add(cb.greaterThanOrEqualTo(root.get("dataFim"), inicio));
        }

        if (StringUtils.hasText(query.getColaborador())) {
            String termo = "%" + query.getColaborador().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(funcJoin.get("nome")), termo));
        }

        return predicates;
    }
}
