package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaLicensaSemVencimentoQuery implements Query {

    private String dataInicio;

    private String dataFim;

    private String colaborador;

    private String direccao;

    private String page;

    private String size;
}
