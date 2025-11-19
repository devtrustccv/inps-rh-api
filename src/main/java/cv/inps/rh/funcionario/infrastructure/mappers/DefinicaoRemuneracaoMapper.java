package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.RenumeracaoListDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.funcionario.domain.filters.RenumeracaoFilter;
import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefinicaoRemuneracaoMapper {

  private final ContratoMapper contratoMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public DefinicaoRemuneracao toDomain(DefinicaoRemuneracaoEntity entity) {
    if (entity == null) return null;

    return DefinicaoRemuneracao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getPercentagem(),
        entity.getValor(),
        entity.getEstado(),
        entity.getObs(),
        tipoMovimentoMapper.toDomain(entity.getTmId()),
        entity.getDataInicio(),
        entity.getDataFim()
    );
  }

  // Domain -> Entity
  public DefinicaoRemuneracaoEntity toEntity(DefinicaoRemuneracao domain) {
    if (domain == null) return null;

    DefinicaoRemuneracaoEntity entity = new DefinicaoRemuneracaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setPercentagem(domain.getPercentagem());
    entity.setValor(domain.getValor());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());

    if (domain.getTipoMovimento() != null) {
      entity.setTmId(entityManager.getReference(
          TipoMovimentoEntity.class,
          domain.getTipoMovimento().getId()
      ));
    }
    return entity;
  }

  public RenumeracaoFilter toFilterDomain(String estado,
                                          String dataInicio,
                                          String dataFim,
                                          Integer pageNumber,
                                          Integer pageSize) {

    return RenumeracaoFilter.builder()
        .estado(StringUtils.hasText(estado) ? Estado.fromCodeOrThrow(estado) : null)
        .dataInicio(StringUtils.hasText(dataInicio) ? DateFormatter.stringToLocalDate(dataInicio) : null)
        .dataFim(StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDate(dataFim) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();

  }

  public RenumeracaoListDTO toDTO(DefinicaoRemuneracao domain) {
    if (domain == null) return null;

    RenumeracaoListDTO dto = new RenumeracaoListDTO();
    dto.setId(domain.getId());
    dto.setUuid(domain.getUuid().getValor().toString());
    dto.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);
    dto.setEstadoDesc(domain.getEstado() != null ? domain.getEstado().getDescription() : null);
    dto.setMovimento(domain.getTipoMovimento() != null ? domain.getTipoMovimento().getDescricao() : null);
    dto.setValor(domain.getValor() != null ? domain.getValor().toPlainString() : null);
    dto.setUltimoPRoc(domain.getDataInicio() != null ? domain.getDataInicio().toString() : null);

    return dto;
  }

  public java.util.List<DefinicaoRemuneracaoEntity> syncRemuneracoes(List<DefinicaoRemuneracaoEntity> existingList,
                                                                      List<cv.inps.rh.funcionario.application.dto.SubsidioReqDTO> newList) {
    if (newList == null) return existingList;
    for (cv.inps.rh.funcionario.application.dto.SubsidioReqDTO dto : newList) {
      DefinicaoRemuneracaoEntity found = null;
      if (dto.getId() != null) {
        for (DefinicaoRemuneracaoEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        if (dto.getTipoSubsidioId() != null) {
          found.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoSubsidioId()));
        }
        found.setPercentagem(dto.getPercentagem());
        found.setValor(dto.getValor());
        found.setObs(dto.getObservacoes());
      } else {
        DefinicaoRemuneracaoEntity novo = new DefinicaoRemuneracaoEntity();
        if (dto.getTipoSubsidioId() != null) {
          novo.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoSubsidioId()));
        }
        novo.setPercentagem(dto.getPercentagem());
        novo.setValor(dto.getValor());
        novo.setObs(dto.getObservacoes());
        novo.setEstado(Estado.P);
        existingList.add(novo);
      }
    }
    for (DefinicaoRemuneracaoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public DefinicaoRemuneracaoEntity toDefinicaoRemuneracao(SubsidioReqDTO s, FuncionarioEntity fun, Estado estado) {
    if (s == null) return null;
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(s.getPercentagem());
    de.setValor(s.getValor());
    de.setEstado(estado);
    de.setObs(s.getObservacoes());
    de.setDataInicio(LocalDate.now());
    de.setDataFim(LocalDate.now());
    if (s.getTipoSubsidioId() != null) {
      de.setTmId(entityManager.getReference(TipoMovimentoEntity.class, s.getTipoSubsidioId()));
    }
    de.setFunId(fun);
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }


}
