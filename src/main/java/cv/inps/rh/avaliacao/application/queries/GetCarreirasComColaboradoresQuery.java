package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCarreirasComColaboradoresQuery implements Query {

  private Long institId;

}
