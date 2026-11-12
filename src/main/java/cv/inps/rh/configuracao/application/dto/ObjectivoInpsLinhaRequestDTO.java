package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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

    // Objetivo e KPI são opcionais (linhas INDIVIDUAL ficam por preencher na definição).
    private String descricao;

    private String kpi;
}
