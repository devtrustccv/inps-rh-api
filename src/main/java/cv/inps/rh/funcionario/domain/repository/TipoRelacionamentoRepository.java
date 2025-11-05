package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;

import java.util.List;

public interface TipoRelacionamentoRepository {

  public List<TiposRelacionamento> findAllWithMobilidadeFilter(MobilidadeFilter filters);
}
