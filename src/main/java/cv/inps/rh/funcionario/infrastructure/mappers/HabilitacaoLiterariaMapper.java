package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaRespDTO;
import cv.inps.rh.funcionario.domain.models.HabilitacaoLiteraria;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HabilitacaoLiterariaMapper {

  private final GeografiaMapper geografiaMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public HabilitacaoLiteraria toDomain(HabilitacaoLiterariaEntity entity) {
    if (entity == null) return null;

    Geografia paisRef = entity.getPaisId() != null
        ? geografiaMapper.toDomain(entity.getPaisId())
        : null;

    return HabilitacaoLiteraria.rebuild(
        entity.getId(),
        entity.getUuid(),
        paisRef,
        entity.getEstabelecimento(),
        entity.getArea(),
        entity.getNomeCurso(),
        entity.getNivel(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getConcluido() != null && entity.getConcluido() == 1,
        entity.getEstado() != null ? entity.getEstado() : null
    );
  }

  // Domain -> Entity (usando referência do EntityManager)
  public HabilitacaoLiterariaEntity toEntity(HabilitacaoLiteraria domain) {
    if (domain == null) return null;

    HabilitacaoLiterariaEntity entity = new HabilitacaoLiterariaEntity();
    if(domain.getId() !=null && domain.getId()>0)
     entity.setId(domain.getId());

    entity.setUuid(domain.getUuid().getValor());

    // Apenas referência de Geografia
    if (domain.getPais() != null) {
      entity.setPaisId(entityManager.getReference(
          GeografiaEntity.class,
          domain.getPais().getId()
      ));
    }

    entity.setEstabelecimento(domain.getEstabelecimento());
    entity.setArea(domain.getArea());
    entity.setNomeCurso(domain.getNomeCurso());
    entity.setNivel(domain.getNivel());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setConcluido(domain.getConcluido() != null && domain.getConcluido() ? 1 : 0);
    entity.setEstado(domain.getEstado() != null ? domain.getEstado() : null);

    return entity;
  }

  public HabilitacaoLiteraria toDomain(HabilitacaoLiterariaReqDTO dto) {
    if (dto == null) return null;

    Geografia pais = dto.getPaisId() != null
        ? geografiaMapper.toDomain(dto.getPaisId())
        : null;

    return HabilitacaoLiteraria.create(
        dto.getId(),
        pais,
        dto.getEstabelecimento(),
        dto.getArea(),
        dto.getCurso(),
        dto.getGrauAcademico(),
        dto.getDataInicio(),
        dto.getDataTermino(),
        dto.getConcluido() != null && dto.getConcluido() == 1
    );
  }

  public List<HabilitacaoLiteraria> toHabilitacoesLiterariasDomain(List<HabilitacaoLiterariaReqDTO> dtos) {
    if (dtos == null) return null;
    return dtos.stream().map(this::toDomain).toList();
  }


  public HabilitacaoLiterariaRespDTO toResponseDTO(HabilitacaoLiteraria domain) {
    if (domain == null) return null;

    HabilitacaoLiterariaRespDTO dto = new HabilitacaoLiterariaRespDTO();

    dto.setId(domain.getId());
    dto.setPais(domain.getPais() != null ? domain.getPais().getId().intValue() : null); // converte Long -> Integer
    dto.setPaisDesc(domain.getPais() != null ? domain.getPais().getNome() : null);
    dto.setEstabelecimento(domain.getEstabelecimento());
    dto.setArea(domain.getArea());
    dto.setCurso(domain.getNomeCurso());
    dto.setGrauAcademico(domain.getNivel());
    dto.setDataInicio(domain.getDataInicio());
    dto.setDataTermino(domain.getDataFim());
    dto.setConcluido(domain.getConcluido() != null && domain.getConcluido() ? 1 : 0);

    return dto;
  }

  public List<HabilitacaoLiterariaRespDTO> toResponseDTOList(List<HabilitacaoLiteraria> domains) {
    if (domains == null) return null;
    return domains.stream()
        .map(this::toResponseDTO)
        .toList();
  }


}
