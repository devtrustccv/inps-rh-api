package cv.inps.rh.configuracao.infrastructure.mappers;

import cv.inps.rh.configuracao.application.dto.DocOutputRequestDTO;
import cv.inps.rh.configuracao.application.dto.DocOutputResponseDTO;
import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocOutputMapper {

    public DocOutputResponseDTO toResponseDto(ParamDocOutputEntity entity) {
        if (entity == null) {
            return null;
        }

        DocOutputResponseDTO dto = new DocOutputResponseDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setTipoDocumento(entity.getTipoDocumento());
        dto.setTitulo(entity.getTitulo());
        dto.setCorpo(entity.getCorpo());
        dto.setAssinadoPor(entity.getAssinadoPor());
        dto.setEstado(entity.getEstado());

        if (entity.getResponsavel() != null) {
            dto.setResponsavelId(entity.getResponsavel().getFunId().getUuid().toString());
            dto.setResponsavelNome(entity.getResponsavel().getFunId().getNome());
        }

        return dto;
    }

    public List<DocOutputResponseDTO> toResponseDtoList(List<ParamDocOutputEntity> entities) {
        return entities.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    public ParamDocOutputEntity toEntity(DocOutputRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ParamDocOutputEntity entity = new ParamDocOutputEntity();
        entity.setTipoDocumento(ValidationUtil.trimToNull(dto.getTipoDocumento()));
        entity.setTitulo(ValidationUtil.trimToNull(dto.getTitulo()));
        entity.setCorpo(ValidationUtil.trimToNull(dto.getCorpo()));
        entity.setAssinadoPor(ValidationUtil.trimToNull(dto.getAssinadoPor()));

        return entity;
    }

    public WrapperDocOutputListDTO toWrapper(Page<ParamDocOutputEntity> page) {
        WrapperDocOutputListDTO wrapper = new WrapperDocOutputListDTO();
        wrapper.setContent(toResponseDtoList(page.getContent()));
        wrapper.setPageNumber(page.getNumber());
        wrapper.setPageSize(page.getSize());
        wrapper.setTotalElements(page.getTotalElements());
        wrapper.setTotalPages(page.getTotalPages());
        wrapper.setLast(page.isLast());
        wrapper.setFirst(page.isFirst());
        return wrapper;
    }
}
