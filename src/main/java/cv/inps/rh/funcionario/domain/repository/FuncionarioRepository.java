package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.filters.FuncionarioFilter;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {

  Funcionario save(Funcionario funcionario);

  Optional<Funcionario> findById(IdentificadorUnico id);

  List<FuncionarioList> findAll(FuncionarioFilter filters);
}
