package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.funcionario.application.dto.ContactoRespDTO;
import cv.inps.rh.funcionario.domain.models.Contacto;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

  public ContactoEntity toEntity(ContactoReqDTO dto, Estado estado) {
    if (dto == null) return null;
    ContactoEntity entity = new ContactoEntity();
    entity.setTipoContacto(dto.getTipoContacto());
    entity.setContacto(dto.getContacto());
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado(estado);
    return entity;
  }

  public List<ContactoEntity> syncContactos(List<ContactoEntity> existingList,
                                            List<ContactoReqDTO> newList) {

    if (newList == null) return existingList; // nada a fazer

    for (ContactoReqDTO dto : newList) {
      addOrUpdate(existingList, dto);
    }

    // Soft delete dos que não vêm mais na nova lista
    for (ContactoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        // Se tiver campo de soft delete:
        existing.setEstado(Estado.E);
      }
    }

    return existingList;
  }

  private void addOrUpdate(List<ContactoEntity> existingList, ContactoReqDTO dto) {
    if (dto == null) return;

    ContactoEntity found = findById(existingList, dto.getId());

    if (found != null) {
      // atualizar
      found.setTipoContacto(dto.getTipoContacto());
      found.setContacto(dto.getContacto());
    } else {
      // adicionar
      ContactoEntity novo = this.toEntity(dto, Estado.P);
      existingList.add(novo);
    }
  }

  private ContactoEntity findById(List<ContactoEntity> list, Long id) {
    if (id == null) return null;
    return list.stream()
        .filter(c -> Objects.equals(c.getId(), id))
        .findFirst()
        .orElse(null);
  }


}
