package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.funcionario.application.dto.ContactoRespDTO;
import cv.inps.rh.funcionario.domain.models.Contacto;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactoMapper {

  public ContactoEntity toEntity(Contacto contacto) {
    if(contacto == null) return null;

    ContactoEntity entity = new ContactoEntity();
    entity.setId(contacto.getId());
    entity.setUuid(contacto.getUuid().getValor());
    entity.setTipoContacto(contacto.getTipoContacto());
    entity.setContacto(contacto.getContacto());
    entity.setEstado(contacto.getEstado());
    return entity;
  }

  public Contacto toDomain(ContactoEntity entity) {
    if(entity == null) return null;

    return Contacto.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoContacto(),
        entity.getContacto(),
        entity.getEstado()
    );
  }

  public Contacto toDomain(ContactoReqDTO contactoReqDTO) {
    if(contactoReqDTO == null) return null;
    return Contacto.create(contactoReqDTO.getId(), contactoReqDTO.getTipoContacto(), contactoReqDTO.getContacto());
  }

  public ContactoRespDTO toDTO(Contacto contacto) {
    if (contacto == null) return null;

    var dto = new ContactoRespDTO();
    dto.setId(contacto.getId());
    dto.setUuid(contacto.getUuid() != null ? contacto.getUuid().toString() : null);
    dto.setTipoContacto(contacto.getTipoContacto());
    dto.setContacto(contacto.getContacto());
    dto.setEstado(contacto.getEstado() != null ? contacto.getEstado().getDescription() : null);
    return dto;
  }

  public List<ContactoRespDTO> toDTOList(List<Contacto> contactos) {
    if (contactos == null) return null;
    return contactos.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public List<Contacto> toContactosDomain(List<ContactoReqDTO> reqDTOS){
    if(reqDTOS == null) return null;
    return reqDTOS.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

}
