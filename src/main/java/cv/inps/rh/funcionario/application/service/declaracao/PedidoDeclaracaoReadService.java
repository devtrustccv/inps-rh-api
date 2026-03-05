package cv.inps.rh.funcionario.application.service.declaracao;

import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetPedidoDeclaracoesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.PedidoDeclaracaoMapper;
import cv.inps.rh.funcionario.application.dto.WrapperListaPedidoDeclaracaoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoDeclaracaoReadService {

    private final DeclaracaoEntityRepository declaracaoRepository;
    private final PedidoDeclaracaoMapper pedidoDeclaracaoMapper;

    public WrapperListaPedidoDeclaracaoDTO findAll(GetPedidoDeclaracoesQuery query) {
        Pageable pageable = PageRequest.of(
                Integer.parseInt(query.getPageNumber()),
                Integer.parseInt(query.getPageSize()),
                Sort.by(Sort.Direction.DESC, "dataPedido"));

        Specification<DeclaracaoEntity> spec = (root, criteriaQuery, cb) -> {
            // Evitar N+1 fetches
            root.fetch("pedidoId", JoinType.LEFT).fetch("funId", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(query.getTipoDeclaracao())) {
                predicates.add(cb.equal(root.get("tipoDeclaracao"), query.getTipoDeclaracao()));
            }

            if (StringUtils.hasText(query.getDataPedidoDe())) {
                LocalDate dataDe = LocalDate.parse(query.getDataPedidoDe(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataPedido"), dataDe));
            }

            if (StringUtils.hasText(query.getDataPedidoAte())) {
                LocalDate dataAte = LocalDate.parse(query.getDataPedidoAte(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicates.add(cb.lessThanOrEqualTo(root.get("dataPedido"), dataAte));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<DeclaracaoEntity> page = declaracaoRepository.findAll(spec, pageable);

        WrapperListaPedidoDeclaracaoDTO response = new WrapperListaPedidoDeclaracaoDTO();
        response.setContent(page.getContent().stream().map(pedidoDeclaracaoMapper::toDto).collect(Collectors.toList()));
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());

        return response;
    }

    public PedidoDeclaracaoResponseDTO findById(String id) {
        DeclaracaoEntity entity = declaracaoRepository.findByIdOrThrow(Long.parseLong(id));
        return pedidoDeclaracaoMapper.toResponseDto(entity);
    }
}
