package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.PagamentosDescontoListDTO;
import cv.inps.rh.funcionario.domain.filters.PagamentoDescontoFilter;
import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
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
}
