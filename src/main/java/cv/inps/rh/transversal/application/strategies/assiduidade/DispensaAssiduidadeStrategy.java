package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.TimeUtils;
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

@Component("dispensaAssiduidadeStrategy")
@RequiredArgsConstructor
public class DispensaAssiduidadeStrategy implements AssiduidadeStrategy {

    private final DispensaEntityRepository dispensaRepository;
    private final AssiduidadeParametroEntityRepository parametroRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. COUNT QUERY (Distinct Funcionario)
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

        // 2. MAIN QUERY - Group By + Fetching raw data for memory calculation

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

        // Obter total horas disponíveis globalmente (parametro)
        String horasDispStr = parametroRepository.findActiveTDispensa().orElse("00:00");
        int totalMinutosDisponiveis = TimeUtils.hhmmToMinutes(horasDispStr);

        // 3. AGGREGATE IN MEMORY
        List<AssiduidadeRowDTO> dtos = new ArrayList<>();

        rawData.stream()
                .collect(Collectors.groupingBy(t -> t.get("funId", Long.class)))
                .forEach((funId, tuples) -> {
                    AssiduidadeRowDTO dto = new AssiduidadeRowDTO();
                    Tuple first = tuples.get(0);

                    dto.setColaborador(first.get("funNome", String.class));
                    dto.setDireccao(first.get("dirNome", String.class));
                    dto.setSeccao(first.get("secNome", String.class));

                    // Calcular total de minutos gozados no período filtrado
                    long totalMinutesGozados = 0;
                    for (Tuple t : tuples) {
                        String hInicio = t.get("horaInicio", String.class);
                        String hFim = t.get("horaFim", String.class);
                        totalMinutesGozados += TimeUtils.diffMinutes(hInicio, hFim);
                    }
                    dto.setHorasDispensaGozadas(totalMinutesGozados);

                    // Calcular Saldo (Disponível - Gozado no Mês Atual)
                    // A regra do service é mensal. Se o filtro for maior que um mês, o saldo pode
                    // ser confuso.
                    // Mas geralmente mostra-se o saldo atual.
                    // Vamos calcular o saldo subtraindo o total gozado (assumindo que o filtro é
                    // mensal ou o usuário quer ver o saldo do período).
                    // Ou deveríamos buscar todas as dispensas do mês atual para calcular o saldo
                    // real?
                    // O DTO pede "horasDispensaPorGozar".
                    // Simplificação: Total Disponível - Total Gozado (neste relatório)
                    // Se quiser o saldo real do mês, teríamos que fazer outra query.
                    // Dado o contexto de relatório, vamos mostrar Disponivel - GozadoNestePeriodo.

                    long saldo = Math.max(0, totalMinutosDisponiveis - totalMinutesGozados);
                    dto.setHorasDispensaPorGozar(saldo);

                    dtos.add(dto);
                });

        // Reordenar
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
