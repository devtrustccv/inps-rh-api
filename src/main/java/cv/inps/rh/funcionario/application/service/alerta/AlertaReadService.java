package cv.inps.rh.funcionario.application.service.alerta;

import cv.inps.rh.funcionario.application.dto.AlertaDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListAlertaDTO;
import cv.inps.rh.funcionario.application.queries.GetListAlertaQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.AlertaMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.NotificacaoMapper;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AlertaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertaReadService {

    private final AlertaEntityRepository alertaRepository;
    private final NotificacaoEntityRepository notificacaoRepository;
    private final AlertaMapper alertaMapper;
    private final NotificacaoMapper notificacaoMapper;

    public WrapperListAlertaDTO findAll(GetListAlertaQuery query) {

        PageRequest pageRequest = PageRequest.of(Integer.parseInt(query.getPageNumber()), Integer.parseInt(query.getPageSize()));

        Specification<AlertaEntity> spec = (root, q, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(query.getReferencia())) {
                predicates.add(cb.like(root.get("referenciaName"), "%" + query.getReferencia() + "%"));
            }
            if (StringUtils.hasText(query.getTipoAlerta())) {
                predicates.add(cb.equal(root.get("tipoAlerta"), query.getTipoAlerta()));
            }
            if (StringUtils.hasText(query.getEstado())) {
                predicates.add(cb.equal(root.get("estado"), query.getEstado()));
            }
            if (StringUtils.hasText(query.getDataRegistoDe())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.parse(query.getDataRegistoDe(), DateTimeFormatter.ISO_LOCAL_DATE)));
            }
            if (StringUtils.hasText(query.getDataRegistoAte())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdDate").as(LocalDate.class), LocalDate.parse(query.getDataRegistoAte(), DateTimeFormatter.ISO_LOCAL_DATE)));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<AlertaEntity> page = alertaRepository.findAll(spec, pageRequest);

        WrapperListAlertaDTO result = new WrapperListAlertaDTO();
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setContent(alertaMapper.toDtoList(page.getContent()));

        return result;
    }

    public AlertaDTO findById(String id) {

        var entity = getById(id);
        return alertaMapper.toDto(entity);
    }

    private AlertaEntity getById(String id){
      var uuid = UUID.fromString(id);

      AlertaEntity entity = alertaRepository.findByUuid(uuid).orElseThrow(
          () -> IgrpResponseStatusException.notFound("Alerta not found for id: " + id)
      );

      return entity;
    }

    public List<NotificacaoInfoDTO> findNotificacoesByAlertaId(String alertaId) {
      var entity = getById(alertaId);
         List<NotificacaoEntity> notificacoes = notificacaoRepository.findByAlertaId(entity);
        return notificacaoMapper.toDtoList(notificacoes);
    }
}
