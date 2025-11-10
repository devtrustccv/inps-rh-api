package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.domain.filters.CarreiraFilter;
import cv.inps.rh.funcionario.domain.models.Carreira;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCargoMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCarreiraMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamCategoriaMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamEscalaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CarreiraMapper {

  private final ContratoMapper contratoMapper;
  private final ParamCargoMapper paramCargoMapper;
  private final ParamEscalaoMapper paramEscalaoMapper;
  private final ParamCategoriaMapper paramCategoriaMapper;
  private final ParamCarreiraMapper paramCarreiraMapper;

  private final EntityManager entityManager;

  // Entity -> Domain
  public Carreira toDomain(CarreiraEntity entity) {
    if (entity == null) return null;

    return Carreira.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getSalario(),
        entity.getFlgProcessa(),
        entity.getTipoSituacao(),
        entity.getEstado(),
        entity.getObs(),
        contratoMapper.toDomain(entity.getContratoId()),
        entity.getCargoId() != null ? paramCargoMapper.toDomain(entity.getCargoId()) : null,
        entity.getEscalaoId() != null ? paramEscalaoMapper.toDomain(entity.getEscalaoId()) : null,
        entity.getCategoriaId() != null ? paramCategoriaMapper.toDomain(entity.getCategoriaId()) : null,
        entity.getCarrPccsId() != null ? paramCarreiraMapper.toDomain(entity.getCarrPccsId()) : null
    );
  }

  // Domain -> Entity
  public CarreiraEntity toEntity(Carreira domain) {
    if (domain == null) return null;

    CarreiraEntity entity = new CarreiraEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setSalario(domain.getSalario());
    entity.setFlgProcessa(domain.getFlgProcessa());
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());

    // By reference ou via mapper
    entity.setCargoId(domain.getCargo() != null
        ? entityManager.getReference(ParamCargoEntity.class, domain.getCargo().getId())
        : null);
    entity.setEscalaoId(domain.getEscalao() != null
        ? entityManager.getReference(ParamEscalaoEntity.class, domain.getEscalao().getId())
        : null);
    entity.setCategoriaId(domain.getCategoria() != null
        ? entityManager.getReference(ParamCategoriaEntity.class, domain.getCategoria().getId())
        : null);
    entity.setCarrPccsId(domain.getCarrPccs() != null
        ? entityManager.getReference(ParamCarreiraEntity.class, domain.getCarrPccs().getId())
        : null);

    return entity;
  }

  public CarreiraFilter toFilterDomain(String tipoCarreira,
                                       String dataInicio,
                                       String dataFim,
                                       Integer pageNumber,
                                       Integer pageSize,
                                       String idFuncionario) {

    return CarreiraFilter.builder()
        .idFuncionario(IdentificadorUnico.from(idFuncionario))
        .tipoCarreira(StringUtils.hasText(tipoCarreira) ? tipoCarreira : null)
        .dataInicio(StringUtils.hasText(dataInicio) ? DateFormatter.stringToLocalDate(dataInicio) : null)
        .dataFim(StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDate(dataFim) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();

  }


  public  CarreiraListDTO toDTO(CarreiraList projection) {
    if (projection == null) return null;

    var dto = new CarreiraListDTO();
    dto.setId(projection.getId());
    dto.setUuid(projection.getUuid());
    dto.setIdFuncionario(projection.getIdFuncionario());
    dto.setUuidFuncionario(projection.getUuidFuncionario());
    dto.setTipoCarreira(projection.getTipoCarreira());
    dto.setVinculo(projection.getVinculo());
    dto.setCarreira(projection.getCarreira());
    dto.setCargo(projection.getCargo());
    dto.setEscalao(projection.getEscalao());
    dto.setSalario(projection.getSalario());
    dto.setSituacaoLaboral(projection.getSituacaoLaboral());
    dto.setDataInicio(projection.getDataInicio());
    dto.setDataFim(projection.getDataFim());
    dto.setProcessamento(projection.getProcessamento());
    dto.setEstado(projection.getEstado() != null ? projection.getEstado() : null);
    dto.setEstadoDesc(projection.getEstado() != null ? Estado.fromCodeOrThrow(projection.getEstado()).getDescription() : null);

    return dto;
  }


  /*public  CarreiraListDTO toDTO(Carreira carreira) {
    if (carreira == null) return null;

    var dto = new CarreiraListDTO();
    dto.setId(carreira.getId());
    dto.setUuid(carreira.getUuid().toString());
    dto.setIdFuncionario(projection.getIdFuncionario());
    dto.setUuidFuncionario(projection.getUuidFuncionario());
    dto.setTipoCarreira(carreira.getTipoSituacao());
    dto.setVinculo(carreira.ge);
    dto.setCarreira(carreira.getCarrPccs() != null ? carreira.getCarrPccs().getNome() : null);
    dto.setCargo(carreira.getCargo()!=null ? carreira.getCargo().getNome() : null);
    dto.setEscalao(carreira.getEscalao()!=null ? carreira.getEscalao().getEscalao() : null);
    dto.setSalario(projection.getSalario());
    dto.setSituacaoLaboral(projection.getSituacaoLaboral());
    dto.setDataInicio(projection.getDataInicio());
    dto.setDataFim(projection.getDataFim());
    dto.setProcessamento(projection.getProcessamento());
    dto.setEstado(projection.getEstado() != null ? projection.getEstado() : null);
    dto.setEstadoDesc(projection.getEstado() != null ? Estado.fromCodeOrThrow(projection.getEstado()).getDescription() : null);

    return dto;
  }*/


}
