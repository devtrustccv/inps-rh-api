package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import org.springframework.stereotype.Component;

@Component
public class GeografiaMapper {

  /**
   * Reconstrói Geografia a partir da entity
   */
  public Geografia toDomain(GeografiaEntity entity) {
    if (entity == null) return null;

    return Geografia.rebuild(
        entity.getId(),
        entity.getNome(),
        entity.getNacionalidade(),
        entity.getGeogrId(),
        entity.getPais(),
        entity.getNivelDetalhe(),
        entity.getNomeOficial(),
        entity.getFlagAlter(),
        entity.getNomeNorm(),
        entity.getTpGeogCd(),
        entity.getFlgSituacao()
    );
  }

  public Geografia toDomain(Long idGeografia) {
    if (idGeografia == null) return null;

    return Geografia.rebuild(
        idGeografia,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );
  }


  /**
   * Converte domínio Geografia para entity
   */
  public GeografiaEntity toEntity(Geografia geografia) {
    GeografiaEntity entity = new GeografiaEntity();
    entity.setId(geografia.getId());
    entity.setNome(geografia.getNome());
    entity.setNacionalidade(geografia.getNacionalidade());
    entity.setGeogrId(geografia.getGeografiaPai());
    entity.setPais(geografia.getPais());
    entity.setNivelDetalhe(geografia.getNivelDetalhe());
    entity.setNomeOficial(geografia.getNomeOficial());
    entity.setFlagAlter(geografia.getFlagAlter());
    entity.setNomeNorm(geografia.getNomeNorm());
    entity.setTpGeogCd(geografia.getTipoGeografia());
    entity.setFlgSituacao(geografia.getSituacao());
    return entity;
  }

}
