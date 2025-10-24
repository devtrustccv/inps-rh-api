package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.models.Funcionario;

import java.util.Optional;

public interface FuncionarioRepository {

  Funcionario save(Funcionario funcionario);

  Optional<Funcionario> findById(Long id);
}
