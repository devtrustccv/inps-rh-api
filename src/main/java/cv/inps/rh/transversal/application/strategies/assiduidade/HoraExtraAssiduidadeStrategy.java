package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
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

@Component("horaExtraAssiduidadeStrategy")
@RequiredArgsConstructor
public class HoraExtraAssiduidadeStrategy implements AssiduidadeStrategy {

    private final HoraExtraEntityRepository horaExtraRepository;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {

        Specification<HoraExtraEntity> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join com TiposRelacionamento (Direto na entidade)
            Join<HoraExtraEntity, TiposRelacionamentoEntity> tiprelJoin = root.join("tiprelId", JoinType.INNER);

            // Join com Funcionario
            Join<TiposRelacionamentoEntity, FuncionarioEntity> funcionarioJoin = tiprelJoin.join("funId", JoinType.INNER);

            // Join com Mobilidade para Direção e Secção
            Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = tiprelJoin.join("mobId", JoinType.LEFT);
            Join<MobilidadeEntity, InstituicaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT); // Direção
            Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT); // Secção

            // Filtros de Data
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

            // Filtro Colaborador
            if (StringUtils.hasText(query.getColaborador())) {
                String termo = "%" + query.getColaborador().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(funcionarioJoin.get("nome")), termo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HoraExtraEntity> pageResult = horaExtraRepository.findAll(spec, pageable);

        List<AssiduidadeRowDTO> dtos = pageResult.getContent().stream().map(entity -> {
            AssiduidadeRowDTO dto = new AssiduidadeRowDTO();

            TiposRelacionamentoEntity tr = entity.getTiprelId();
            if (tr != null) {
                if (tr.getFunId() != null) {
                    dto.setColaborador(tr.getFunId().getNome());
                }
                if (tr.getMobId() != null) {
                    if (tr.getMobId().getInstidId() != null) {
                        dto.setDireccao(tr.getMobId().getInstidId().getNome());
                    }
                    if (tr.getMobId().getSecaoId() != null) {
                        dto.setSeccao(tr.getMobId().getSecaoId().getNome());
                    }
                }
            }

            // Dados Hora Extra
            dto.setNumHorasExtras(entity.getHorasDiarias());

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }
}
