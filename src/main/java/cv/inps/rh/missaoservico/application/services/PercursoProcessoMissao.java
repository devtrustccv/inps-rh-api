package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Percurso de etapas de cada processo da missão.
 *
 * <p>A etapa em que o processo começa vem da referência do domínio TIPO_PROCESSO_MISSAO
 * (ex.: {@code SEGURO_VIAGEM → ETAPA_PRESTADOR_SERVICO}); a partir dela o processo percorre todas
 * as etapas seguintes de {@link EtapaProcesso}, por ordem. Sem referência válida, vale o percurso
 * fixo de {@link TipoProcesso}.</p>
 *
 * <p>Um processo já criado que esteja numa etapa anterior à do domínio (criado antes de a
 * referência mudar, ex.: um Alojamento ainda em Prestador Serviço) mantém o percurso a partir da
 * etapa em que está — não fica bloqueado.</p>
 */
@Component
@RequiredArgsConstructor
public class PercursoProcessoMissao {

  private static final String PREFIXO_ETAPA = "ETAPA_";

  private final DomainEntityRepository domainRepository;

  /** Etapa em que um processo novo deste tipo começa. */
  public EtapaProcesso etapaInicial(TipoProcesso tipo) {
    return domainRepository
        .findByDominioAndValorAndEstado(Domains.TIPO_PROCESSO_MISSAO.getCode(), tipo.name(), Estado.A)
        .stream()
        .map(d -> d.getReferencia())
        .filter(StringUtils::hasText)
        .map(r -> EtapaProcesso.fromCode(r.trim().replaceFirst("^" + PREFIXO_ETAPA, "")))
        .filter(java.util.Objects::nonNull)
        .findFirst()
        .orElse(tipo.primeiraEtapa());
  }

  /** Percurso de um processo novo deste tipo. */
  public List<EtapaProcesso> etapas(TipoProcesso tipo) {
    return aPartirDe(etapaInicial(tipo));
  }

  /** Percurso deste processo: o do domínio, ou a partir da etapa atual se esta for anterior. */
  public List<EtapaProcesso> etapas(MissaoProcessoEntity processo) {
    var inicio = etapaInicial(TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso()));
    var atual = EtapaProcesso.fromCode(processo.getEtapa());
    if (atual != null && atual.ordinal() < inicio.ordinal()) {
      inicio = atual;
    }
    return aPartirDe(inicio);
  }

  /** Primeira etapa do percurso deste processo (para saber se ainda não avançou). */
  public EtapaProcesso primeiraEtapa(MissaoProcessoEntity processo) {
    return etapas(processo).getFirst();
  }

  public boolean percorre(MissaoProcessoEntity processo, EtapaProcesso etapa) {
    return etapas(processo).contains(etapa);
  }

  /** Se este processo passa por Prestadores Serviço (e Emissão de Requisição). */
  public boolean temPrestador(MissaoProcessoEntity processo) {
    return percorre(processo, EtapaProcesso.PRESTADOR_SERVICO);
  }

  /** Etapa seguinte no percurso deste processo; null quando {@code etapa} é a última. */
  public EtapaProcesso seguinte(MissaoProcessoEntity processo, EtapaProcesso etapa) {
    var etapas = etapas(processo);
    var i = etapas.indexOf(etapa);
    return i >= 0 && i + 1 < etapas.size() ? etapas.get(i + 1) : null;
  }

  private static List<EtapaProcesso> aPartirDe(EtapaProcesso inicio) {
    return Arrays.stream(EtapaProcesso.values())
        .filter(e -> e.ordinal() >= inicio.ordinal())
        .toList();
  }
}
