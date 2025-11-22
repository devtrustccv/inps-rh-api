package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MobilidadeMapper {

  private final EntityManager entityManager;

  public MobilidadeDTO mobilidadeDTO(MobilidadeEntity mobilidade) {

    var dto = new MobilidadeDTO();
    dto.setDirrecaoAntes(mobilidade.getInstidId().getNome());
    dto.setSeccaoAntes(mobilidade.getSecaoId().getNome() != null ? mobilidade.getSecaoId().getNome() : "");
    dto.setLocalTrabalhoAntes(mobilidade.getLocalTrabId().getNome());

    return dto;

  }

  public MobilidadeEntity toMobilidade(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var me = new MobilidadeEntity();
    me.setTipoSituacao("NOVO_CONTRATO");
    me.setObs("NOVO_CONTRATO");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setLocalTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    me.setSecaoId(entityManager.getReference(SecaoEntity.class, dc.getSeccaoId()));
    me.setInstidId(entityManager.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
    me.setDataInicio(dc.getDataInicio());
    me.setDataFim(dc.getDataFim());
    me.setEstado(estado);
    return me;
  }

  public void toUpdateEntity(MobilidadeEntity mobilidade, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    mobilidade.setTipoSituacao("NOVO_CONTRATO");
    mobilidade.setObs("NOVO_CONTRATO");
    mobilidade.setLocalTrabId(entityManager.getReference(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    mobilidade.setSecaoId(entityManager.getReference(SecaoEntity.class, dc.getSeccaoId()));
    mobilidade.setInstidId(entityManager.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
    mobilidade.setDataInicio(dc.getDataInicio());
    mobilidade.setDataFim(dc.getDataFim());
  }

}
