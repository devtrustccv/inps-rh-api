package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamSituacoesLaboraisByVinculoQuery implements Query {

    private Long vinculoId;

    private String flgEstadoContrato;

    private String flgAbonoBeneficio;

    private String codigo;
}
