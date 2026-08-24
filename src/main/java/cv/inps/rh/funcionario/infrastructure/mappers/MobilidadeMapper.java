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

    if (Objects.nonNull(mobilidade.getInstidId())) {
      dto.setDirrecaoAntes(mobilidade.getInstidId().getNome());
      dto.setDirecaoAntesId(mobilidade.getInstidId().getId());
    }

    if (Objects.nonNull(mobilidade.getSecaoId()))
      dto.setSeccaoAntes(mobilidade.getSecaoId().getNome() != null ? mobilidade.getSecaoId().getNome() : "");

    if (Objects.nonNull(mobilidade.getLocalTrabId()))
      dto.setLocalTrabalhoAntes(mobilidade.getLocalTrabId().getNome());

    dto.setTipoMobilidade(mobilidade.getTipoSituacao());
    dto.setDataInicio(mobilidade.getDataInicio());
    dto.setDataFim(mobilidade.getDataFim());

    return dto;

  }

  /**
   * Detalhe/editar de uma mobilidade específica. Spec DOSSIÊ: o INSTIT_ID gravado é a
   * "Direção (Depois)"; o "Antes" é a direção do vínculo anterior (só display).
   *
   * @param atual    a mobilidade que está a ser vista/editada (fornece o "Depois")
   * @param anterior a mobilidade do vínculo anterior (fornece o "Antes"); pode ser null
   */
  public MobilidadeDTO mobilidadeDetalheDTO(MobilidadeEntity atual, MobilidadeEntity anterior) {
    var dto = new MobilidadeDTO();

    // Depois = valores gravados nesta mobilidade. O id alimenta os selects do formulário; a
    // descrição existe para o ecrã de validação, que é só leitura e não carrega as listas.
    if (Objects.nonNull(atual.getInstidId())) {
      dto.setDirecaoDepois(atual.getInstidId().getId());
      dto.setDirecaoDepoisDesc(atual.getInstidId().getNome());
    }
    if (Objects.nonNull(atual.getSecaoId())) {
      dto.setSeccaoDepois(atual.getSecaoId().getId());
      dto.setSeccaoDepoisDesc(atual.getSecaoId().getNome());
    }
    if (Objects.nonNull(atual.getLocalTrabId())) {
      dto.setLocalTrabalhoDepois(atual.getLocalTrabId().getId());
      dto.setLocalTrabalhoDepoisDesc(atual.getLocalTrabId().getNome());
    }

    // Antes = direção/secção/local do vínculo anterior (display)
    if (anterior != null) {
      if (Objects.nonNull(anterior.getInstidId())) {
        dto.setDirrecaoAntes(anterior.getInstidId().getNome());
        dto.setDirecaoAntesId(anterior.getInstidId().getId());
      }
      if (Objects.nonNull(anterior.getSecaoId()))
        dto.setSeccaoAntes(anterior.getSecaoId().getNome());
      if (Objects.nonNull(anterior.getLocalTrabId()))
        dto.setLocalTrabalhoAntes(anterior.getLocalTrabId().getNome());
    }

    dto.setTipoMobilidade(atual.getTipoSituacao());
    dto.setDataInicio(atual.getDataInicio());
    dto.setDataFim(atual.getDataFim());
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
