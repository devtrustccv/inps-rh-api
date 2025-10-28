package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContratoMapper {

  private final FuncionarioMapper funcionarioMapper;
  private final ParamVinculoMapper paramVinculoMapper;
  private final ParamContratoMapper paramContratoMapper;

  // Entity -> Domain
  public Contrato toDomain(ContratoEntity entity) {
    if (entity == null) return null;

    List<Contrato> contratosFilhos = entity.getContratosFilhos() != null
        ? entity.getContratosFilhos().stream().map(this::toDomain).collect(Collectors.toList())
        : null;

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
        funcionarioMapper.toDomain(entity.getFunId()),
        paramVinculoMapper.toDomain(entity.getVinculoId()),
        paramContratoMapper.toDomain(entity.getTpContratoId()),
        contratosFilhos
    );
  }

  // Domain -> Entity
  public ContratoEntity toEntity(Contrato domain) {
    if (domain == null) return null;

    ContratoEntity entity = new ContratoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setEstado(domain.getEstado());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDuracao(domain.getDuracao());
    entity.setVersao(domain.getVersao());
    entity.setTpContrato(domain.getTpContrato());
    entity.setSituacaoLaboral(domain.getSituacaoLaboral());
    entity.setObs(domain.getObs());
    entity.setFunId(funcionarioMapper.toEntity(domain.getFuncionario()));
    entity.setVinculoId(paramVinculoMapper.toEntity(domain.getVinculo()));
    entity.setTpContratoId(paramContratoMapper.toEntity(domain.getTpContratoParam()));

    if (domain.getContratosFilhos() != null && !domain.getContratosFilhos().isEmpty()) {
      entity.setContratosFilhos(domain.getContratosFilhos().stream()
          .map(this::toEntity)
          .collect(Collectors.toList()));
    }

    return entity;
  }

  // List<Entity> -> List<Domain>
  public List<Contrato> toDomainList(List<ContratoEntity> entities) {
    if (entities == null) return null;
    return entities.stream().map(this::toDomain).collect(Collectors.toList());
  }

  // List<Domain> -> List<Entity>
  public List<ContratoEntity> toEntityList(List<Contrato> domains) {
    if (domains == null) return null;
    return domains.stream().map(this::toEntity).collect(Collectors.toList());
  }


}
