package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.EnderecoReqDTO;
import cv.inps.rh.funcionario.application.dto.EnderecoRespDTO;
import cv.inps.rh.funcionario.domain.models.Endereco;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnderecoMapper {

  private final GeografiaMapper geografiaMapper;
  private final EntityManager em;

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

    if (endereco.getId() != null) {
      entity.setId(endereco.getId());
    }

    entity.setUuid(endereco.getUuid().getValor());

    if (endereco.getPais() != null) {
      entity.setPaisId(em.getReference(GeografiaEntity.class, endereco.getPais().getId()));
    }
    if (endereco.getIlha() != null) {
      entity.setIlhaId(em.getReference(GeografiaEntity.class, endereco.getIlha().getId()));
    }
    if (endereco.getConcelho() != null) {
      entity.setConcelhoId(em.getReference(GeografiaEntity.class, endereco.getConcelho().getId()));
    }
    if (endereco.getFreguesia() != null) {
      entity.setFreguesiaId(em.getReference(GeografiaEntity.class, endereco.getFreguesia().getId()));
    }
    if (endereco.getZona() != null) {
      entity.setZonaId(em.getReference(GeografiaEntity.class, endereco.getZona().getId()));
    }

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
      dto.setPaisId(endereco.getPais().getId() != null ? endereco.getPais().getId().intValue() : null);
      dto.setPaisDesc(endereco.getPais().getNome());
    }

    if (endereco.getIlha() != null) {
      dto.setIlhaId(endereco.getIlha().getId() != null ? endereco.getIlha().getId().intValue() : null);
      dto.setIlhaDesc(endereco.getIlha().getNome());
    }

    if (endereco.getConcelho() != null) {
      dto.setConcelhoId(endereco.getConcelho().getId() != null ? endereco.getConcelho().getId().intValue() : null);
      dto.setConcelhoDesc(endereco.getConcelho().getNome());
    }

    if (endereco.getFreguesia() != null) {
      dto.setFreguesiaId(endereco.getFreguesia().getId() != null ? endereco.getFreguesia().getId().intValue() : null);
      dto.setFreguesiaDesc(endereco.getFreguesia().getNome());
    }


    if (endereco.getZona() != null) {
      dto.setZonaId(endereco.getZona().getId() != null ? endereco.getZona().getId().intValue() : null);
      dto.setZonaDesc(endereco.getZona().getNome());
    }

    dto.setMorada(endereco.getMorada());

    dto.setEstado(endereco.getEstado() != null ? endereco.getEstado().getDescription() : null);
    dto.setUuid(endereco.getUuid() != null ? endereco.getUuid().toString() : null);

    return dto;
  }

  public List<EnderecoRespDTO> toDTOList(List<Endereco> enderecos){
    if(enderecos == null) return null;
    return enderecos.stream()
        .map(this::toDTO)
        .collect(java.util.stream.Collectors.toList());
  }


  public EnderecoEntity toEntity(EnderecoReqDTO dto) {
    if (dto == null) {
      return null;
    }
    EnderecoEntity e = new EnderecoEntity();
    e.setPaisId(ref(GeografiaEntity.class, dto.getPaisId()));
    e.setIlhaId(ref(GeografiaEntity.class, dto.getIlhaId()));
    e.setConcelhoId(ref(GeografiaEntity.class, dto.getConcelhoId()));
    e.setFreguesiaId(ref(GeografiaEntity.class, dto.getFreguesiaId()));
    e.setZonaId(ref(GeografiaEntity.class, dto.getZonaId()));
    e.setMorada(dto.getMorada());
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    return e;
  }

  private <T> T ref(Class<T> type, Long id) {
    return id == null ? null : em.getReference(type, id);
  }

}
