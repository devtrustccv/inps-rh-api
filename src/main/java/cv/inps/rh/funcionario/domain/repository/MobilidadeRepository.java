package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.MobilidadeFilter;
import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;

import java.util.List;
import java.util.Optional;

public interface MobilidadeRepository {

  public List<MobilidadeList> findAll(MobilidadeFilter filters);

  public Optional<Mobilidade> getMobilidadeById(IdentificadorUnico id);
}
