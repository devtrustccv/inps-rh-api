package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjectivoInpsLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

    @NotNull
    private Integer numeroOrdem;

    @NotBlank
    private String abrangencia;

    private Long institId;

    @NotBlank
    private String descricao;

    private String kpi;
}
