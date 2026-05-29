package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtrairMapaPessoalQuery implements Query {

    private Long direcaoId;
    private Long seccaoId;
    private String colaborador;
    private String estado;
}
