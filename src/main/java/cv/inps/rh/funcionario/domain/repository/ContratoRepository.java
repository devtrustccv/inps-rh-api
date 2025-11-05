package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.ContratoFilter;
import cv.inps.rh.funcionario.domain.models.Contrato;

import java.util.List;

public interface ContratoRepository {

  public List<Contrato> findAll(ContratoFilter filters);
}
