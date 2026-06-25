package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
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

@Component("feriasAssiduidadeStrategy")
@RequiredArgsConstructor
public class FeriasAssiduidadeStrategy implements AssiduidadeStrategy {

    private final FeriasGozadasEntityRepository feriasRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ---------------------------------------------------------------------
        // 1. COUNT QUERY (Distinct Funcionario)
        // ---------------------------------------------------------------------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FeriasGozadasEntity> rootCount = countQuery.from(FeriasGozadasEntity.class);

        // Predicates para Count
        List<Predicate> countPredicates = buildPredicates(query, cb, rootCount);

        countQuery.select(cb.countDistinct(rootCount.get("funId")));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        if (totalElements == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // ---------------------------------------------------------------------
        // 2. MAIN QUERY (Group By Func/Dir/Sec, SUM(numDia), MIN(start), MAX(end))
        // ---------------------------------------------------------------------
        CriteriaQuery<Tuple> mainQuery = cb.createTupleQuery();
        Root<FeriasGozadasEntity> rootMain = mainQuery.from(FeriasGozadasEntity.class);

        // Joins (para Select e Group By)
        Join<FeriasGozadasEntity, FuncionarioEntity> funcJoin = rootMain.join("funId", JoinType.INNER);

        // Joins de Estrutura (LEFT para não perder dados se funcionário sem alocação)
        // Filtro estActAdm=1 é crucial
        Join<FuncionarioEntity, TiposRelacionamentoEntity> relJoin = funcJoin.join("tiposrelacionamentos",
                JoinType.LEFT);
        Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);
        Join<MobilidadeEntity, DirecaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT);
        Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT);

        // Predicates para Main (Reutiliza lógica, mas precisa dos joins específicos
        // desta query)
        // Como os joins são diferentes objetos (Root vs Root), recriamos a lista, mas a
        // lógica é idêntica.
        // O método buildPredicates abaixo cuida disso se passarmos o Root e fizermos os
        // joins lá,
        // MAS para o GroupBy precisamos das referências dos joins.
        // Então vamos fazer manual aqui para garantir acesso às variáveis de join.

        List<Predicate> mainPredicates = new ArrayList<>();

        // Filtro Relacionamento Atual
        mainPredicates.add(cb.equal(relJoin.get("estActAdm"), 1));

        // Filtros Comuns
        if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim())) {
            LocalDate inicio = DateFormatter.stringToLocalDate(query.getDataInicio());
            LocalDate fim = DateFormatter.stringToLocalDate(query.getDataFim());
            mainPredicates.add(cb.lessThanOrEqualTo(rootMain.get("dataInicio"), fim));
            mainPredicates.add(cb.greaterThanOrEqualTo(rootMain.get("dataFim"), inicio));
        }
        if (query.getDireccaoId() != null) {
            mainPredicates.add(cb.equal(instJoin.get("id"), query.getDireccaoId()));
        }
        if (query.getSeccaoId() != null) {
            mainPredicates.add(cb.equal(secaoJoin.get("id"), query.getSeccaoId()));
        }
        if (StringUtils.hasText(query.getColaborador())) {
            String termo = "%" + query.getColaborador().toLowerCase() + "%";
            mainPredicates.add(cb.like(cb.lower(funcJoin.get("nome")), termo));
        }

        // Select
        mainQuery.multiselect(
                funcJoin.get("id").alias("funId"),
                funcJoin.get("nome").alias("funNome"),
                instJoin.get("nome").alias("dirNome"),
                secaoJoin.get("nome").alias("secNome"),
                cb.sum(rootMain.get("numDia")).alias("totalDias"),
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

            Number total = t.get("totalDias", Number.class);
            dto.setNumDiasFerias(total != null ? total.intValue() : 0);

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

    // Helper apenas para o Count, já que o Main tem joins específicos para GroupBy
    private List<Predicate> buildPredicates(RelatorioAssiduidadeQuery query, CriteriaBuilder cb,
            Root<FeriasGozadasEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        Join<FeriasGozadasEntity, FuncionarioEntity> funcJoin = root.join("funId", JoinType.INNER);

        // Para filtros de estrutura, precisamos dos joins
        if (query.getDireccaoId() != null || query.getSeccaoId() != null) {
            Join<FuncionarioEntity, TiposRelacionamentoEntity> relJoin = funcJoin.join("tiposrelacionamentos",
                    JoinType.LEFT);
            predicates.add(cb.equal(relJoin.get("estActAdm"), 1));

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
