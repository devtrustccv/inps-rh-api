package cv.inps.rh.parametrizacao.domain.repository;

import cv.inps.rh.parametrizacao.domain.models.Dominio;

import java.util.List;

public interface DomainsRepository {
  List<Dominio> findAllByDominio(String dominio);
  List<Dominio> findAllByDominio(String dominio, String referencia);
}
