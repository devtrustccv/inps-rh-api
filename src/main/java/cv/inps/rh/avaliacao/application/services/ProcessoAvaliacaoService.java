package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.ComissaoExecutivaDTO;
import cv.inps.rh.avaliacao.application.dto.ObservacaoGeralDTO;
import cv.inps.rh.avaliacao.application.dto.ParecerColaboradorDTO;
import cv.inps.rh.shared.application.constants.ComponenteAvaliacaoRef;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.aplicarPercentagem;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.chave;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.escala2;

/**
 * Escrita da avaliação e da autoavaliação, e cálculo do resultado do período.
 *
 * <p>Depois do refactor de 21/09 os valores medidos já não vivem nas linhas das componentes
 * mas em RH_T_AVD_PERIODICIDADE, uma linha por (componente, período); e o resultado calculado
 * vai para RH_T_AVD_DETALHE, uma linha por período. A ponderação continua na componente,
 * porque é da definição do objectivo e não muda de período para período.</p>
 */
@Service
public class ProcessoAvaliacaoService {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_PARCIAL = "P";
  private static final String ESTADO_CONCLUIDO = "C";
  private static final String ESTADO_ELIMINADO = "E";

  private static final String COMP_COMPORTAMENTAL = "COMPETENCIA_COMPORTAMENTAL";
  private static final String COMP_TECNICA = "COMPETENCIA_TECNICA";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;
  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaRepository;
  private final AvaliacaoPeriodoService periodoService;

