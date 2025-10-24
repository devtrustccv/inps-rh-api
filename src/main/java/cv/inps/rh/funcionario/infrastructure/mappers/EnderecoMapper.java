package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoRespDTO;
import cv.inps.rh.funcionario.domain.models.Endereco;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnderecoMapper {

  private final GeografiaMapper geografiaMapper;

  public Endereco toDomain(EnderecoEntity entity) {
    if (entity == null) return null;

    return Endereco.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getPaisId() != null ? geografiaMapper.toDomain(entity.getPaisId()) : null,
        entity.getIlhaId() != null ? geografiaMapper.toDomain(entity.getIlhaId()) : null,
        entity.getConcelhoId() != null ? geografiaMapper.toDomain(entity.getConcelhoId()) : null,
        entity.getFreguesiaId() != null ? geografiaMapper.toDomain(entity.getFreguesiaId()) : null,
        entity.getZonaId() != null ? geografiaMapper.toDomain(entity.getZonaId()) : null,
        entity.getMorada(),
        entity.getEstado()
    );
  }

  public EnderecoEntity toEntity(Endereco endereco) {
    if (endereco == null) return null;
    EnderecoEntity entity = new EnderecoEntity();

    if(endereco.getId() != null){
      entity.setId(endereco.getId());
    }

    entity.setUuid(endereco.getUuid().getValor());
    entity.setPaisId(endereco.getPais() != null ? geografiaMapper.toEntity(endereco.getPais()) : null);
    entity.setIlhaId(endereco.getIlha() != null ? geografiaMapper.toEntity(endereco.getIlha()) : null);
    entity.setConcelhoId(endereco.getConcelho() != null ? geografiaMapper.toEntity(endereco.getConcelho()) : null);
    entity.setFreguesiaId(endereco.getFreguesia() != null ? geografiaMapper.toEntity(endereco.getFreguesia()) : null);
    entity.setZonaId(endereco.getZona() != null ? geografiaMapper.toEntity(endereco.getZona()) : null);
    entity.setMorada(endereco.getMorada());
    entity.setEstado(endereco.getEstado());
    return entity;
  }


  public Endereco toDomain(EnderecoReqDTO enderecoReqDTO) {
    if (enderecoReqDTO == null) return null;

    var pais = geografiaMapper.toDomain(enderecoReqDTO.getPaisId());
    var ilha = geografiaMapper.toDomain(enderecoReqDTO.getIlhaId());
    var concelho = geografiaMapper.toDomain(enderecoReqDTO.getConcelhoId());
    var freguesia = geografiaMapper.toDomain(enderecoReqDTO.getFreguesiaId());
    var zona = geografiaMapper.toDomain(enderecoReqDTO.getZonaId());

    return Endereco.create(
        enderecoReqDTO.getId(),
        pais,
        ilha,
        concelho,
        freguesia,
        zona,
        enderecoReqDTO.getMorada()
    );
  }

  public List<Endereco> toEnderecosDomain(List<EnderecoReqDTO> reqDTOS){
    System.out.println("EnderecoMapper.toEnderecosDomain::: "+reqDTOS);
    if(reqDTOS == null) return null;
    return reqDTOS.stream()
        .map(this::toDomain)
        .collect(java.util.stream.Collectors.toList());
  }

  public EnderecoRespDTO toDTO(Endereco endereco) {
    if (endereco == null) return null;

    var dto = new EnderecoRespDTO();

    dto.setId(endereco.getId());

    if (endereco.getPais() != null) {
      dto.setPais(endereco.getPais().getId() != null ? endereco.getPais().getId().intValue() : null);
      dto.setPaisDesc(endereco.getPais().getNome());
    }

    if (endereco.getIlha() != null) {
      dto.setIlha(endereco.getIlha().getId() != null ? endereco.getIlha().getId().intValue() : null);
      dto.setIlhaDesc(endereco.getIlha().getNome());
    }

    if (endereco.getConcelho() != null) {
      dto.setConcelho(endereco.getConcelho().getId() != null ? endereco.getConcelho().getId().intValue() : null);
      dto.setConcelhoDesc(endereco.getConcelho().getNome());
    }

    if (endereco.getFreguesia() != null) {
      dto.setFreguesia(endereco.getFreguesia().getId() != null ? endereco.getFreguesia().getId().intValue() : null);
      dto.setFreguesiaDesc(endereco.getFreguesia().getNome());
    }


    if (endereco.getZona() != null) {
      dto.setZona(endereco.getZona().getId() != null ? endereco.getZona().getId().intValue() : null);
      dto.setZonaDesc(endereco.getZona().getNome());
    }

    dto.setMorada(endereco.getMorada());

    dto.setEstado(endereco.getEstado() != null ? endereco.getEstado().getDescription() : null);
    dto.setUuid(endereco.getUuid() != null ? endereco.getUuid().toString() : null);

    return dto;
  }

}
