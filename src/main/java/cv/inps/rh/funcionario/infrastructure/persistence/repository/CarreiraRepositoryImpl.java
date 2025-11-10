package cv.inps.rh.funcionario.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.filters.CarreiraFilter;
import cv.inps.rh.funcionario.domain.models.Carreira;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.funcionario.domain.repository.CarreiraRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CarreiraRepositoryImpl implements CarreiraRepository {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final CarreiraMapper carreiraMapper;

  @Override
  public List<CarreiraList> findAllWithProjection(CarreiraFilter filters) {

    long startRow = (long) filters.getPageNumber() * filters.getPageSize() + 1;
    long endRow = startRow + filters.getPageSize() - 1;

    return carreiraEntityRepository.findAllCarreiras(
        filters.getTipoCarreira(),
        filters.getDataInicio(),
        filters.getDataFim(),
        startRow,
        endRow,
        filters.getIdFuncionario().getValor()
    );
  }

  @Override
  public List<Carreira> findAll(CarreiraFilter filters) {

    long startRow = (long) filters.getPageNumber() * filters.getPageSize() + 1;
    long endRow = startRow + filters.getPageSize() - 1;

     return carreiraEntityRepository.findCarreirasNative( filters.getTipoCarreira(),
         filters.getDataInicio(),
         filters.getDataFim(),
         startRow,
         endRow,
         filters.getIdFuncionario().getValor()).stream().map( carreiraMapper::toDomain ).toList();
  }
}
