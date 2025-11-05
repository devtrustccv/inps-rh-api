package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.FuncionarioFilter;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.projections.FuncionarioList;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {

  Funcionario save(Funcionario funcionario);

  Optional<Funcionario> findById(Long id);

  List<FuncionarioList> findAll(FuncionarioFilter filters);
}
