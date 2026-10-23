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
import cv.inps.rh.shared.util.DateFormatter;

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

    var dataDe = dataDoFiltro(query.getDataEnvioDe(), "dataEnvioDe");
    var dataAte = dataDoFiltro(query.getDataEnvioAte(), "dataEnvioAte");
    if (dataDe != null && dataAte != null && dataAte.isBefore(dataDe))
      throw IgrpResponseStatusException.badRequest("Data Envio Até não pode ser anterior à Data Envio De.");

    Specification<NotificacaoEntity> spec = (root, criteriaQuery, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(query.getTipoNotificacao())) {
        predicates.add(cb.equal(root.get("tipoNotificacao"), query.getTipoNotificacao()));
      }

      if (dataDe != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), dataDe.atStartOfDay()));
      }

      // "Até" inclui o próprio dia: createdDate tem hora (antes "22/10 a 22/10" não trazia nada — QA TR-19).
      if (dataAte != null) {
        predicates.add(cb.lessThan(root.get("createdDate"), dataAte.plusDays(1).atStartOfDay()));
      }

      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));
      }

      if (StringUtils.hasText(query.getReferenciaName())) {
        predicates.add(cb.equal(root.get("referenciaName"), query.getReferenciaName()));
      }

      if (StringUtils.hasText(query.getReferenciaUuid())) {
        predicates.add(cb.equal(root.get("referenciaUuid"), UUID.fromString(query.getReferenciaUuid())));
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

  /** Data de um filtro: aceita só data ou data com hora (fica a parte da data); inválida → 400 (antes 500). */
  private static LocalDate dataDoFiltro(String valor, String campo) {
    if (!StringUtils.hasText(valor)) return null;
    var data = DateFormatter.stringToLocalDate(valor.trim());
    if (data == null && valor.trim().length() > 10)
      data = DateFormatter.stringToLocalDate(valor.trim().substring(0, 10));
    if (data == null)
      throw IgrpResponseStatusException.badRequest("Data inválida em '" + campo + "': " + valor);
    return data;
  }

  @Transactional(readOnly = true)
  public NotificacaoInfoDTO findById(String id) {
    // A lista devolve o id numérico (o mesmo que o "enviar" usa); aceita também o uuid.
    NotificacaoEntity entity;
    if (id != null && id.chars().allMatch(Character::isDigit) && !id.isEmpty()) {
      entity = notificacaoRepository.findByIdOrThrow(Long.parseLong(id));
    } else {
      var uuid = IdentificadorUnico.from(id).valor();
      entity = notificacaoRepository.findByUuid(uuid)
          .orElseThrow(() -> IgrpResponseStatusException.notFound("Notificação não encontrada: " + id));
    }
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
