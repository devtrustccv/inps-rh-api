package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.FuncionarioFilter;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper mapper;

  @Transactional
  @Override
  public Funcionario save(Funcionario funcionario) {
    // Converte domínio para entity
    var entity = mapper.toEntity(funcionario);

    var savedEntity = funcionarioEntityRepository.save(entity);

    return mapper.toDomain(savedEntity);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Funcionario> findById(Long id) {
    return funcionarioEntityRepository.findById(id)
        .map(mapper::toDomain);
  }

  @Transactional(readOnly = true)
  @Override
  public List<FuncionarioList> findAll(FuncionarioFilter filters) {
    int pageNumber = filters.getPageNumber() != null ? filters.getPageNumber() : 0;
    int pageSize = filters.getPageSize() != null ? filters.getPageSize() : 50;
    int offset = pageNumber * pageSize;

    return funcionarioEntityRepository.findFuncionariosWithFilters(
        filters.getNome(),
        filters.getDirecao(),
        filters.getSeccao(),
        filters.getTipoVinculoLaboral(),
        filters.getEstado() != null ? filters.getEstado().getCode() : null,
        filters.getDataInicio(),
        filters.getDataFim(),
        offset + 1,
        offset + pageSize
    );
  }


}
