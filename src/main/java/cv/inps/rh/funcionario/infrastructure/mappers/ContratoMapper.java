package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamContratoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamVinculoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    ContratoEntity entity = new ContratoEntity();
    if (domain.getId() != null && domain.getId() > 0) {
      entity.setId(domain.getId());
    }

    entity.setUuid(domain.getUuid().getValor());
    entity.setEstado(domain.getEstado());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setDuracao(domain.getDuracao());
    entity.setVersao(domain.getVersao());

    entity.setSituacaoLaboral(domain.getSituacaoLaboral());
    entity.setObs(domain.getObs());

    entity.setVinculoId(entityManager.getReference(
        ParamVinculoEntity.class,
        domain.getVinculo().getId()
    ));

    var tipoContrato = entityManager.getReference(
        ParamContratoEntity.class,
        domain.getTpContratoParam().getId());

   entity.setTpContratoId(entityManager.getReference(
        ParamContratoEntity.class,
        domain.getTpContratoParam().getId()
    ));

    entity.setTpContrato(tipoContrato.getNome());

    // 🔹 Contratos filhos
    if (domain.getContratosFilhos() != null) {
      List<ContratoEntity> filhos = domain.getContratosFilhos().stream()
          .map(this::toInternalEntity)
          .peek(f -> f.setContratoId(entity)) // cada filho aponta para o mestre
          .collect(Collectors.toList());
      entity.setContratosFilhos(filhos);
    }

    // 🔹 Contrato mestre (self-FK na primeira versão será preenchido automaticamente pelo Hibernate)
    if (domain.getContratoMestre() != null) {
      entity.setContratoId(entityManager.getReference(
          ContratoEntity.class,
          domain.getContratoMestre().getId()
      ));
    } else {
      // primeira versão → self-FK
      entity.setContratoId(entity); // agora Hibernate já conhece o ID
    }

    return entity;
  }

  private ContratoEntity toInternalEntity(Contrato domain) {
    if (domain == null) return null;

    ContratoEntity entity = new ContratoEntity();
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
        ParamVinculoEntity.class,
        domain.getVinculo().getId()
    ));
    entity.setTpContratoId(entityManager.getReference(
        ParamContratoEntity.class,
        domain.getTpContratoParam().getId()
    ));

    return entity;
  }

  public Contrato toDomain(ContratoEntity entity) {
    if (entity == null) return null;

    List<Contrato> filhos = entity.getContratosFilhos() != null
        ? entity.getContratosFilhos().stream()
        .map(this::toDomain)
        .collect(Collectors.toList())
        : new ArrayList<>();

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
        null, // mestre será reconstruído automaticamente se necessário
        filhos
    );
  }

}
