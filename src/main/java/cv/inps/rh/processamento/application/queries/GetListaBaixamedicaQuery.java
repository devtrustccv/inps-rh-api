package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaBaixamedicaQuery implements Query {

    private String dataInicio;

    private String dataFim;

    private String nomeFuncionario;

    private Long direccaoId;

    private Integer page;

    private Integer size;

    private Long tipoAbonoBeneficioId;
}
