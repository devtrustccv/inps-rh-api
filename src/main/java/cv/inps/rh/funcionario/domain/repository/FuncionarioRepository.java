package cv.inps.rh.funcionario.domain.repository;

import cv.inps.rh.funcionario.domain.models.Funcionario;

public interface FuncionarioRepository {

  Funcionario save(Funcionario funcionario);
}
