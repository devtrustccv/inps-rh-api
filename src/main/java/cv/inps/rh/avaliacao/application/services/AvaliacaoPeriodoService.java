package cv.inps.rh.avaliacao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.constants.ComponenteAvaliacaoRef;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.TipoPeriodicidade;
import cv.inps.rh.shared.application.constants.TipoProcessoAvaliacao;
import cv.inps.rh.shared.application.service.DominioService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoPeriodicidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoPeriodicidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import cv.inps.rh.shared.application.constants.Estado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tudo o que depende do eixo temporal da avaliação.
 *
 * <p>Concentra aqui a leitura do domínio PERIODICIDADE (que tem dois níveis — tipos e
 * períodos), a validação de um período contra o tipo parametrizado no ano, e o
 * acesso às duas tabelas novas: RH_T_AVD_DETALHE (resultado por período) e
 * RH_T_AVD_PERIODICIDADE (medição de cada componente em cada período).</p>
 */
@Service
public class AvaliacaoPeriodoService {

  private static final String ESTADO_ATIVO = "A";

  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final AvaliacaoDetalheEntityRepository detalheRepository;
  private final AvaliacaoPeriodicidadeEntityRepository periodicidadeRepository;
  private final DomainEntityRepository domainRepository;
  private final DominioService dominioService;

  public AvaliacaoPeriodoService(
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      AvaliacaoDetalheEntityRepository detalheRepository,
      AvaliacaoPeriodicidadeEntityRepository periodicidadeRepository,
      DomainEntityRepository domainRepository,
      DominioService dominioService) {
    this.objetivoDetRepository = objetivoDetRepository;
    this.detalheRepository = detalheRepository;
    this.periodicidadeRepository = periodicidadeRepository;
    this.domainRepository = domainRepository;
    this.dominioService = dominioService;
  }

  // ---------------------------------------------------------------- períodos

  /**
   * O tipo de periodicidade parametrizado para o ano. Sem parametrização não há ciclo,
   * por isso rebenta em vez de assumir semestral.
   */
  @Transactional(readOnly = true)
  public TipoPeriodicidade tipoDoAno(Integer ano) {
    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(ano)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Não há parametrização de componentes para o ano " + ano + "."));
    if (!StringUtils.hasText(det.getPeriodicidade())) {
      throw IgrpResponseStatusException.badRequest(
          "A parametrização do ano " + ano + " não tem periodicidade definida.");
    }
    return TipoPeriodicidade.fromValorOrThrow(det.getPeriodicidade());
  }

  /**
   * Valida que {@code periodo} é um dos períodos do ciclo parametrizado no ano e
   * devolve-o normalizado (maiúsculas, sem espaços).
   */
  @Transactional(readOnly = true)
  public String validarPeriodo(Integer ano, String periodo) {
    if (!StringUtils.hasText(periodo)) {
      throw IgrpResponseStatusException.badRequest("A periodicidade é obrigatória.");
    }
    var tipo = tipoDoAno(ano);
    var normalizado = periodo.trim().toUpperCase();
    if (!tipo.aceita(normalizado)) {
      throw IgrpResponseStatusException.badRequest(
          "Periodicidade '" + periodo + "' não pertence ao ciclo " + tipo.name()
              + " do ano " + ano + ". Valores aceites: " + tipo.periodos());
    }
    return normalizado;
  }

  /** Rótulos dos períodos, do domínio PERIODICIDADE: {@code código -> descrição}. */
  @Transactional(readOnly = true)
  public Map<String, String> descricoesDosPeriodos() {
    return dominioService.getDominioMap(Domains.PERIODICIDADE.getCode());
  }

  /**
   * Peso de cada período na nota do ano, do domínio AVD_PONDERACAO_FINAL, indexado pela
   * referência (que é o próprio código do período: SEMESTRE1, TRIMESTRE3, ANUAL, ...).
   */
  @Transactional(readOnly = true)
  public Map<String, BigDecimal> ponderacoesDoCiclo(TipoPeriodicidade tipo) {
    var linhas = domainRepository.findByDominioAndEstado(
        Domains.AVD_PONDERACAO_FINAL.getCode(), Estado.A);

    var porReferencia = new LinkedHashMap<String, BigDecimal>();
    for (var l : linhas) {
      if (l.getReferencia() == null || l.getValor() == null) {
        continue;
      }
      try {
        porReferencia.put(l.getReferencia().trim().toUpperCase(), new BigDecimal(l.getValor().trim()));
      } catch (NumberFormatException ignored) {
        // Linha de domínio mal preenchida: ignora-se em vez de rebentar a leitura toda.
      }
    }

    var resultado = new LinkedHashMap<String, BigDecimal>();
    for (var periodo : tipo.periodos()) {
      var p = porReferencia.get(periodo);
      if (p == null) {
        throw IgrpResponseStatusException.badRequest(
            "Falta a ponderação final do período " + periodo + " no domínio "
                + Domains.AVD_PONDERACAO_FINAL.getCode() + ".");
      }
      resultado.put(periodo, p);
    }
    return resultado;
  }

  // ---------------------------------------------------------------- detalhe

