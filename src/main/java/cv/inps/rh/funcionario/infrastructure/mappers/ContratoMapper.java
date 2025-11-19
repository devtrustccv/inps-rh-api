package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ContratoListDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.domain.filters.ContratoFilter;
import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContratoMapper {

  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamContratoMapper paramContratoMapper;
  private final EntityManager entityManager;

  public ContratoEntity toEntity(Contrato domain) {
    if (domain == null) return null;

    ContratoEntity entity;

    if (domain.getId() != null && domain.getId() > 0) {
      entity = entityManager.getReference(ContratoEntity.class, domain.getId());
    } else {
      entity = new ContratoEntity();

    }
    //entity.setContratoId(entity);
    entity.setUuid(domain.getUuid().getValor());
    entity.setEstado(domain.getEstado());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDuracao(domain.getDuracao());
    entity.setVersao(domain.getVersao());
    entity.setTpContrato("afwgfwgeg");
    entity.setSituacaoLaboral(domain.getSituacaoLaboral());
    entity.setObs(domain.getObs());

    entity.setVinculoId(entityManager.getReference(
        ParamVinculoEntity.class, domain.getVinculo().getId()));
    var paramContrato = entityManager.getReference(
        ParamContratoEntity.class, domain.getTpContratoParam().getId());
    entity.setTpContratoId(paramContrato);

    if (domain.getContratosFilhos() != null && !domain.getContratosFilhos().isEmpty()) {
      List<ContratoEntity> filhos = domain.getContratosFilhos().stream()
          .map(this::toInternalEntity)
          .peek(f -> f.setContratoId(entity)) // cada filho aponta para o mestre
          .collect(Collectors.toList());
      entity.setContratosFilhos(filhos);
    }

    return entity;
  }

  private ContratoEntity toInternalEntity(Contrato domain) {
    if (domain == null) return null;

    ContratoEntity entity = new ContratoEntity();
    entity.setId(domain.getId() != null && domain.getId() > 0 ? domain.getId() : null);
    entity.setUuid(domain.getUuid().getValor());
    entity.setEstado(domain.getEstado());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDuracao(domain.getDuracao());
    entity.setVersao(domain.getVersao());
    entity.setTpContrato(domain.getTpContrato());
    entity.setSituacaoLaboral(domain.getSituacaoLaboral());
    entity.setObs(domain.getObs());

    entity.setVinculoId(entityManager.getReference(
        ParamVinculoEntity.class, domain.getVinculo().getId()));
    entity.setTpContratoId(entityManager.getReference(
        ParamContratoEntity.class, domain.getTpContratoParam().getId()));

    return entity;
  }

  public Contrato toDomain(ContratoEntity entity) {
    if (entity == null) return null;

    List<Contrato> filhos = entity.getContratosFilhos().stream()
        .map(filho -> Contrato.rebuild(
            filho.getId(),
            filho.getUuid(),
            filho.getEstado(),
            filho.getDataInicio(),
            filho.getDataFim(),
            filho.getDuracao(),
            filho.getVersao(),
            filho.getTpContrato(),
            filho.getSituacaoLaboral(),
            filho.getObs(),
            paramVinculoMapper.toDomain(filho.getVinculoId()),
            paramContratoMapper.toDomain(filho.getTpContratoId()),
            null, // contratosFilhos já tratados,
            null,
            null
        ))
        .toList();

    return Contrato.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getEstado(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getDuracao(),
        entity.getVersao(),
        entity.getTpContrato(),
        entity.getSituacaoLaboral(),
        entity.getObs(),
        paramVinculoMapper.toDomain(entity.getVinculoId()),
        paramContratoMapper.toDomain(entity.getTpContratoId()),
        filhos,
        entity.getFunId().getId(),
        entity.getFunId().getUuid()
    );
  }

  public ContratoListDTO toDTO(Contrato contrato) {
    if (contrato == null) return null;

    var dto = new ContratoListDTO();
    dto.setId(contrato.getId());
    dto.setUuid(contrato.getUuid() != null ? contrato.getUuid().getValor().toString() : null);
    dto.setFuncionarioId(contrato.getIdFuncionario());
    dto.setUuidFuncionario(contrato.getUuidFuncionario() != null ? contrato.getUuidFuncionario().toString() : null);
    dto.setSituacao(contrato.getSituacaoLaboral());
    dto.setTipoVinculo(contrato.getVinculo() != null ? contrato.getVinculo().getNome() : null);
    dto.setDataInicio(contrato.getDataInicio() != null ? DateFormatter.localDateToString(contrato.getDataInicio()) : null);
    dto.setDataFim(contrato.getDataFim() != null ? DateFormatter.localDateToString(contrato.getDataFim()) : null);
    dto.setDuracao(contrato.getDuracao() != null ? contrato.getDuracao().toString() : null);
    dto.setEstado(contrato.getEstado() != null ? contrato.getEstado().name() : null);
    dto.setEstadoDesc(contrato.getEstado() != null ? contrato.getEstado().getDescription() : null);

    return dto;
  }

  public ContratoFilter toFilterDomain(Long vinculo,
                                       String idFuncionario,
                                       Integer pageNumber,
                                       Integer pageSize ) {

    return ContratoFilter.builder()
        .idFuncionario(IdentificadorUnico.from(idFuncionario))
        .vinculo(vinculo)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }

  public DadosContratuaisRespDTO toRespDTO(Contrato contrato) {
    if (contrato == null) return null;

    var dto = new cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO();
    dto.setTipoContratoId(contrato.getTpContratoParam() != null ? contrato.getTpContratoParam().getId() : null);
    dto.setTipoContratoDesc(contrato.getTpContratoParam() != null ? contrato.getTpContratoParam().getNome() : null);
    dto.setTipoVinculoLaboralId(contrato.getVinculo() != null ? contrato.getVinculo().getId() : null);
    dto.setTipoVinculoLaboralDesc(contrato.getVinculo() != null ? contrato.getVinculo().getNome() : null);
    dto.setDataInicio(contrato.getDataInicio());
    dto.setDataFim(contrato.getDataFim());
    dto.setDuracaoMeses(contrato.getDuracao());
    return dto;
  }

  public DadosContratuaisRespDTO toRespDTO(TiposRelacionamento tr) {
    var dto = toRespDTO(tr.getContrato());
    if (dto == null) dto = new DadosContratuaisRespDTO();

    dto.setCargoPosicaoId(tr.getCargo() != null ? tr.getCargo().getId() : null);
    dto.setCargoPosicaoDesc(tr.getCargo() != null ? tr.getCargo().getNome() : null);
    dto.setDirecaoId(tr.getInstituicao() != null ? tr.getInstituicao().getId() : null);
    dto.setDirecaoDesc(tr.getInstituicao() != null ? tr.getInstituicao().getNome() : null);
    dto.setSeccaoId(tr.getSeccao() != null ? tr.getSeccao().getId() : null);
    dto.setSeccaoDesc(tr.getSeccao() != null ? tr.getSeccao().getNome() : null);
    dto.setCarreiraId(tr.getCarrPcc() != null ? tr.getCarrPcc().getId() : null);
    dto.setCarreiraDesc(tr.getCarrPcc() != null ? tr.getCarrPcc().getNome() : null);
    dto.setCategoriaId(tr.getCategoria() != null ? tr.getCategoria().getId() : null);
    dto.setCategoriaDesc(tr.getCategoria() != null ? tr.getCategoria().getNome() : null);
    dto.setEscalaoReferenciaId(tr.getEscalao() != null ? tr.getEscalao().getId() : null);
    dto.setEscalaoReferenciaDesc(tr.getEscalao() != null ? tr.getEscalao().getEscalao() : null);
    dto.setLocalTrabalhoId(tr.getLocTrab() != null ? tr.getLocTrab().getId() : null);
    dto.setLocalTrabalhoDesc(tr.getLocTrab() != null ? tr.getLocTrab().getNome() : null);
    dto.setSalario(tr.getSalario());
    dto.setMoeda(tr.getMoeda());
    dto.setRegimeTrabalho(tr.getRegime());

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
    c.setTpContratoId(entityManager.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return c;
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




}
