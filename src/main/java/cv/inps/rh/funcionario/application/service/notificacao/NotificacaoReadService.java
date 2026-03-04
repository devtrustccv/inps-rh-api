package cv.inps.rh.funcionario.application.service.notificacao;

import cv.inps.rh.funcionario.application.dto.NotificacaoResponseDTO;
import cv.inps.rh.funcionario.application.queries.ListaNotificacoesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.NotificacaoMapper;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.application.dto.WrapperListaNotificacoesDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
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
public class NotificacaoReadService {

    private final NotificacaoEntityRepository notificacaoRepository;
    private final NotificacaoMapper notificacaoMapper;

    public WrapperListaNotificacoesDTO findAll(ListaNotificacoesQuery query) {

        Pageable pageable = PageRequest.of(
                Integer.parseInt(query.getPageNumber()),
                Integer.parseInt(query.getPageSize()),
                Sort.by(Sort.Direction.DESC, "dataRegisto"));

        Specification<NotificacaoEntity> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(query.getTipoNotificacao())) {
                predicates.add(cb.equal(root.get("tipoNotificacao"), query.getTipoNotificacao()));
            }

            if (StringUtils.hasText(query.getDataEnvioDe())) {
                LocalDate dataDe = LocalDate.parse(query.getDataEnvioDe(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataRegisto"), dataDe));
            }

            if (StringUtils.hasText(query.getDataEnvioAte())) {
                LocalDate dataAte = LocalDate.parse(query.getDataEnvioAte(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicates.add(cb.lessThanOrEqualTo(root.get("dataRegisto"), dataAte));
            }

            if (StringUtils.hasText(query.getEstado())) {
                predicates.add(cb.equal(root.get("estado"), query.getEstado()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<NotificacaoEntity> page = notificacaoRepository.findAll(spec, pageable);

        WrapperListaNotificacoesDTO response = new WrapperListaNotificacoesDTO();
        response.setContent(page.getContent().stream().map(notificacaoMapper::toDto).collect(Collectors.toList()));
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());

        return response;
    }

    public NotificacaoInfoDTO findById(String id) {
        NotificacaoEntity entity = notificacaoRepository.findByIdOrThrow(Long.parseLong(id));
        return notificacaoMapper.toDto(entity);
    }

    public NotificacaoResponseDTO findByDeclaracaoId(String declaracaoId) {
        NotificacaoEntity entity = notificacaoRepository
                .findByReferenciaNameAndReferenciaId("RH_T_DECLARACAO", Long.parseLong(declaracaoId))
                .orElseThrow(() -> IgrpResponseStatusException.badRequest(
                    "Notificação não encontrada para a declaração com id: " + declaracaoId));
        return notificacaoMapper.toResponseDto(entity);
    }
}
