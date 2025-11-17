package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.RegimeListDTO;
import cv.inps.rh.funcionario.domain.filters.RegimeFilter;
import cv.inps.rh.funcionario.domain.models.RegimeModalidade;
import cv.inps.rh.funcionario.domain.models.RegimeTrabalho;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegimeTrabalhoMapper {

  private final ContratoMapper contratoMapper;
  private final RegimeModalidadeMapper regimeModalidadeMapper;

  // Entity -> Domain
  public RegimeTrabalho toDomain(RegimeTrabalhoEntity entity) {
    if (entity == null) return null;

    List<RegimeModalidade> modalidades = entity.getModalidades() != null
        ? entity.getModalidades().stream()
        .map(regimeModalidadeMapper::toDomain)
        .collect(Collectors.toList())
        : null;

    return RegimeTrabalho.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoRegime(),
        entity.getTipoSituacao(),
        entity.getDataFim(),
        entity.getObs(),
        entity.getEstado(),
        modalidades,
        entity.getFunId().getId(),
        entity.getFunId().getUuid()
    );
  }

  // Domain -> Entity
  public RegimeTrabalhoEntity toEntity(RegimeTrabalho domain) {
    if (domain == null) return null;

    RegimeTrabalhoEntity entity = new RegimeTrabalhoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setTipoRegime(domain.getTipoRegime());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setDataFim(domain.getDataFim());
    entity.setObs(domain.getObs());
    entity.setEstado(domain.getEstado());

    if (domain.getRegimeModalidades() != null && !domain.getRegimeModalidades().isEmpty()) {
      List<RegimeModalidadeEntity> modalidades = domain.getRegimeModalidades().stream()
          .map(regimeModalidadeMapper::toEntity)
          .collect(Collectors.toList());
      entity.setModalidades(modalidades);
    }

    return entity;
  }

  public RegimeListDTO toDTO(RegimeTrabalho regime) {
    if (regime == null) return null;

    RegimeListDTO dto = new RegimeListDTO();

    dto.setId(regime.getId());
    dto.setUuid(regime.getUuid().getValor().toString());

    dto.setIdFuncionario(regime.getIdFuncionario());
    dto.setUuidFuncionario(regime.getUuidFuncionario()!= null ? regime.getUuidFuncionario().toString() : null);

    dto.setTipoRegime(regime.getTipoRegime());
    dto.setDataInicio(null); // todo tirar duvidas com analise, nao existe na base de dados
    dto.setDataFim(regime.getDataFim() != null ? regime.getDataFim().toString() : null);
    dto.setModalidade(regime.getDiasSemanaAgrupados());
    dto.setNumHoras(String.valueOf(regime.getTotalHoras()));

    if (regime.getEstado() != null) {
      dto.setEstado(regime.getEstado().getCode());
      dto.setEstadoDesc(regime.getEstado().getDescription());
    }

    return dto;
  }

  public RegimeFilter toFilterDomain(String tipoRegime,
                                           String estado,
                                          Integer pageNumber,
                                          Integer pageSize) {
       return RegimeFilter.builder().tipoRegime(tipoRegime).estado(StringUtils.hasText(estado) ? Estado.fromCodeOrThrow(estado) : null)
           .pageNumber(pageNumber)
           .pageSize(pageSize).build();
  }

}
