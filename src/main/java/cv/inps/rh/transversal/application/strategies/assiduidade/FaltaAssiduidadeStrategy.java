package cv.inps.rh.transversal.application.strategies.assiduidade;

import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
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

@Component("faltaAssiduidadeStrategy")
@RequiredArgsConstructor
public class FaltaAssiduidadeStrategy implements AssiduidadeStrategy {

    private final FaltaEntityRepository faltaRepository;

    @Override
    public Page<AssiduidadeRowDTO> filtrar(RelatorioAssiduidadeQuery query, Pageable pageable) {

        Specification<FaltaEntity> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join com Sintese para Data e Funcionario
            Join<FaltaEntity, AssiduidadeSinteseDiarioEntity> sinteseJoin = root.join("sinteseDiarioId", JoinType.INNER);
            Join<AssiduidadeSinteseDiarioEntity, FuncionarioEntity> funcionarioJoin = sinteseJoin.join("funcionarioId", JoinType.INNER);

            // Join com TiposRelacionamento para obter Direção/Secção
            Join<FuncionarioEntity, TiposRelacionamentoEntity> relJoin = funcionarioJoin.join("tiposrelacionamentos", JoinType.LEFT);
            predicates.add(cb.equal(relJoin.get("estActAdm"), 1));

            Join<TiposRelacionamentoEntity, MobilidadeEntity> mobJoin = relJoin.join("mobId", JoinType.LEFT);
            Join<MobilidadeEntity, InstituicaoEntity> instJoin = mobJoin.join("instidId", JoinType.LEFT); // Direção
            Join<MobilidadeEntity, SecaoEntity> secaoJoin = mobJoin.join("secaoId", JoinType.LEFT); // Secção

            // Filtros de Data (Baseado na data da síntese)
            if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim())) {
                LocalDate inicio = DateFormatter.stringToLocalDate(query.getDataInicio());
                LocalDate fim = DateFormatter.stringToLocalDate(query.getDataFim());

                predicates.add(cb.between(sinteseJoin.get("data"), inicio, fim));
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

        Page<FaltaEntity> pageResult = faltaRepository.findAll(spec, pageable);

        List<AssiduidadeRowDTO> dtos = pageResult.getContent().stream().map(entity -> {
            AssiduidadeRowDTO dto = new AssiduidadeRowDTO();

            FuncionarioEntity fun = entity.getSinteseDiarioId().getFuncionarioId();
            if (fun != null) {
                dto.setColaborador(fun.getNome());

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

            // Dados de Falta
            // Assumindo que cada registro de FaltaEntity conta como 1 ou usar valor da síntese
            if (entity.getSinteseDiarioId() != null && entity.getSinteseDiarioId().getFalta() != null) {
                dto.setNumFaltas(entity.getSinteseDiarioId().getFalta());
            } else {
                dto.setNumFaltas(1); // Default
            }

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
    }
}
