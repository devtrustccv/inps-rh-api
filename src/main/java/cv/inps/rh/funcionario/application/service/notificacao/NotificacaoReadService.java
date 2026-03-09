package cv.inps.rh.funcionario.application.service.notificacao;

import cv.inps.rh.funcionario.application.queries.ListaNotificacoesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.NotificacaoMapper;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.application.dto.WrapperListaNotificacoesDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacaoReadService {

  private final NotificacaoEntityRepository notificacaoRepository;
  private final NotificacaoMapper notificacaoMapper;

  @Transactional(readOnly = true)
  public WrapperListaNotificacoesDTO findAll(ListaNotificacoesQuery query) {

    Pageable pageable = PageRequest.of(
        Integer.parseInt(query.getPageNumber()),
        Integer.parseInt(query.getPageSize()),
        Sort.by(Sort.Direction.DESC, "createdDate"));

    Specification<NotificacaoEntity> spec = (root, criteriaQuery, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(query.getTipoNotificacao())) {
        predicates.add(cb.equal(root.get("tipoNotificacao"), query.getTipoNotificacao()));
      }

      if (StringUtils.hasText(query.getDataEnvioDe())) {
        LocalDate dataDe = LocalDate.parse(query.getDataEnvioDe(), DateTimeFormatter.ISO_LOCAL_DATE);
        predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), dataDe));
      }

      if (StringUtils.hasText(query.getDataEnvioAte())) {
        LocalDate dataAte = LocalDate.parse(query.getDataEnvioAte(), DateTimeFormatter.ISO_LOCAL_DATE);
        predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), dataAte));
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

  @Transactional(readOnly = true)
  public NotificacaoInfoDTO findById(String id) {
    var uuid = UUID.fromString(id);
    NotificacaoEntity entity = notificacaoRepository.findByUuid((uuid))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Notificacão not found with id"+uuid
        ));
    return notificacaoMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public NotificacaoInfoDTO findNotificacaoByDeclaracaoId(String declaracaoId) {

    var uuid = IdentificadorUnico.from(declaracaoId).valor();
    NotificacaoEntity entity = notificacaoRepository
        .findByReferenciaNameAndReferenciaUuid(TableName.RH_T_DECLARACAO.name(), uuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Notificação não encontrada para a declaração com id: " + declaracaoId));
    return notificacaoMapper.toDto(entity);
  }
}
