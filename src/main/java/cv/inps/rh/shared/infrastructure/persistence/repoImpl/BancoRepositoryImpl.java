package cv.inps.rh.shared.infrastructure.persistence.repoImpl;

import cv.inps.rh.shared.domain.models.Banco;
import cv.inps.rh.shared.domain.repository.BancoRepository;
import cv.inps.rh.shared.infrastructure.mappers.BancoMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BancoRepositoryImpl implements BancoRepository {

  private final BancoEntityRepository bancoEntityRepository;
  private final BancoMapper bancoMapper;

  @Transactional(readOnly = true)
  @Override
  public List<Banco> findAllActive() {
    return bancoEntityRepository.findAll()
        .stream()
        .map(bancoMapper::toDomain)
        .toList();
  }
}
