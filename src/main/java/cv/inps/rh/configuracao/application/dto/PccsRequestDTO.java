package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PccsRequestDTO {

    @NotBlank(message = "O campo <descricao> é obrigatório")
    private String descricao;

    @NotNull(message = "O campo <dataInicio> é obrigatório")
    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Integer flgCopiaAnterior;

    private Integer flgFecharAnterior;

    private String estado;
}
