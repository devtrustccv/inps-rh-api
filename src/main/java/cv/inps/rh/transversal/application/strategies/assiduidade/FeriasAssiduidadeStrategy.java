package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.transversal.application.dto.AssiduidadeRowDTO;
import cv.inps.rh.transversal.application.queries.RelatorioAssiduidadeQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {

        Specification<FeriasGozadasEntity> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join com Funcionario
            Join<FeriasGozadasEntity, FuncionarioEntity> funcionarioJoin = root.join("funId", JoinType.INNER);

            // Join com TiposRelacionamento para obter Direção/Secção (apenas relacionamento
            // atual)
            Join<FuncionarioEntity, TiposRelacionamentoEntity> relJoin = funcionarioJoin.join("tiposrelacionamentos",
                    JoinType.LEFT);
            // Filtra apenas o relacionamento atual ativo
            predicates.add(cb.equal(relJoin.get("estActAdm"), 1));

            // Join com Mobilidade para Direção e Secção
            Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);
            Join<MobilidadeEntity, InstituicaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT); // Direção
            Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT); // Secção

            // Filtros de Data (Inicio e Fim das Férias dentro do período ou interceptando)
            // Lógica: (ferias.inicio <= fim_filtro) AND (ferias.fim >= inicio_filtro)
            if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim())) {
                LocalDate inicio = DateFormatter.stringToLocalDate(query.getDataInicio());
                LocalDate fim = DateFormatter.stringToLocalDate(query.getDataFim());

                predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), fim));
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataFim"), inicio));
            }

            // Filtro Direção
            if (query.getDireccaoId() != null) {
                predicates.add(cb.equal(instJoin.get("id"), query.getDireccaoId()));
            }

            // Filtro Secção
            if (query.getSeccaoId() != null) {
                predicates.add(cb.equal(secaoJoin.get("id"), query.getSeccaoId()));
            }

            // Filtro Colaborador (Nome ou ID)
            if (StringUtils.hasText(query.getColaborador())) {
                String termo = "%" + query.getColaborador().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(funcionarioJoin.get("nome")), termo));
                // Opcional: filtrar por ID ou NIF se for numérico
            }

            // Ordenação padrão se não especificada
            if (cq != null && cq.getResultType() != Long.class) {
                // cq.orderBy(cb.asc(root.get("dataInicio")));
                // O Pageable já trata a ordenação normalmente, mas se precisar forçar:
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<FeriasGozadasEntity> pageResult = feriasRepository.findAll(spec, pageable);

        List<AssiduidadeRowDTO> dtos = pageResult.getContent().stream().map(entity -> {
            AssiduidadeRowDTO dto = new AssiduidadeRowDTO();

            // Dados do Funcionario
            FuncionarioEntity fun = entity.getFunId();
            if (fun != null) {
                dto.setColaborador(fun.getNome());

                // Tentar obter Direção/Secção do relacionamento atual
                // Como filtramos na query, sabemos que existe, mas precisamos extrair da lista
                // ou fazer fetch.
                // Na entidade carregada, a lista 'tiposrelacionamentos' pode ter todos.
                // Precisamos filtrar em memória o estActAdm=1.
                fun.getTiposrelacionamentos().stream()
                        .filter(t -> t.getEstActAdm() != null && t.getEstActAdm() == 1)
                        .findFirst()
                        .ifPresent(tr -> {
                            if (tr.getMobId() != null) {
                                if (tr.getMobId().getInstidId() != null) {
                                    dto.setDireccao(tr.getMobId().getInstidId().getNome());
                                }
                                if (tr.getMobId().getSecaoId() != null) {
                                    dto.setSeccao(tr.getMobId().getSecaoId().getNome());
                                }
                            }
                        });
            }

            // Dados de Férias
            dto.setNumDiasFerias(entity.getNumDia());
            String periodo = "";
            if (entity.getDataInicio() != null) {
                periodo += DateFormatter.localDateToString(entity.getDataInicio());
            }
            if (entity.getDataFim() != null) {
                periodo += " a " + DateFormatter.localDateToString(entity.getDataFim());
            }
            dto.setPeriodoFerias(periodo);

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }
}
