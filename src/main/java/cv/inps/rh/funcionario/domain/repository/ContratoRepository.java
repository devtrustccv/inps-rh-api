package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.ContratoFilter;
import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;

import java.util.Optional;

import java.util.List;

public interface ContratoRepository {

  public List<Contrato> findAll(ContratoFilter filters);

  public Optional<Contrato> findById(Long id);

  public Optional<Contrato> findByUuid(IdentificadorUnico id);
}