  public ProcessoAvaliacaoService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository,
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ParamEscalaAvaliacaoEntityRepository escalaRepository,
      AvaliacaoPeriodoService periodoService) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
    this.objetivoDetRepository = objetivoDetRepository;
    this.escalaRepository = escalaRepository;
    this.periodoService = periodoService;
  }

  // ------------------------------------------------------------- avaliação

  @Transactional
  public ResponseEntity<SuccessResponseDTO> gravarAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);
    var periodo = periodoService.validarPeriodo(avaliacao.getAno(), periodicidadeDe(dto));
    validarNotas(dto);

    aplicarMedicoes(avaliacao, periodo, dto, (medicao, valores) -> {
      medicao.setRealizado(valores.realizado());
      medicao.setAvaliacaoValor(valores.nota());
    });

    recalcularPeriodo(avaliacao, periodo);
    atualizarEstado(avaliacao, periodo);
    avaliacaoRepository.save(avaliacao);

    return ResponseEntity.ok(sucesso(avaliacao,
        "Avaliação do período " + periodo + " gravada."));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> gravarAutoAvaliacao(String uuid, AvaliacaoDTO dto) {
    var avaliacao = load(uuid);
    var periodo = periodoService.validarPeriodo(avaliacao.getAno(), periodicidadeDe(dto));
    validarNotas(dto);

    // A autoavaliação não recalcula o resultado do período: a nota oficial é a do avaliador.
    aplicarMedicoes(avaliacao, periodo, dto, (medicao, valores) -> {
      medicao.setAutoRealizado(valores.realizado());
      medicao.setAutoAvaliacao(valores.nota());
    });

    return ResponseEntity.ok(sucesso(avaliacao,
        "Autoavaliação do período " + periodo + " gravada."));
  }

  /**
   * Percorre as componentes do pedido e escreve cada valor na respectiva medição do período.
   * O {@code escritor} decide se está a preencher os campos do avaliador ou os da
   * autoavaliação — é a única coisa que difere entre os dois fluxos.
   */
  private void aplicarMedicoes(AvaliacaoEntity avaliacao, String periodo, AvaliacaoDTO dto,
      BiConsumer<AvaliacaoPeriodicidadeEntity, Valores> escritor) {

    if (dto == null) {
      return;
    }

    var existentes = periodoService.medicoesDoPeriodo(avaliacao.getId(), periodo);
    var alteradas = new ArrayList<AvaliacaoPeriodicidadeEntity>();

    if (dto.getObjectivos() != null) {
      var objectivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
      dto.getObjectivos().forEach(o -> {
        if (o == null || o.getNumero() == null) {
          return;
        }
        objectivos.stream()
            .filter(e -> e != null && o.getNumero().equals(e.getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> alteradas.add(escrever(existentes, escritor, avaliacao, periodo,
                ComponenteAvaliacaoRef.OBJECTIVO, e.getId(),
                new Valores(o.getRealizado(), o.getAvaliacao()))));
      });
    }

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());

    if (dto.getCompetenciasComportamentais() != null) {
      dto.getCompetenciasComportamentais().forEach(c -> {
        if (c == null || c.getNumeroOrdem() == null) {
          return;
        }
        competenciaPorOrdem(competencias, COMP_COMPORTAMENTAL, c.getNumeroOrdem())
            .ifPresent(e -> alteradas.add(escrever(existentes, escritor, avaliacao, periodo,
                ComponenteAvaliacaoRef.COMPETENCIA_COMPORTAMENTAIS, e.getId(),
                new Valores(null, c.getAvaliacao()))));
      });
    }

    if (dto.getCompetenciasTecnicas() != null) {
      dto.getCompetenciasTecnicas().forEach(c -> {
        if (c == null || c.getNumeroOrdem() == null) {
          return;
        }
        competenciaPorOrdem(competencias, COMP_TECNICA, c.getNumeroOrdem())
            .ifPresent(e -> alteradas.add(escrever(existentes, escritor, avaliacao, periodo,
                ComponenteAvaliacaoRef.COMPETENCIA_TECNICA, e.getId(),
                new Valores(null, c.getAvaliacao()))));
      });
    }

    if (dto.getAtitudesPessoais() != null) {
      var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
      dto.getAtitudesPessoais().forEach(a -> {
        if (a == null || a.getNumeroOrdem() == null) {
          return;
        }
        atitudes.stream()
            .filter(e -> e != null && e.getParamObjetivo() != null
                && a.getNumeroOrdem().equals(e.getParamObjetivo().getNumeroOrdem()))
            .findFirst()
            .ifPresent(e -> alteradas.add(escrever(existentes, escritor, avaliacao, periodo,
                ComponenteAvaliacaoRef.ATITUDE_PESSOAL, e.getId(),
                new Valores(null, a.getAvaliacao()))));
      });
    }

    periodoService.guardarMedicoes(alteradas);
  }

  private AvaliacaoPeriodicidadeEntity escrever(
      Map<String, AvaliacaoPeriodicidadeEntity> existentes,
      BiConsumer<AvaliacaoPeriodicidadeEntity, Valores> escritor,
      AvaliacaoEntity avaliacao, String periodo,
      ComponenteAvaliacaoRef referencia, Long referenciaId, Valores valores) {

    var medicao = existentes.get(chave(referencia, referenciaId));
    if (medicao == null) {
      medicao = periodoService.obterOuCriarMedicao(avaliacao, periodo, referencia, referenciaId);
      existentes.put(chave(referencia, referenciaId), medicao);
    }
    escritor.accept(medicao, valores);
    return medicao;
  }

  private java.util.Optional<AvaliacaoCompetenciaEntity> competenciaPorOrdem(
      List<AvaliacaoCompetenciaEntity> competencias, String componente, Integer numeroOrdem) {
    if (competencias == null) {
      return java.util.Optional.empty();
    }
    return competencias.stream()
        .filter(e -> e != null
            && componente.equalsIgnoreCase(e.getComponente())
            && numeroOrdem.equals(e.getNumeroOrdem()))
        .findFirst();
  }

  /** Realizado e nota de uma componente, tal como vêm do pedido. */
  private record Valores(String realizado, BigDecimal nota) {
  }

  /**
   * Valida todas as notas do pedido antes de escrever seja o que for, para uma nota
   * inválida não deixar o período meio gravado.
   */
  private void validarNotas(AvaliacaoDTO dto) {
    if (dto == null) {
      return;
    }
    if (dto.getObjectivos() != null) {
      dto.getObjectivos().forEach(o -> {
        if (o != null) periodoService.validarNota(o.getAvaliacao(), "objectivo " + o.getNumero());
      });
    }
    if (dto.getCompetenciasComportamentais() != null) {
      dto.getCompetenciasComportamentais().forEach(c -> {
        if (c != null) periodoService.validarNota(c.getAvaliacao(),
            "competência comportamental " + c.getNumeroOrdem());
      });
    }
    if (dto.getCompetenciasTecnicas() != null) {
      dto.getCompetenciasTecnicas().forEach(c -> {
        if (c != null) periodoService.validarNota(c.getAvaliacao(),
            "competência técnica " + c.getNumeroOrdem());
      });
    }
    if (dto.getAtitudesPessoais() != null) {
      dto.getAtitudesPessoais().forEach(a -> {
        if (a != null) periodoService.validarNota(a.getAvaliacao(),
            "atitude pessoal " + a.getNumeroOrdem());
      });
    }
  }

  // ------------------------------------------------- observações e pareceres

  @Transactional
  public ResponseEntity<SuccessResponseDTO> gravarObservacaoGeral(
      String uuid, String periodicidade, ObservacaoGeralDTO dto) {
    var avaliacao = load(uuid);
    var periodo = periodoService.validarPeriodo(avaliacao.getAno(), periodicidade);
    var detalhe = periodoService.obterOuCriarDetalhe(avaliacao, periodo);

    if (dto != null) {
      detalhe.setObservacaoGeral(dto.getObservacaoGeralAvaliacao());
      detalhe.setDescricaoPlano(dto.getDescPlanoDesenvolvimento());
      detalhe.setDataInicioEntrevista(dto.getDataInicio());
      detalhe.setHoraInicioEntrevista(dto.getHoraInicio());
      detalhe.setHoraFimEntrevista(dto.getHoraFim());
      periodoService.guardarDetalhe(detalhe);
    }

    return ResponseEntity.ok(sucesso(avaliacao, "Observação geral do período " + periodo + " gravada."));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> gravarParecerColaborador(
      String uuid, String periodicidade, ParecerColaboradorDTO dto) {
    var avaliacao = load(uuid);
    var periodo = periodoService.validarPeriodo(avaliacao.getAno(), periodicidade);
    var detalhe = periodoService.obterOuCriarDetalhe(avaliacao, periodo);

    if (dto != null) {
      detalhe.setParecerColaborador(dto.getParecer());
      detalhe.setJustificacaoMotivo(dto.getJustificar());
      periodoService.guardarDetalhe(detalhe);
    }

    return ResponseEntity.ok(sucesso(avaliacao, "Parecer do colaborador do período " + periodo + " gravado."));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> gravarComissaoExecutiva(
      String uuid, String periodicidade, ComissaoExecutivaDTO dto) {
    var avaliacao = load(uuid);
    var periodo = periodoService.validarPeriodo(avaliacao.getAno(), periodicidade);
    var detalhe = periodoService.obterOuCriarDetalhe(avaliacao, periodo);

    if (dto != null) {
      detalhe.setObsComissaoExec(dto.getObservacao());
      periodoService.guardarDetalhe(detalhe);
    }

    return ResponseEntity.ok(sucesso(avaliacao, "Observação da comissão executiva do período "
        + periodo + " gravada."));
  }

  // ------------------------------------------------------------- cálculo

  /**
   * Recalcula as notas do período a partir das medições e escreve-as no detalhe.
   *
   * <p>Cada componente contribui com {@code nota x ponderação da linha}; a soma de cada
   * família é depois pesada pela ponderação global do ano (objectivos / competências /
   * atitude pessoal), definida em RH_T_PARAM_OBJETIVO_DET.</p>
   */
  private void recalcularPeriodo(AvaliacaoEntity avaliacao, String periodo) {
    var ano = avaliacao.getAno();
    if (ano == null) {
      return;
    }
    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(ano).orElse(null);
    if (det == null) {
      return;
    }

    var medicoes = periodoService.medicoesDoPeriodo(avaliacao.getId(), periodo);

    var objectivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacao.getUuid());
    var resultadoObjectivos = objectivos.stream()
        .filter(this::ativa)
        .map(o -> aplicarPercentagem(
            nota(medicoes, ComponenteAvaliacaoRef.OBJECTIVO, o.getId()), o.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // As competências são duas famílias, cada uma com ponderações que somam 100%. Sem as
    // pesar por PESO_COMPORTAMENTAIS / PESO_TECNICA, juntas valeriam 200% e o resultado do
    // período saía fora da escala — ficando sem classificação qualitativa.
    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    var resultadoComportamentais = aplicarPercentagem(
        somaCompetencias(competencias, COMP_COMPORTAMENTAL,
            ComponenteAvaliacaoRef.COMPETENCIA_COMPORTAMENTAIS, medicoes),
        det.getPesoComportamentais());
    var resultadoTecnicas = aplicarPercentagem(
        somaCompetencias(competencias, COMP_TECNICA,
            ComponenteAvaliacaoRef.COMPETENCIA_TECNICA, medicoes),
        det.getPesoTecnica());

    var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(avaliacao.getUuid());
    var resultadoAtitudes = (atitudes == null ? List.<AvaliacaoAtitudePessoalEntity>of() : atitudes).stream()
        .filter(this::ativa)
        .map(a -> aplicarPercentagem(
            nota(medicoes, ComponenteAvaliacaoRef.ATITUDE_PESSOAL, a.getId()), a.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var avaliacaoObjectivo = aplicarPercentagem(resultadoObjectivos, det.getPonderacaoObjetivo());
    var avaliacaoCompetencia = aplicarPercentagem(
        resultadoComportamentais.add(resultadoTecnicas), det.getPonderacaoCompetencia());
    var avaliacaoAtitude = aplicarPercentagem(resultadoAtitudes, det.getPonderacaoAtitudePess());

    var avaliacaoFinal = avaliacaoObjectivo.add(avaliacaoCompetencia).add(avaliacaoAtitude);

    var detalhe = periodoService.obterOuCriarDetalhe(avaliacao, periodo);
    detalhe.setAvaliacaoObjectivo(escala2(avaliacaoObjectivo));
    detalhe.setAvaliacaoCompetencia(escala2(avaliacaoCompetencia));
    detalhe.setAvaliacaoAtitudePess(escala2(avaliacaoAtitude));
    detalhe.setAvaliacaoFinal(escala2(avaliacaoFinal));
    detalhe.setAvaliacaoQualitativa(resolveQualitativa(escalaRepository.findAll(), avaliacaoFinal));
    periodoService.guardarDetalhe(detalhe);
  }

  private BigDecimal somaCompetencias(List<AvaliacaoCompetenciaEntity> competencias, String componente,
      ComponenteAvaliacaoRef referencia, Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    if (competencias == null) {
      return BigDecimal.ZERO;
    }
    return competencias.stream()
        .filter(c -> c != null && componente.equalsIgnoreCase(c.getComponente()))
        .filter(c -> c.getEstado() == null || !ESTADO_ELIMINADO.equalsIgnoreCase(c.getEstado()))
        .map(c -> aplicarPercentagem(nota(medicoes, referencia, c.getId()), c.getPonderacao()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal nota(Map<String, AvaliacaoPeriodicidadeEntity> medicoes,
      ComponenteAvaliacaoRef referencia, Long referenciaId) {
    var m = medicoes.get(chave(referencia, referenciaId));
    return m != null ? m.getAvaliacaoValor() : null;
  }

  private boolean ativa(AvaliacaoObjectivoEntity o) {
    return o.getEstado() == null || !ESTADO_ELIMINADO.equalsIgnoreCase(o.getEstado());
  }

  private boolean ativa(AvaliacaoAtitudePessoalEntity a) {
    return a != null && (a.getEstado() == null || !ESTADO_ELIMINADO.equalsIgnoreCase(a.getEstado()));
  }

  /**
   * O estado da avaliação passa a 'C' quando todos os períodos do ciclo já têm nota, e a
   * 'P' enquanto faltar algum. Antes isto estava preso a "semestre 2 implica concluído",
   * que deixa de fazer sentido com ciclos trimestrais ou anuais.
   */
  private void atualizarEstado(AvaliacaoEntity avaliacao, String periodo) {
    var tipo = periodoService.tipoDoAno(avaliacao.getAno());
    var comNota = periodoService.detalhesDe(avaliacao.getId()).stream()
        .filter(d -> d.getAvaliacaoFinal() != null)
        .map(AvaliacaoDetalheEntity::getPeriodicidade)
        .collect(java.util.stream.Collectors.toSet());
    comNota.add(periodo);

    avaliacao.setEstado(comNota.containsAll(tipo.periodos()) ? ESTADO_CONCLUIDO : ESTADO_PARCIAL);
  }

  // ------------------------------------------------------------------ util

  private String periodicidadeDe(AvaliacaoDTO dto) {
    if (dto == null || !StringUtils.hasText(dto.getPeriodicidade())) {
      throw IgrpResponseStatusException.badRequest(
          "A periodicidade é obrigatória para saber a que período pertence a avaliação.");
    }
    return dto.getPeriodicidade();
  }

  private AvaliacaoEntity load(String uuid) {
    UUID parsed;
    try {
      parsed = UUID.fromString(uuid);
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido: " + uuid);
    }
    return avaliacaoRepository.findByUuidOrThrow(parsed);
  }

  private SuccessResponseDTO sucesso(AvaliacaoEntity avaliacao, String mensagem) {
    var dto = new SuccessResponseDTO();
    dto.setSucesso(true);
    dto.setId(avaliacao.getUuid() != null ? avaliacao.getUuid().toString() : null);
    dto.setMensagem(mensagem);
    return dto;
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal valor) {
    if (valor == null || escala == null) {
      return null;
    }
    for (var e : escala) {
      if (e == null || e.getEstado() != Estado.A) {
        continue;
      }
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null) {
        continue;
      }
      if (valor.compareTo(e.getQuantitativaDe()) >= 0 && valor.compareTo(e.getQuantitativaAte()) <= 0) {
        return e.getQualitativa();
      }
    }
    return null;
  }
}
