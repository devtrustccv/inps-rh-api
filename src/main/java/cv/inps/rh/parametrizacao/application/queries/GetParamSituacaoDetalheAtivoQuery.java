package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamSituacaoDetalheAtivoQuery implements Query {

    @NotNull(message = "The field <situacaoLaboralId> is required")
    private Long situacaoLaboralId;
}
