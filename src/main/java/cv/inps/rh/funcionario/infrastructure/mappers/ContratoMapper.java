package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ContratoListDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoRespDTO;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVContratoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContratoMapper {

  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamContratoMapper paramContratoMapper;
  private final EntityManager entityManager;


  public ContratoListDTO toDTO(RhVContratoEntity v) {
    if (v == null) return null;

    var dto = new ContratoListDTO();
    dto.setId(v.getContratoId());
    dto.setUuid(v.getContratoUuid() != null ? v.getContratoUuid().toString() : null);
    dto.setFuncionarioId(v.getFunId());
    dto.setUuidFuncionario(v.getFunUuid() != null ? v.getFunUuid().toString() : null);
    dto.setSituacao(v.getTipoSituacao());
    dto.setTipoContrato(v.getTipoContrato());
    dto.setTipoVinculo(v.getVinculo());
    dto.setDataInicio(v.getDataInicio() != null ? DateFormatter.localDateToString(v.getDataInicio()) : null);
    dto.setDataFim(v.getDataFim() != null ? DateFormatter.localDateToString(v.getDataFim()) : null);
    dto.setDuracao(v.getDuracao() != null ? v.getDuracao().toString() : null);
    dto.setEstado(v.getEstado());
    dto.setEstadoDesc(Estado.fromCode(v.getEstado()).map(Estado::getDescription).orElse(null));
    dto.setVersao(v.getVersao());
    dto.setInicial(Integer.valueOf(1).equals(v.getVersao()));
    dto.setAtual(Integer.valueOf(1).equals(v.getEstActAdm()));
    dto.setRenovavel(Integer.valueOf(1).equals(v.getFlgRenovavel()));
    dto.setProcessamento(v.getProcessamento() != null && v.getProcessamento() > 0);

    return dto;
  }

  public ContratoEntity toContrato(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var c = new ContratoEntity();
    c.setEstado(estado);
    c.setDataInicio(dc.getDataInicio());
    c.setDataFim(dc.getDataFim());
    c.setDuracao(dc.getDuracaoMeses());
    c.setTipoSituacao("INICIO");
    c.setObs("NOVO_CONTRATO");
    c.setUuid(IdentificadorUnico.create().valor());
    c.setTpContratoId(ValidationUtil.ref(entityManager, ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(ValidationUtil.ref(entityManager, ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return c;
  }
  public ContratoEntity toRenovarContrato(RenovarContratoReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var c = new ContratoEntity();
    c.setEstado(estado);
    c.setDataInicio(dc.getDataInicio());
    c.setDataFim(dc.getDataFim());
    c.setDuracao(dc.getDuracaoMeses());
    c.setTipoSituacao("RENOVACAO");
    c.setObs("RENOVACAO");
    c.setUuid(IdentificadorUnico.create().valor());
    c.setTpContratoId(ValidationUtil.ref(entityManager, ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(ValidationUtil.ref(entityManager, ParamVinculoEntity.class, dc.getTipoVinculoId()));
    return c;
  }

  public RenovarContratoReqDTO toRenovacaoContratoReqDTO(ContratoEntity contratoEntity) {
    var renovacaoContrato = new RenovarContratoReqDTO();
    renovacaoContrato.setDataInicio(contratoEntity.getDataInicio());
    renovacaoContrato.setDataFim(contratoEntity.getDataFim());
    renovacaoContrato.setDuracaoMeses(contratoEntity.getDuracao());
    renovacaoContrato.setTipoContratoId(contratoEntity.getTpContratoId().getId());
    renovacaoContrato.setTipoVinculoId(contratoEntity.getVinculoId().getId());
    return renovacaoContrato;
  }

  public RenovarContratoRespDTO toRenovacaoContratoRespDTO(ContratoEntity contratoEntity) {
    if (contratoEntity == null) return null;
    var resp = new RenovarContratoRespDTO();
    resp.setDataInicio(contratoEntity.getDataInicio());
    resp.setDataFim(contratoEntity.getDataFim());
    resp.setDuracaoMeses(contratoEntity.getDuracao());
    if (contratoEntity.getTpContratoId() != null) {
      resp.setTipoContratoId(contratoEntity.getTpContratoId().getId());
      resp.setTipoContratoDesc(contratoEntity.getTpContratoId().getNome());
    }
    if (contratoEntity.getVinculoId() != null) {
      resp.setTipoVinculoId(contratoEntity.getVinculoId().getId());
      resp.setTipoVinculoDesc(contratoEntity.getVinculoId().getNome());
    }
    return resp;
  }

  public ContratoEntity toUpdateEntity(ContratoEntity entity, DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    entity.setDuracao(dc.getDuracaoMeses());
    entity.setTpContratoId(ValidationUtil.ref(entityManager, ParamContratoEntity.class, dc.getTipoContratoId()));
    entity.setVinculoId(ValidationUtil.ref(entityManager, ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return entity;
  }

  public ContratoEntity toUpdateEntity(ContratoEntity entity, RenovarContratoReqDTO dc) {
    if (dc == null) return null;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    entity.setDuracao(dc.getDuracaoMeses());
    entity.setTpContratoId(ValidationUtil.ref(entityManager, ParamContratoEntity.class, dc.getTipoContratoId()));
    entity.setVinculoId(ValidationUtil.ref(entityManager, ParamVinculoEntity.class, dc.getTipoVinculoId()));
    return entity;
  }




}
