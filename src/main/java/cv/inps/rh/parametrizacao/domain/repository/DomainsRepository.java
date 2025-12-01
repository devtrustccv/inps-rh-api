package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.Dominio;

import java.util.List;

public interface DomainsRepository {
  public List<Dominio> findAllByDominio(String dominio);
  public List<Dominio> findAllByDominio(String dominio, String referencia);
}
