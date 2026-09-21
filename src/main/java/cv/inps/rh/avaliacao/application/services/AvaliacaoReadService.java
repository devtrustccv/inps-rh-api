package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.*;
import cv.inps.rh.shared.application.constants.ComponenteAvaliacaoRef;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoPeriodicidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoAtitudePessoalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoCompetenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.aplicarPercentagem;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.chave;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.escala2;
import static java.util.Optional.ofNullable;

/**
 * Leitura da avaliação.
 *
 * <p>As notas deixaram de estar nas linhas das componentes e passaram a vir de
 * RH_T_AVD_PERIODICIDADE, por período. Por isso todas as leituras que incluam notas
 * precisam de saber <em>que</em> período mostrar — sem ele, devolvem-se as componentes
 * sem medição, que é exactamente o que a definição de objectivos precisa.</p>
 */
@Service
public class AvaliacaoReadService {

  private static final String COMP_COMPORTAMENTAL = "COMPETENCIA_COMPORTAMENTAL";
  private static final String COMP_TECNICA = "COMPETENCIA_TECNICA";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;
  private final AvaliacaoPeriodoService periodoService;

  public AvaliacaoReadService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository,
      AvaliacaoPeriodoService periodoService) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
    this.periodoService = periodoService;
  }

  /** Componentes com as medições do período. Sem período, vêm sem notas. */
  @Transactional(readOnly = true)
  public AvaliacaoDTO getAvaliacao(String uuid, String periodicidade) {
    var id = parseUuid(uuid);
    var avaliacao = avaliacaoRepository.findByUuidOrThrow(id);
    var periodo = resolverPeriodo(avaliacao, periodicidade);

    var dto = new AvaliacaoDTO();
    preencher(dto, avaliacao, periodo);
    return dto;
  }

  /** A definição de objectivos não tem medições — só a estrutura das componentes. */
  @Transactional(readOnly = true)
  public AvaliacaoDTO getDefinicaoObjetivo(String uuid) {
    return getAvaliacao(uuid, null);
  }

  /** Como {@link #getAvaliacao}, mais o resultado e os pareceres do período. */
  @Transactional(readOnly = true)
  public AvaliacaoResponseDTO getAvaliacaoFull(String uuid, String periodicidade) {
    var id = parseUuid(uuid);
    var avaliacao = avaliacaoRepository.findByUuidOrThrow(id);
    var periodo = resolverPeriodo(avaliacao, periodicidade);

    var dto = new AvaliacaoResponseDTO();
    preencher(dto, avaliacao, periodo);

    if (periodo != null) {
      dto.setPeriodo(construirPeriodo(avaliacao, periodo));
    }

    return dto;
  }

  // ------------------------------------------------------------------ interno

  private void preencher(AvaliacaoDTO dto, AvaliacaoEntity avaliacao, String periodo) {
    var id = avaliacao.getUuid();
    fillHeader(avaliacao, dto, periodo);

    var medicoes = periodo != null
        ? periodoService.medicoesDoPeriodo(avaliacao.getId(), periodo)
        : Map.<String, AvaliacaoPeriodicidadeEntity>of();

    dto.setObjectivos(objectivoRepository.findAllByAvaliacaoObj_Uuid(id).stream()
        .sorted(Comparator.comparing(AvaliacaoObjectivoEntity::getNumeroOrdem,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(e -> toObjectivo(e, medicoes))
        .toList());

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(id).stream()
        .sorted(Comparator.comparing(AvaliacaoCompetenciaEntity::getNumeroOrdem,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();

    dto.setCompetenciasComportamentais(competencias.stream()
        .filter(c -> COMP_COMPORTAMENTAL.equalsIgnoreCase(c.getComponente()))
        .map(e -> toCompetenciaComportamental(e, medicoes))
        .toList());

    dto.setCompetenciasTecnicas(competencias.stream()
        .filter(c -> COMP_TECNICA.equalsIgnoreCase(c.getComponente()))
        .map(e -> toCompetenciaTecnica(e, medicoes))
        .toList());

    dto.setAtitudesPessoais(atitudeRepository.findAllByAvaliacao_Uuid(id).stream()
        .sorted(Comparator.comparing(
            (AvaliacaoAtitudePessoalEntity a) -> a.getNumeroOrdem() != null
                ? a.getNumeroOrdem()
                : (a.getParamObjetivo() != null ? a.getParamObjetivo().getNumeroOrdem() : null),
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(e -> toAtitude(e, medicoes))
        .toList());
  }

  private PeriodoAvaliacaoDTO construirPeriodo(AvaliacaoEntity avaliacao, String periodo) {
    var detalhe = periodoService.detalhesDe(avaliacao.getId()).stream()
        .filter(d -> periodo.equals(d.getPeriodicidade()))
        .findFirst()
        .orElse(null);
    if (detalhe == null) {
      return null;
    }

    var dto = new PeriodoAvaliacaoDTO();
    dto.setUuid(detalhe.getUuid() != null ? detalhe.getUuid().toString() : null);
    dto.setPeriodicidade(detalhe.getPeriodicidade());
    dto.setDescricao(periodoService.descricoesDosPeriodos()
        .getOrDefault(detalhe.getPeriodicidade(), detalhe.getPeriodicidade()));
    dto.setAvaliacaoObjectivo(detalhe.getAvaliacaoObjectivo());
    dto.setAvaliacaoCompetencia(detalhe.getAvaliacaoCompetencia());
    dto.setAvaliacaoAtitudePessoal(detalhe.getAvaliacaoAtitudePess());
    dto.setAvaliacaoFinal(detalhe.getAvaliacaoFinal());
    dto.setAvaliacaoQualitativa(detalhe.getAvaliacaoQualitativa());
    dto.setEstado(detalhe.getEstado());

    if (hasText(detalhe.getObservacaoGeral())
        || hasText(detalhe.getDescricaoPlano())
        || detalhe.getDataInicioEntrevista() != null
        || hasText(detalhe.getHoraInicioEntrevista())
        || hasText(detalhe.getHoraFimEntrevista())) {
      var obs = new ObservacaoGeralDTO();
      obs.setObservacaoGeralAvaliacao(detalhe.getObservacaoGeral());
      obs.setDescPlanoDesenvolvimento(detalhe.getDescricaoPlano());
      obs.setDataInicio(detalhe.getDataInicioEntrevista());
      obs.setHoraInicio(detalhe.getHoraInicioEntrevista());
      obs.setHoraFim(detalhe.getHoraFimEntrevista());
      dto.setObservacaoGeral(obs);
    }

    if (hasText(detalhe.getParecerColaborador()) || hasText(detalhe.getJustificacaoMotivo())) {
      var parecer = new ParecerColaboradorDTO();
      parecer.setParecer(detalhe.getParecerColaborador());
      parecer.setJustificar(detalhe.getJustificacaoMotivo());
      dto.setParecerColaborador(parecer);
    }

    if (hasText(detalhe.getObsComissaoExec())) {
      var ce = new ComissaoExecutivaDTO();
      ce.setObservacao(detalhe.getObsComissaoExec());
      dto.setComissaoExecutiva(ce);
    }

    return dto;
  }

  /**
   * Valida o período pedido contra o ciclo do ano. Em branco devolve {@code null} — leitura
   * sem medições — em vez de adivinhar um período.
   */
  private String resolverPeriodo(AvaliacaoEntity avaliacao, String periodicidade) {
    if (!StringUtils.hasText(periodicidade)) {
      return null;
    }
    return periodoService.validarPeriodo(avaliacao.getAno(), periodicidade);
  }

  private void fillHeader(AvaliacaoEntity entity, AvaliacaoDTO target, String periodo) {
    target.setId(entity.getId());
    target.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    target.setAno(entity.getAno());
    target.setPesoComportamentais(entity.getPesoComportamentais());
    target.setPesoTecnica(entity.getPesoTecnica());
    target.setPeriodicidade(periodo);
    target.setPeriodicidadesDefinidas(periodoService.detalhesDe(entity.getId()).stream()
        .map(d -> d.getPeriodicidade())
        .sorted()
        .toList());
    target.setAbrangencia(entity.getAbrangencia());
    target.setEstado(entity.getEstado());
    // O ecrã de avaliação mostra Direção / Unidade / Carreira / Cargo pelo nome,
    // por isso o id sozinho não chega ao cliente.
    ofNullable(entity.getInstitId()).ifPresent(i -> {
      target.setInstitId(i.getId());
      target.setInstituicaoNome(i.getNome());
    });
    ofNullable(entity.getSeccaoId()).ifPresent(s -> {
      target.setSeccaoId(s.getId());
      target.setSeccaoNome(s.getNome());
    });
    ofNullable(entity.getCargo()).ifPresent(c -> {
      target.setCargoId(c.getId());
      target.setCargoNome(c.getNome());
    });
    ofNullable(entity.getCarreira()).ifPresent(c -> {
      target.setCarrPccsId(c.getId());
      target.setCarrPccsNome(c.getNome());
    });
    ofNullable(entity.getFuncionario()).ifPresent(f -> {
      target.setNomeColaborador(f.getNome());
      target.setUuidColaborador(f.getUuid());
    });
  }

  private ObjectivoAvaliacaoDTO toObjectivo(AvaliacaoObjectivoEntity e,
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    var dto = new ObjectivoAvaliacaoDTO();
    dto.setParamId(e.getParamObjetivo() != null ? e.getParamObjetivo().getId() : null);
    dto.setNumero(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setObjectivo(e.getObjectivos());
    dto.setKpi(e.getKpi());
    dto.setPonderacao(e.getPonderacao());
    dto.setMeta(e.getMeta());

    var m = medicoes.get(chave(ComponenteAvaliacaoRef.OBJECTIVO, e.getId()));
    if (m != null) {
      dto.setRealizado(m.getRealizado());
      dto.setAvaliacao(m.getAvaliacaoValor());
      dto.setAutoRealizado(m.getAutoRealizado());
      dto.setAutoAvaliacao(m.getAutoAvaliacao());
      dto.setResultado(escala2(aplicarPercentagem(m.getAvaliacaoValor(), e.getPonderacao())));
      dto.setAutoResultado(escala2(aplicarPercentagem(m.getAutoAvaliacao(), e.getPonderacao())));
    }
    return dto;
  }

  private CompetenciaComportAvaliacaoDTO toCompetenciaComportamental(AvaliacaoCompetenciaEntity e,
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    var dto = new CompetenciaComportAvaliacaoDTO();
    dto.setNumeroOrdem(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setCompetencia(e.getDescricao());
    dto.setPeso(e.getPeso());
    dto.setPonderacao(e.getPonderacao());
    aplicarMedicao(medicoes, ComponenteAvaliacaoRef.COMPETENCIA_COMPORTAMENTAIS, e.getId(),
        e.getPonderacao(), dto::setAvaliacao, dto::setAutoAvaliacao, dto::setResultado, dto::setAutoResultado);
    return dto;
  }

  private CompetenciaTecAvaliacaoDTO toCompetenciaTecnica(AvaliacaoCompetenciaEntity e,
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    var dto = new CompetenciaTecAvaliacaoDTO();
    dto.setNumeroOrdem(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setCompetencia(e.getDescricao());
    dto.setPeso(e.getPeso());
    dto.setPonderacao(e.getPonderacao());
    aplicarMedicao(medicoes, ComponenteAvaliacaoRef.COMPETENCIA_TECNICA, e.getId(),
        e.getPonderacao(), dto::setAvaliacao, dto::setAutoAvaliacao, dto::setResultado, dto::setAutoResultado);
    return dto;
  }

  private AtitudePessoalAvaliacaoDTO toAtitude(AvaliacaoAtitudePessoalEntity e,
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    var dto = new AtitudePessoalAvaliacaoDTO();
    // Ler do próprio registo; fallback ao paramObjetivo para dados migrados antes do V3
    dto.setNumeroOrdem(e.getNumeroOrdem() != null
        ? e.getNumeroOrdem()
        : (e.getParamObjetivo() != null ? e.getParamObjetivo().getNumeroOrdem() : null));
    dto.setAbrangencia(e.getAbrangencia());
    dto.setAtitudePessoal(e.getDescricao() != null
        ? e.getDescricao()
        : (e.getParamObjetivo() != null ? e.getParamObjetivo().getDescricao() : null));
    dto.setPonderacao(e.getPonderacao());
    aplicarMedicao(medicoes, ComponenteAvaliacaoRef.ATITUDE_PESSOAL, e.getId(),
        e.getPonderacao(), dto::setAvaliacao, dto::setAutoAvaliacao, dto::setResultado, dto::setAutoResultado);
    return dto;
  }

  /** As três componentes só diferem nos setters: aqui partilha-se a leitura da medição. */
  private void aplicarMedicao(
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes,
      ComponenteAvaliacaoRef referencia, Long referenciaId, BigDecimal ponderacao,
      java.util.function.Consumer<BigDecimal> setAvaliacao,
      java.util.function.Consumer<BigDecimal> setAutoAvaliacao,
      java.util.function.Consumer<BigDecimal> setResultado,
      java.util.function.Consumer<BigDecimal> setAutoResultado) {

    var m = medicoes.get(chave(referencia, referenciaId));
    if (m == null) {
      return;
    }
    setAvaliacao.accept(m.getAvaliacaoValor());
    setAutoAvaliacao.accept(m.getAutoAvaliacao());
    setResultado.accept(escala2(aplicarPercentagem(m.getAvaliacaoValor(), ponderacao)));
    setAutoResultado.accept(escala2(aplicarPercentagem(m.getAutoAvaliacao(), ponderacao)));
  }

  private boolean hasText(String s) {
    return s != null && !s.trim().isEmpty();
  }

  private UUID parseUuid(String raw) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "UUID inválido: " + raw);
    }
  }
}
