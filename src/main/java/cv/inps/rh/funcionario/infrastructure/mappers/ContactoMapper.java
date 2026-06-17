package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.funcionario.application.dto.ContactoRespDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ContactoMapper {


  public ContactoEntity toEntity(ContactoReqDTO dto, Estado estado, FuncionarioEntity fun) {
    if (dto == null) return null;
    String tipoContacto = ValidationUtil.trimToNull(dto.getTipoContacto());
    String contacto = ValidationUtil.trimToNull(dto.getContacto());
    ValidationUtil.validateContacto(tipoContacto, contacto);
    ContactoEntity entity = new ContactoEntity();
    entity.setTipoContacto(tipoContacto);
    entity.setContacto(contacto);
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setFunId(fun);
    entity.setEstado(estado);
    return entity;
  }

  public List<ContactoEntity> syncContactos(List<ContactoEntity> existingList,
                                            List<ContactoReqDTO> newList, FuncionarioEntity fun) {

    if (newList == null) return existingList; // nada a fazer

    for (ContactoReqDTO dto : newList) {
      addOrUpdate(existingList, dto, fun);
    }

    // Soft delete dos que não vêm mais na nova lista
    for (ContactoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }

    return existingList;
  }

  private void addOrUpdate(List<ContactoEntity> existingList, ContactoReqDTO dto, FuncionarioEntity fun) {
    if (dto == null) return;

    ContactoEntity found = findById(existingList, dto.getId());

    if (found != null) {
      String tipoContacto = ValidationUtil.trimToNull(dto.getTipoContacto());
      String contacto = ValidationUtil.trimToNull(dto.getContacto());
      ValidationUtil.validateContacto(tipoContacto, contacto);
      found.setTipoContacto(tipoContacto);
      found.setContacto(contacto);
    } else {
      // adicionar
      ContactoEntity novo = this.toEntity(dto, Estado.P, fun);
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


  public ContactoRespDTO respDTO(ContactoEntity entity) {
    if (entity == null) return null;
    ContactoRespDTO cr = new ContactoRespDTO();
    cr.setId(entity.getId());
    cr.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    cr.setTipoContacto(entity.getTipoContacto());
    cr.setContacto(entity.getContacto());
    cr.setEstado(entity.getEstado() != null ? entity.getEstado().getDescription() : null);
    return cr;
  }

  public List<ContactoRespDTO> respDTOList(List<ContactoEntity> entities) {
    return entities.stream().map(this::respDTO).toList();
  }

}
