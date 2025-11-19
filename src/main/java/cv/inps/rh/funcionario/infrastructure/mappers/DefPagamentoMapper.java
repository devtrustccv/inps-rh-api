package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.PagamentosDescontoListDTO;
import cv.inps.rh.funcionario.domain.filters.PagamentoDescontoFilter;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DefPagamentoMapper {

  private final ContratoMapper contratoMapper;
  private final TiposRelacionamentoMapper tiprelMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;

  public DefPagamento toDomain(DefPagamentoEntity entity) {
    if (entity == null) return null;

    return DefPagamento.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getValor(),
        tipoMovimentoMapper.toDomain(entity.getTmId()),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado(),
        entity.getObs()
    );
  }

  public DefPagamentoEntity toEntity(DefPagamento domain) {
    if (domain == null) return null;

    DefPagamentoEntity entity = new DefPagamentoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setValor(domain.getValor());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());


    if (domain.getTipoMovimento() != null) {
      entity.setTmId(entityManager.getReference(
          TipoMovimentoEntity.class,
          domain.getTipoMovimento().getId()
      ));
    }

    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());

    return entity;
  }

  public PagamentoDescontoFilter toFilterDomain(String estado,
                                                String dataInicio,
                                                String dataFim,
                                                Integer pageNumber,
                                                Integer pageSize) {

    return PagamentoDescontoFilter.builder()
        .estado(StringUtils.hasText(estado) ? Estado.fromCodeOrThrow(estado) : null)
        .dataInicio(StringUtils.hasText(dataInicio) ? DateFormatter.stringToLocalDate(dataInicio) : null)
        .dataFim(StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDate(dataFim) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();

  }

  public PagamentosDescontoListDTO toDTO(DefPagamento domain) {
    if (domain == null) return null;

    var dto = new PagamentosDescontoListDTO();
    dto.setId(domain.getId() != null ? domain.getId().toString() : null);
    dto.setUuid(domain.getUuid() != null ? domain.getUuid().getValor().toString() : null);
    dto.setMovimento(domain.getTipoMovimento() != null ? domain.getTipoMovimento().getDescricao() : null);
    dto.setValor(domain.getValor() != null ? domain.getValor().toPlainString() : null);
    dto.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);
    dto.setEstadoDesc(domain.getEstado() != null ? domain.getEstado().getDescription() : null);
    dto.setDataInicio(domain.getDataInicio() != null ? domain.getDataInicio().toString() : null);
    dto.setDataFim(domain.getDataFim() != null ? domain.getDataFim().toString() : null);
    dto.setUltimoProc(domain.getDataInicio() != null ? domain.getDataInicio().toString() : null);

    return dto;
  }

  public java.util.List<DefPagamentoEntity> syncPagamentos(
      java.util.List<DefPagamentoEntity> existingList,
      java.util.List<cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO> newList) {
    if (newList == null) return existingList;
    for (cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO dto : newList) {
      DefPagamentoEntity found = null;
      if (dto.getId() != null) {
        for (DefPagamentoEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        if (dto.getTipoEncargoId() != null) {
          found.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoEncargoId()));
        }
        found.setValor(dto.getValor());
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataFim());
        found.setObs(dto.getObservacoes());
      } else {
        DefPagamentoEntity novo = new DefPagamentoEntity();
        if (dto.getTipoEncargoId() != null) {
          novo.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoEncargoId()));
        }
        novo.setValor(dto.getValor());
        novo.setDataInicio(dto.getDataInicio());
        novo.setDataFim(dto.getDataFim());
        novo.setObs(dto.getObservacoes());
        novo.setEstado(Estado.P);
        existingList.add(novo);
      }
    }
    for (DefPagamentoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public DefPagamentoEntity toDefPagamento(EncargosDescontosReqDTO e, FuncionarioEntity fun, Estado estado) {
    if (e == null) return null;
    var dp = new DefPagamentoEntity();
    if (e.getTipoEncargoId() != null) {
      dp.setTmId(entityManager.getReference(TipoMovimentoEntity.class, e.getTipoEncargoId()));
    }
    dp.setValor(e.getValor());
    dp.setDataInicio(e.getDataInicio());
    dp.setDataFim(e.getDataFim());
    dp.setObs(e.getObservacoes());
    dp.setEstado(estado);
    dp.setUuid(UuidCreator.getTimeOrderedEpoch());
    dp.setFunId(fun);
    return dp;
  }

}