  /** O detalhe do período, criando-o se ainda não existir. */
  @Transactional
  public AvaliacaoDetalheEntity obterOuCriarDetalhe(AvaliacaoEntity avaliacao, String periodo) {
    return detalheRepository.findByAvaliacao_IdAndPeriodicidade(avaliacao.getId(), periodo)
        .orElseGet(() -> {
          var novo = new AvaliacaoDetalheEntity();
          novo.setUuid(UuidCreator.getTimeOrderedEpoch());
          novo.setAvaliacao(avaliacao);
          novo.setPeriodicidade(periodo);
          novo.setEstado(ESTADO_ATIVO);
          return detalheRepository.save(novo);
        });
  }

  @Transactional(readOnly = true)
  public List<AvaliacaoDetalheEntity> detalhesDe(Long avdId) {
    return detalheRepository.findAllByAvaliacao_Id(avdId);
  }

  @Transactional(readOnly = true)
  public List<AvaliacaoDetalheEntity> detalhesDe(List<Long> avdIds) {
    return avdIds.isEmpty() ? List.of() : detalheRepository.findAllByAvaliacao_IdIn(avdIds);
  }

  // --------------------------------------------------------------- medições

  /** A medição de uma componente num período, criando-a se ainda não existir. */
  @Transactional
  public AvaliacaoPeriodicidadeEntity obterOuCriarMedicao(
      AvaliacaoEntity avaliacao, String periodo, ComponenteAvaliacaoRef referencia, Long referenciaId) {

    return periodicidadeRepository
        .findByAvaliacao_IdAndPeriodicidadeAndReferenciaAndReferenciaId(
            avaliacao.getId(), periodo, referencia.name(), referenciaId)
        .orElseGet(() -> {
          var nova = new AvaliacaoPeriodicidadeEntity();
          nova.setUuid(UuidCreator.getTimeOrderedEpoch());
          nova.setAvaliacao(avaliacao);
          nova.setPeriodicidade(periodo);
          nova.setReferencia(referencia.name());
          nova.setReferenciaId(referenciaId);
          nova.setTipoProcesso(TipoProcessoAvaliacao.AVALIACAO.name());
          nova.setEstado(ESTADO_ATIVO);
          return periodicidadeRepository.save(nova);
        });
  }

  /**
   * As medições de um período, indexadas por {@code referencia|referenciaId} para
   * evitar um select por linha de componente.
   */
  @Transactional(readOnly = true)
  public Map<String, AvaliacaoPeriodicidadeEntity> medicoesDoPeriodo(Long avdId, String periodo) {
    var medicoes = periodicidadeRepository.findAllByAvaliacao_IdAndPeriodicidade(avdId, periodo);
    var mapa = new LinkedHashMap<String, AvaliacaoPeriodicidadeEntity>(medicoes.size());
    medicoes.forEach(m -> mapa.put(chave(m.getReferencia(), m.getReferenciaId()), m));
    return mapa;
  }

  public static String chave(String referencia, Long referenciaId) {
    return referencia + "|" + referenciaId;
  }

  public static String chave(ComponenteAvaliacaoRef referencia, Long referenciaId) {
    return chave(referencia.name(), referenciaId);
  }

  @Transactional
  public void guardarMedicoes(List<AvaliacaoPeriodicidadeEntity> medicoes) {
    periodicidadeRepository.saveAll(medicoes);
  }

  @Transactional
  public void guardarDetalhe(AvaliacaoDetalheEntity detalhe) {
    detalheRepository.save(detalhe);
  }

  // -------------------------------------------------------------- validação

  /**
   * Valida uma nota contra os níveis de avaliação do domínio {@link Domains#NIVEIS_AVD}.
   *
   * <p>Sem isto, uma nota fora da escala entra no cálculo e produz um resultado que não
   * cai em nenhum escalão — a avaliação fica com nota e sem classificação qualitativa.</p>
   *
   * <p>Enquanto o domínio estiver por preencher (uma linha de exemplo), não se valida,
   * para não bloquear ambientes ainda por parametrizar.</p>
   */
  @Transactional(readOnly = true)
  public void validarNota(BigDecimal nota, String ondeOcorreu) {
    if (nota == null) {
      return;
    }
    var niveis = niveisPermitidos();
    if (niveis.isEmpty()) {
      return;
    }
    if (!niveis.contains(nota.stripTrailingZeros().toPlainString())) {
      throw IgrpResponseStatusException.badRequest(
          "Avaliação inválida em " + ondeOcorreu + ": " + nota.toPlainString()
              + ". Níveis aceites: " + niveis + " (domínio " + Domains.NIVEIS_AVD.getCode() + ").");
    }
  }

  private java.util.Set<String> niveisPermitidos() {
    var valores = dominioService.getDominioMap(Domains.NIVEIS_AVD.getCode()).keySet();
    return valores.size() <= 1 ? java.util.Set.of() : valores;
  }

  // ------------------------------------------------------------------ util

  /** {@code nota x ponderacao / 100}, com zero quando falta algum dos dois. */
  public static BigDecimal aplicarPercentagem(BigDecimal nota, BigDecimal ponderacao) {
    if (nota == null || ponderacao == null) {
      return BigDecimal.ZERO;
    }
    return nota.multiply(ponderacao).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
  }

  public static BigDecimal escala2(BigDecimal valor) {
    return valor == null ? null : valor.setScale(2, RoundingMode.HALF_UP);
  }
}
