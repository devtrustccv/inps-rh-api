package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ValidacaoResponseDTO;
import cv.inps.rh.funcionario.domain.filters.ValidacoesFilters;
import cv.inps.rh.funcionario.domain.models.Validacao;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ValidacaoMapper {

  private final TiposRelacionamentoMapper tiposRelacionamentoMapper;

  public ValidacaoEntity toEntity(Validacao validacao) {
    if (validacao == null) return null;

    var entity = new ValidacaoEntity();
    entity.setId(validacao.getId());
    entity.setObs(validacao.getObs());
    entity.setEstado(validacao.getEstado());
    entity.setUuid(validacao.getUuid().getValor());
    entity.setReferenciaName(validacao.getReferenciaName());
    entity.setTipoAccao(validacao.getTipoAccao());
    //entity.setReferenciaId(); //sera setado depois na iteracao do agregado pai

    return  entity;
  }

  public Validacao toDomain(ValidacaoEntity entity) {
    if (entity == null) return null;
    return Validacao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoAccao(),
        entity.getReferenciaName(),
        entity.getReferenciaId(),
        entity.getEstado(),
        entity.getObs(),
        tiposRelacionamentoMapper.toDomain(entity.getTiprelId()),
        entity.getCreatedDate()!=null ? entity.getCreatedDate().toLocalDate() : null,
        entity.getCreatedBy()
    );
  }

  public ValidacaoResponseDTO toDto(Validacao validacao) {
    if (validacao == null) return null;

    var dto = new ValidacaoResponseDTO();
    dto.setId(validacao.getId());
    dto.setUuid(validacao.getUuid() != null ? validacao.getUuid().getValor().toString() : null);
    dto.setNomeColaborador(validacao.getUserRegistro());
    dto.setTipoOperacao(validacao.getTipoAccao());
    dto.setReferenciaName(validacao.getReferenciaName());
    dto.setDataOperacao(DateFormatter.localDateToString(validacao.getDataRegistro()));
    dto.setUtilizador(validacao.getUserRegistro());

    return dto;
  }


  public ValidacoesFilters toFilterDomain(String nomeColaborador,
                                          String tipoAccao,
                                          String referenciaName,
                                          String dataInicio,
                                          String dataFim,
                                          Integer pageNumber,
                                          Integer pageSize) {

    return ValidacoesFilters.builder()
        .nomeColaborador(nomeColaborador)
        .tipoAccao(tipoAccao)
        .referenciaName(referenciaName)
        .dataInicio(StringUtils.hasText(dataInicio)  ? DateFormatter.stringToLocalDateTime(dataInicio) :null)
        .dataFim(StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDateTime(dataFim) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }


}
