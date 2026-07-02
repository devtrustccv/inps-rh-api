package cv.inps.rh.shared.application.service;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DominioService {

  private final DomainEntityRepository domainEntityRepository;

  /**
   * Devolve o mapa {@code valor -> descricao} de um domínio ativo em RH_T_DOMAINS.
   * As chaves são {@code trim}adas para evitar falhas de correspondência causadas
   * por espaços acidentais no valor gravado (ex.: "MUDANCA_CARREIRA ").
   */
  public Map<String, String> getDominioMap(String dominio) {
    return domainEntityRepository.findByDominioAndEstado(dominio, Estado.A)
        .stream()
        .filter(d -> d.getValor() != null)
        .collect(Collectors.toMap(
            d -> d.getValor().trim(),
            DomainEntity::getDescricao,
            (a, b) -> a));
  }

  /**
   * Traduz um código para a descrição correspondente no mapa de domínio.
   * Se não houver correspondência, devolve o próprio código ({@code trim}ado)
   * como fallback.
   */
  public String traduzir(Map<String, String> dominio, String codigo) {
    if (codigo == null) {
      return null;
    }
    var chave = codigo.trim();
    return dominio.getOrDefault(chave, chave);
  }

}
