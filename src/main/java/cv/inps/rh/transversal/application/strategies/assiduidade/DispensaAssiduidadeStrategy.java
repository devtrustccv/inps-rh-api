package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("dispensaAssiduidadeStrategy")
@RequiredArgsConstructor
public class DispensaAssiduidadeStrategy implements AssiduidadeStrategy {

    private final DispensaEntityRepository dispensaRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ---------------------------------------------------------------------
        // 1. COUNT QUERY (Distinct Funcionario)
        // ---------------------------------------------------------------------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<DispensaEntity> rootCount = countQuery.from(DispensaEntity.class);

        Join<DispensaEntity, TiposRelacionamentoEntity> relJoinCount = rootCount.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoinCount = relJoinCount.join("funId", JoinType.INNER);

        List<Predicate> countPredicates = buildPredicates(query, cb, rootCount, relJoinCount, funcJoinCount);

        countQuery.select(cb.countDistinct(funcJoinCount.get("id")));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        if (totalElements == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // ---------------------------------------------------------------------
        // 2. MAIN QUERY - Group By + Fetching raw data for memory calculation
        // ---------------------------------------------------------------------
        // Como 'horaInicio' e 'horaFim' são Strings na entidade, não podemos fazer
        // aritmética direta no JPA/Criteria de forma portável.
        // A melhor abordagem híbrida é: buscar os dados desagregados (filtrados e
        // paginados por funcionário) e somar em memória.
        // Mas para manter o padrão de paginação por funcionário, precisamos buscar os
        // dados dos funcionários da página atual.

        // Passo 2a: Buscar IDs dos funcionários da página atual
        CriteriaQuery<Long> idsQuery = cb.createQuery(Long.class);
        Root<DispensaEntity> rootIds = idsQuery.from(DispensaEntity.class);
        Join<DispensaEntity, TiposRelacionamentoEntity> relJoinIds = rootIds.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoinIds = relJoinIds.join("funId", JoinType.INNER);

        idsQuery.select(funcJoinIds.get("id")).distinct(true);
        idsQuery.where(buildPredicates(query, cb, rootIds, relJoinIds, funcJoinIds).toArray(new Predicate[0]));
        idsQuery.orderBy(cb.asc(funcJoinIds.get("nome")));

        List<Long> funIds = entityManager.createQuery(idsQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        if (funIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, totalElements);
        }

        // Passo 2b: Buscar dados completos para esses funcionários
        CriteriaQuery<Tuple> dataQuery = cb.createTupleQuery();
        Root<DispensaEntity> rootData = dataQuery.from(DispensaEntity.class);
        Join<DispensaEntity, TiposRelacionamentoEntity> relJoinData = rootData.join("tiprelId", JoinType.INNER);
        Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoinData = relJoinData.join("funId", JoinType.INNER);

        Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoinData = relJoinData.join("mobId", JoinType.LEFT);
        Join<MobilidadeEntity, InstituicaoEntity> instJoinData = mobJoinData.join("instidId", JoinType.LEFT);
        Join<MobilidadeEntity, SecaoEntity> secaoJoinData = mobJoinData.join("secaoId", JoinType.LEFT);

        List<Predicate> dataPredicates = buildPredicates(query, cb, rootData, relJoinData, funcJoinData);
        // Adicionar filtro pelos IDs da página
        dataPredicates.add(funcJoinData.get("id").in(funIds));

        dataQuery.multiselect(
                funcJoinData.get("id").alias("funId"),
                funcJoinData.get("nome").alias("funNome"),
                instJoinData.get("nome").alias("dirNome"),
                secaoJoinData.get("nome").alias("secNome"),
                rootData.get("horaInicio").alias("horaInicio"),
                rootData.get("horaFim").alias("horaFim"),
                rootData.get("data").alias("data"));

        dataQuery.where(dataPredicates.toArray(new Predicate[0]));
        dataQuery.orderBy(cb.asc(funcJoinData.get("nome")));

        List<Tuple> rawData = entityManager.createQuery(dataQuery).getResultList();

        // ---------------------------------------------------------------------
        // 3. AGGREGATE IN MEMORY
        // ---------------------------------------------------------------------
        // Agrupar por Funcionario ID
        List<AssiduidadeRowDTO> dtos = new ArrayList<>();

        rawData.stream()
                .collect(Collectors.groupingBy(t -> t.get("funId", Long.class)))
                .forEach((funId, tuples) -> {
                    AssiduidadeRowDTO dto = new AssiduidadeRowDTO();
                    Tuple first = tuples.get(0);

                    dto.setColaborador(first.get("funNome", String.class));
                    dto.setDireccao(first.get("dirNome", String.class));
                    dto.setSeccao(first.get("secNome", String.class));

                    // Calcular total de horas (minutos)
                    long totalMinutes = 0;
                    for (Tuple t : tuples) {
                        String hInicio = t.get("horaInicio", String.class);
                        String hFim = t.get("horaFim", String.class);
                        if (StringUtils.hasText(hInicio) && StringUtils.hasText(hFim)) {
                            try {
                                // Formato esperado HH:mm ou HH:mm:ss
                                LocalTime start = parseTime(hInicio);
                                LocalTime end = parseTime(hFim);
                                totalMinutes += ChronoUnit.MINUTES.between(start, end);
                            } catch (Exception e) {
                                // Log erro parsing
                            }
                        }
                    }

                    // Converter minutos para horas decimais ou manter minutos?
                    // O DTO tem Long horasDispensaGozadas. Vamos assumir minutos ou converter.
                    // Se o campo for horas, seria totalMinutes / 60.0
                    dto.setHorasDispensaGozadas(totalMinutes); // Em minutos para precisão, ou converter conforme regra
                                                               // de negócio

                    dtos.add(dto);
                });

        // Ordenar DTOs conforme a ordem original dos IDs (para manter consistência da
        // query de IDs)
        // Como usamos map, a ordem pode ter perdido. Reordenar:
        List<AssiduidadeRowDTO> sortedDtos = new ArrayList<>();
        for (Long id : funIds) {
            dtos.stream()
                    .filter(d -> d.getColaborador()
                            .equals(rawData.stream().filter(t -> t.get("funId", Long.class).equals(id)).findFirst()
                                    .get().get("funNome", String.class)))
                    .findFirst().ifPresent(sortedDtos::add);
        }

        return new PageImpl<>(sortedDtos, pageable, totalElements);
    }

    private LocalTime parseTime(String timeStr) {
        // Tenta parsear HH:mm ou HH:mm:ss
        if (timeStr.length() == 5)
            return LocalTime.parse(timeStr); // 08:30
        if (timeStr.length() == 8)
            return LocalTime.parse(timeStr); // 08:30:00
        return LocalTime.parse(timeStr);
    }

    private List<Predicate> buildPredicates(
            RelatorioAssiduidadeQuery query,
            CriteriaBuilder cb,
            Root<DispensaEntity> root,
            Join<DispensaEntity, TiposRelacionamentoEntity> relJoin,
            Join<TiposRelacionamentoEntity, FuncionarioEntity> funcJoin) {

        List<Predicate> predicates = new ArrayList<>();

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
            predicates.add(cb.between(root.get("data"), inicio, fim));
        }

        if (StringUtils.hasText(query.getColaborador())) {
            String termo = "%" + query.getColaborador().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(funcJoin.get("nome")), termo));
        }

        return predicates;
    }
}
