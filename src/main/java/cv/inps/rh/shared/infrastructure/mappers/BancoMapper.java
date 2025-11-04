package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.models.Banco;
import cv.inps.rh.shared.domain.models.Entidade;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BancoMapper {


  public BancoEntity toEntity(Banco banco) {
    if (banco == null) return null;

    BancoEntity entity = new BancoEntity();
    entity.setId(banco.getId());
    entity.setCdBanco(banco.getCodigoBanco());
    entity.setSigla(banco.getSigla());
    entity.setNmBanco(banco.getNomeBanco());
    entity.setNuConta(banco.getNumeroConta());
    entity.setEntId(banco.getEntId());
    entity.setNib(banco.getNib());
    entity.setTmId(banco.getTmId());
    return entity;
  }

  // Converte entity -> domain
  public Banco toDomain(BancoEntity entity) {
    if (entity == null) return null;

    return Banco.rebuild(
        entity.getId(),
        entity.getCdBanco(),
        entity.getSigla(),
        entity.getNmBanco(),
        entity.getNuConta(),
        entity.getEntId(),
        entity.getNib(),
        entity.getTmId()
    );
  }

  public Banco toDomain(Long id) {
    if (id == null) return null;
    return Banco.rebuild(id);
  }


  public ParametrizacaoDTO toParametrizacaoDto(Banco banco) {
    if (banco == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(banco.getNomeBanco());
    dto.setValue(banco.getId());
    return dto;
  }


}
