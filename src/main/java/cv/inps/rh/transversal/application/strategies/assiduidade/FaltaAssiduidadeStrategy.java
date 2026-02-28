package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("faltaAssiduidadeStrategy")
@RequiredArgsConstructor
public class FaltaAssiduidadeStrategy implements AssiduidadeStrategy {

    private final FaltaEntityRepository faltaRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ---------------------------------------------------------------------
        // 1. COUNT QUERY (Distinct Funcionario)
        // ---------------------------------------------------------------------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FaltaEntity> rootCount = countQuery.from(FaltaEntity.class);

        // Joins para Count (Funcionario através de TiposRelacionamento)
        Join<FaltaEntity, TiposRelacionamentoEntity> relJoinCount = rootCount.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoinCount = relJoinCount.join("funId", JoinType.INNER);

        // Predicates para Count
        List<Predicate> countPredicates = buildPredicates(query, cb, rootCount, relJoinCount, funcJoinCount);

        countQuery.select(cb.countDistinct(funcJoinCount.get("id")));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        if (totalElements == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // ---------------------------------------------------------------------
        // 2. MAIN QUERY (Group By Func/Dir/Sec, COUNT(id), MIN(start), MAX(end))
        // ---------------------------------------------------------------------
        CriteriaQuery<Tuple> mainQuery = cb.createTupleQuery();
        Root<FaltaEntity> rootMain = mainQuery.from(FaltaEntity.class);

        // Joins
        Join<FaltaEntity, TiposRelacionamentoEntity> relJoin = rootMain.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoin = relJoin.join("funId", JoinType.INNER);

        // Joins de Estrutura
        Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);
        Join<MobilidadeEntity, InstituicaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT);
        Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT);

        // Predicates para Main
        List<Predicate> mainPredicates = buildPredicates(query, cb, rootMain, relJoin, funcJoin);

        // Select
        mainQuery.multiselect(
                funcJoin.get("id").alias("funId"),
                funcJoin.get("nome").alias("funNome"),
                instJoin.get("nome").alias("dirNome"),
                secaoJoin.get("nome").alias("secNome"),
                cb.count(rootMain.get("id")).alias("totalFaltas"),
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

            Number total = t.get("totalFaltas", Number.class);
            dto.setNumFaltas(total != null ? total.intValue() : 0);

            // Tratamento de datas (LocalDateTime para String de data)
            LocalDateTime min = t.get("minInicio", LocalDateTime.class);
            LocalDateTime max = t.get("maxFim", LocalDateTime.class);
            String periodo = "";
            if (min != null)
                periodo += DateFormatter.localDateToString(min.toLocalDate());
            if (max != null)
                periodo += " a " + DateFormatter.localDateToString(max.toLocalDate());
            dto.setPeriodo(periodo);

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, totalElements);
    }

    private List<Predicate> buildPredicates(
            RelatorioAssiduidadeQuery query,
            CriteriaBuilder cb,
            Root<FaltaEntity> root,
            Join<FaltaEntity, TiposRelacionamentoEntity> relJoin,
            Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoin) {

        List<Predicate> predicates = new ArrayList<>();

        // Filtro Relacionamento Atual (Fundamental para pegar a direção certa)
        predicates.add(cb.equal(relJoin.get("estActAdm"), 1));

        // Se filtrar por direção/secção, precisamos navegar pelo mobId
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
            // Converter String data (yyyy-MM-dd) para LocalDateTime
            LocalDate inicio = DateFormatter.stringToLocalDate(query.getDataInicio());
            LocalDate fim = DateFormatter.stringToLocalDate(query.getDataFim());

            // FaltaEntity usa LocalDateTime. Ajustar para início e fim do dia.
            LocalDateTime inicioDateTime = inicio.atStartOfDay();
            LocalDateTime fimDateTime = fim.atTime(23, 59, 59);

            // Filtro de intersecção ou range simples
            // Como falta é geralmente um dia ou horas, usar dataInicio
            predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), inicioDateTime));
            predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), fimDateTime));
        }

        if (StringUtils.hasText(query.getColaborador())) {
            String termo = "%" + query.getColaborador().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(funcJoin.get("nome")), termo));
        }

        return predicates;
    }
}
