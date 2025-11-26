package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ContratoListDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContratoMapper {

  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamContratoMapper paramContratoMapper;
  private final EntityManager entityManager;


  public ContratoListDTO toDTO(ContratoEntity contrato) {
    if (contrato == null) return null;

    var dto = new ContratoListDTO();
    dto.setId(contrato.getId());
    dto.setUuid(contrato.getUuid() != null ? contrato.getUuid().toString() : null);
    dto.setFuncionarioId(contrato.getFunId().getId());
    dto.setUuidFuncionario(contrato.getFunId() != null ? contrato.getFunId().getUuid().toString() : null);
    dto.setSituacao(contrato.getSituacaoLaboral());
    dto.setTipoVinculo(contrato.getVinculoId() != null ? contrato.getVinculoId().getNome() : null);
    dto.setDataInicio(contrato.getDataInicio() != null ? DateFormatter.localDateToString(contrato.getDataInicio()) : null);
    dto.setDataFim(contrato.getDataFim() != null ? DateFormatter.localDateToString(contrato.getDataFim()) : null);
    dto.setDuracao(contrato.getDuracao() != null ? contrato.getDuracao().toString() : null);
    dto.setEstado(contrato.getEstado() != null ? contrato.getEstado().name() : null);
    dto.setEstadoDesc(contrato.getEstado() != null ? contrato.getEstado().getDescription() : null);
    dto.setVersao(contrato.getVersao());
    dto.setInicial(contrato.getVersao() == 1);
    dto.setAtual(contrato.getEstado() == Estado.A);

    return dto;
  }

  public ContratoEntity toContrato(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var c = new ContratoEntity();
    c.setEstado(estado);
    c.setDataInicio(dc.getDataInicio());
    c.setDataFim(dc.getDataFim());
    c.setDuracao(dc.getDuracaoMeses());
    c.setTpContrato("NOVO_CONTRATO");
    c.setSituacaoLaboral("INICIO");
    c.setObs("NOVO_CONTRATO");
    c.setUuid(IdentificadorUnico.create().getValor());
    c.setTpContratoId(entityManager.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return c;
  }
  public ContratoEntity toRenovarContrato(RenovarContratoReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var c = new ContratoEntity();
    c.setEstado(estado);
    c.setDataInicio(dc.getDataInicio());
    c.setDataFim(dc.getDataFim());
    c.setDuracao(dc.getDuracaoMeses());
    c.setTpContrato("RENOVACAO_CONTRATO");
    c.setSituacaoLaboral("RENOVACAO_CONTRATO");
    c.setObs("RENOVACAO_CONTRATO");
    c.setUuid(IdentificadorUnico.create().getValor());
    c.setTpContratoId(entityManager.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoId()));
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

  public ContratoEntity toUpdateEntity(ContratoEntity entity, DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    entity.setDuracao(dc.getDuracaoMeses());
    entity.setTpContratoId(entityManager.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    entity.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return entity;
  }

  public ContratoEntity toUpdateEntity(ContratoEntity entity, RenovarContratoReqDTO dc) {
    if (dc == null) return null;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    entity.setDuracao(dc.getDuracaoMeses());
    entity.setTpContratoId(entityManager.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    entity.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoId()));
    return entity;
  }




}
