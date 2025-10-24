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
    if (geografia == null) return null;

    GeografiaEntity entity = new GeografiaEntity();
    entity.setId(geografia.getId());

    if (geografia.getNome() != null)
      entity.setNome(geografia.getNome());

    if (geografia.getNacionalidade() != null)
      entity.setNacionalidade(geografia.getNacionalidade());

    if (geografia.getGeografiaPai() != null)
      entity.setGeogrId(geografia.getGeografiaPai());

    if (geografia.getPais() != null)
      entity.setPais(geografia.getPais());

    if (geografia.getNivelDetalhe() != null)
      entity.setNivelDetalhe(geografia.getNivelDetalhe());

    if (geografia.getNomeOficial() != null)
      entity.setNomeOficial(geografia.getNomeOficial());

    if (geografia.getFlagAlter() != null)
      entity.setFlagAlter(geografia.getFlagAlter());

    if (geografia.getNomeNorm() != null)
      entity.setNomeNorm(geografia.getNomeNorm());

    if (geografia.getTipoGeografia() != null)
      entity.setTpGeogCd(geografia.getTipoGeografia());

    if (geografia.getSituacao() != null)
      entity.setFlgSituacao(geografia.getSituacao());

    return entity;
  }

}
