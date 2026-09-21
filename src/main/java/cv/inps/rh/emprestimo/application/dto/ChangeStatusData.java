package cv.inps.rh.emprestimo.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ChangeStatusData(
    @NotBlank
    String estado,

    String observacao,
    List<DocumentoDTO> files
) {

}
