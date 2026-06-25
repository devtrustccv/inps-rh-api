package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MobilidadeMapper {

  private final EntityManager entityManager;

  public MobilidadeDTO mobilidadeDTO(MobilidadeEntity mobilidade) {

    var dto = new MobilidadeDTO();

    if (Objects.nonNull(mobilidade.getInstidId()))
      dto.setDirrecaoAntes(mobilidade.getInstidId().getNome());

    if (Objects.nonNull(mobilidade.getSecaoId()))
      dto.setSeccaoAntes(mobilidade.getSecaoId().getNome() != null ? mobilidade.getSecaoId().getNome() : "");

    if (Objects.nonNull(mobilidade.getLocalTrabId()))
      dto.setLocalTrabalhoAntes(mobilidade.getLocalTrabId().getNome());

    dto.setTipoMobilidade(mobilidade.getTipoSituacao());
    dto.setDataInicio(mobilidade.getDataInicio());
    dto.setDataFim(mobilidade.getDataFim());

    return dto;

  }

  public MobilidadeEntity toMobilidade(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var me = new MobilidadeEntity();
    me.setTipoSituacao("NOVO_CONTRATO");
    me.setObs("NOVO_CONTRATO");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setLocalTrabId(ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    me.setSecaoId(ValidationUtil.ref(entityManager, SecaoEntity.class, dc.getSeccaoId()));
    me.setInstidId(ValidationUtil.ref(entityManager, DirecaoEntity.class, dc.getDirecaoId()));
    me.setDataInicio(dc.getDataInicio());
    me.setDataFim(dc.getDataFim());
    me.setEstado(estado);
    return me;
  }

  public void toUpdateEntity(MobilidadeEntity mobilidade, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    mobilidade.setTipoSituacao("NOVO_CONTRATO");
    mobilidade.setObs("NOVO_CONTRATO");
    mobilidade.setLocalTrabId(ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    mobilidade.setSecaoId(ValidationUtil.ref(entityManager, SecaoEntity.class, dc.getSeccaoId()));
    mobilidade.setInstidId(ValidationUtil.ref(entityManager, DirecaoEntity.class, dc.getDirecaoId()));
    mobilidade.setDataInicio(dc.getDataInicio());
    mobilidade.setDataFim(dc.getDataFim());
  }

}
