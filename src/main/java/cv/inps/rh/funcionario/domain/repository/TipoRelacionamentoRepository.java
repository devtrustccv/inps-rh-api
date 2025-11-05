package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilters;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;

import java.util.List;

public interface TipoRelacionamentoRepository {

  public List<TiposRelacionamento> findAllWithMobilidadeFilter(MobilidadeFilters filters);
}
