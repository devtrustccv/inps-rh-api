package cv.inps.rh.parametrizacao.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cv.inps.rh.parametrizacao.domain.models.Dominio;
import cv.inps.rh.parametrizacao.domain.repository.DomainsRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.DomainsMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class DomainsRepositoryImpl implements DomainsRepository {

  private final DomainEntityRepository domainEntityRepository;
  private final DomainsMapper domainsMapper;

  @Transactional(readOnly = true)
  @Override
  public List<Dominio> findAllByDominio(String dominio) {
    return StringUtils.hasText(dominio)
        ? domainEntityRepository.findByDominioAndEstado(dominio, Estado.A)
        .stream()
        .map(DomainsMapper::toDomain)
        .toList()
        : List.of();

  }

  @Transactional(readOnly = true)
  @Override
  public List<Dominio> findAllByDominio(String dominio, String referencia) {
    if (!StringUtils.hasText(dominio)) return List.of();

    var entities = StringUtils.hasText(referencia)
        ? domainEntityRepository.findByDominioAndReferenciaAndEstado(dominio, referencia, Estado.A)
        : domainEntityRepository.findByDominioAndEstado(dominio, Estado.A);

    return entities.stream()
        .map(DomainsMapper::toDomain)
        .toList();
  }

}
