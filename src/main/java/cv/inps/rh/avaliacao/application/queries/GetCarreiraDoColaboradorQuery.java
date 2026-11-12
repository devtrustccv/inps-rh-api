package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCarreiraDoColaboradorQuery implements Query {

  private UUID funUuid;

}
