package cv.inps.rh.shared.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DirecaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Sigla da direção (RH_T_DIRECAO.SIGA: GSI, DARH, ...) para as colunas "Direção" das listas — o nome
 * completo partia a coluna em várias linhas. É a mesma sigla da Lista dos Colaboradores
 * (RH_V_DOSSIE.DIRECAO_DESC). Sem sigla fica o nome.
 */
@Service
@RequiredArgsConstructor
public class DirecaoSiglaService {

  private final DirecaoEntityRepository direcaoEntityRepository;

  /**
   * id da direção → sigla (ou nome, se não tiver sigla). A tabela é pequena (dezenas de linhas): uma
   * consulta por pedido de lista, em vez de uma por linha.
   */
  @Transactional(readOnly = true)
  public Map<Long, String> siglaPorId() {
    var mapa = new HashMap<Long, String>();
    direcaoEntityRepository.findAll().forEach(d -> mapa.put(d.getId(), siglaOuNome(d)));
    return mapa;
  }

  /** Sigla de uma direção já carregada; o nome quando não tem sigla. */
  public static String siglaOuNome(DirecaoEntity direcao) {
    if (direcao == null) return null;
    return StringUtils.hasText(direcao.getSiga()) ? direcao.getSiga() : direcao.getNome();
  }

  /** Sigla para o id; {@code fallback} (normalmente o nome vindo da vista) quando não há. */
  public static String sigla(Map<Long, String> siglas, Long idDirecao, String fallback) {
    if (idDirecao == null) return fallback;
    return siglas.getOrDefault(idDirecao, fallback);
  }
}
