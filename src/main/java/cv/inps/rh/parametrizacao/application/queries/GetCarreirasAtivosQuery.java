package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCarreirasAtivosQuery implements Query {

  private String pccsId;

}