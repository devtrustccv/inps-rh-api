package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoObjectivosComunsDTO;
import cv.inps.rh.avaliacao.application.dto.BlocoObjectivosComunsDTO;
import cv.inps.rh.avaliacao.application.dto.LinhaAvaliacaoComumDTO;
import cv.inps.rh.avaliacao.application.dto.LinhaObjectivoComumDTO;
import cv.inps.rh.avaliacao.application.dto.ObjectivosComunsAnoDTO;
import cv.inps.rh.avaliacao.application.dto.ObjectivosComunsDTO;
import cv.inps.rh.avaliacao.application.dto.PeriodoAvaliadoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaObjectivosComunsDTO;
import cv.inps.rh.shared.application.constants.AbrangenciaAvaliacao;
import cv.inps.rh.shared.application.constants.ComponenteAvaliacaoRef;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoPeriodicidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoPeriodicidadeEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.aplicarPercentagem;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.chave;
import static cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService.escala2;

/**
 * "Objectivos / Avaliação Comuns" (spec de 24/09): a lista por ano e a avaliação por período
 * dos objectivos INPS e DIRECAO.
 *
 * <p>Os objectivos comuns de um ano são várias RH_T_AVD sem colaborador — a do INPS e uma por
 * direção — mas o ecrã trabalha por ano, por isso tudo aqui é chaveado por {@code ano}.</p>
 *
 * <p>A avaliação segue a gravação da spec para esta secção e mais nada: "caso for avaliação,
 * grava na tabela RH_T_AVD_PERIODICIDADE" com {@code REFERENCIA = OBJECTIVO},
 * {@code TIPO_PROCESSO = AVALIACAO}, o período, o realizado e a avaliação. Não calcula
 * RH_T_AVD_DETALHE nem mexe em RH_T_AVD.ESTADO, que são regras do processo de avaliação
 * individual.</p>
 */
@Service
public class ObjectivosComunsService {

  private static final String ESTADO_ELIMINADO = "E";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoPeriodicidadeEntityRepository periodicidadeRepository;
  private final AvaliacaoPeriodoService periodoService;

  public ObjectivosComunsService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoPeriodicidadeEntityRepository periodicidadeRepository,
      AvaliacaoPeriodoService periodoService) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.periodicidadeRepository = periodicidadeRepository;
    this.periodoService = periodoService;
  }

  // ------------------------------------------------------------------- lista

  @Transactional(readOnly = true)
  public WrapperListaObjectivosComunsDTO listar(Integer ano, String pageNumber, String pageSize) {
    var numero = StringUtils.hasText(pageNumber) ? Integer.parseInt(pageNumber) : 0;
    var tamanho = StringUtils.hasText(pageSize) ? Integer.parseInt(pageSize) : 20;

    var page = avaliacaoRepository.findAnosComuns(ano, PageRequest.of(numero, tamanho));
    var anos = page.getContent();

    // Um único select para os períodos de todos os anos da página.
    var periodosPorAno = anos.isEmpty()
        ? Map.<Integer, List<String>>of()
        : periodicidadeRepository.findPeriodosAvaliadosComuns(anos).stream()
            .collect(Collectors.groupingBy(
                AvaliacaoPeriodicidadeEntityRepository.AnoPeriodicidade::getAno,
                Collectors.mapping(AvaliacaoPeriodicidadeEntityRepository.AnoPeriodicidade::getPeriodicidade,
                    Collectors.toList())));

    var rotulos = periodoService.descricoesDosPeriodos();

    var response = new WrapperListaObjectivosComunsDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(anos.stream().map(a -> {
      var linha = new ObjectivosComunsAnoDTO();
      linha.setAno(a);
      linha.setPeriodos(ordenarPeriodos(a, periodosPorAno.getOrDefault(a, List.of())).stream()
          .map(p -> new PeriodoAvaliadoDTO(p, rotulos.getOrDefault(p, p)))
          .toList());
      return linha;
    }).toList());
    return response;
  }

  /**
   * Os períodos pela ordem do ciclo parametrizado no ano. Sem parametrização não há ciclo
   * conhecido, e ordena-se pelo código.
   */
  private List<String> ordenarPeriodos(Integer ano, List<String> periodos) {
    try {
      var ordem = periodoService.tipoDoAno(ano).periodos();
      return periodos.stream()
          .sorted(Comparator.comparingInt(p -> {
            var i = ordem.indexOf(p);
            return i < 0 ? Integer.MAX_VALUE : i;
          }))
          .toList();
    } catch (RuntimeException e) {
      return periodos.stream().sorted().toList();
    }
  }

  // ----------------------------------------------------------------- leitura

  @Transactional(readOnly = true)
  public ObjectivosComunsDTO obter(Integer ano, String periodicidade) {
    var comuns = comunsDoAno(ano);
    var periodo = StringUtils.hasText(periodicidade)
        ? periodoService.validarPeriodo(ano, periodicidade)
        : null;

    var ids = comuns.stream().map(AvaliacaoEntity::getId).toList();

    var objectivosPorAvd = objectivosAtivos(ids).stream()
        .collect(Collectors.groupingBy(o -> o.getAvaliacaoObj().getId()));

    // Sem período, as linhas vêm sem medições.
    var medicoes = periodo != null
        ? periodicidadeRepository.findAllByAvaliacao_IdInAndPeriodicidade(ids, periodo).stream()
            .collect(Collectors.toMap(m -> chave(m.getReferencia(), m.getReferenciaId()),
                Function.identity(), (a, b) -> a))
        : Map.<String, AvaliacaoPeriodicidadeEntity>of();

    var dto = new ObjectivosComunsDTO();
    dto.setAno(ano);
    dto.setPeriodicidade(periodo);
    dto.setPeriodicidadeDescricao(periodo != null
        ? periodoService.descricoesDosPeriodos().getOrDefault(periodo, periodo)
        : null);

    var direcoes = new ArrayList<BlocoObjectivosComunsDTO>();
    for (var avd : comuns) {
      var bloco = new BlocoObjectivosComunsDTO();
      bloco.setUuid(avd.getUuid() != null ? avd.getUuid().toString() : null);
      bloco.setAbrangencia(avd.getAbrangencia());
      if (avd.getInstitId() != null) {
        bloco.setInstitId(avd.getInstitId().getId());
        bloco.setInstitNome(avd.getInstitId().getNome());
      }
      bloco.setObjectivos(objectivosPorAvd.getOrDefault(avd.getId(), List.of()).stream()
          .sorted(Comparator.comparing(AvaliacaoObjectivoEntity::getNumeroOrdem,
              Comparator.nullsLast(Comparator.naturalOrder())))
          .map(o -> toLinha(o, medicoes))
          .toList());

      if (eInps(avd)) {
        // A definição é idempotente por ano, por isso só há uma do INPS.
        if (dto.getInstituicao() == null) {
          dto.setInstituicao(bloco);
        }
      } else {
        direcoes.add(bloco);
      }
    }
    direcoes.sort(Comparator.comparing(BlocoObjectivosComunsDTO::getInstitNome,
        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
    dto.setDirecoes(direcoes);
    return dto;
  }

  private LinhaObjectivoComumDTO toLinha(AvaliacaoObjectivoEntity o,
      Map<String, AvaliacaoPeriodicidadeEntity> medicoes) {
    var linha = new LinhaObjectivoComumDTO();
    linha.setId(o.getUuid() != null ? o.getUuid().toString() : null);
    linha.setParamId(o.getParamObjetivo() != null ? o.getParamObjetivo().getId() : null);
    linha.setNumero(o.getNumeroOrdem());
    linha.setAbrangencia(o.getAbrangencia());
    linha.setObjectivo(o.getObjectivos());
    linha.setKpi(o.getKpi());
    linha.setMeta(o.getMeta());
    linha.setPonderacao(o.getPonderacao());

    var m = medicoes.get(chave(ComponenteAvaliacaoRef.OBJECTIVO, o.getId()));
    if (m != null) {
      linha.setRealizado(m.getRealizado());
      linha.setAvaliacao(m.getAvaliacaoValor());
      // Sem nota não há resultado: o helper devolveria 0, que se leria como uma nota zero.
      if (m.getAvaliacaoValor() != null) {
        linha.setResultado(escala2(aplicarPercentagem(m.getAvaliacaoValor(), o.getPonderacao())));
      }
    }
    return linha;
  }

  // ---------------------------------------------------------------- avaliação

  @Transactional
  public SuccessResponseDTO avaliar(Integer ano, String periodicidade, AvaliacaoObjectivosComunsDTO dto) {
    var comuns = comunsDoAno(ano);
    var periodo = periodoService.validarPeriodo(ano, periodicidade);

    var instituicao = dto != null && dto.getInstituicao() != null ? dto.getInstituicao() : List.<LinhaAvaliacaoComumDTO>of();
    var direcoes = dto != null && dto.getDirecoes() != null ? dto.getDirecoes() : List.<LinhaAvaliacaoComumDTO>of();
    if (instituicao.isEmpty() && direcoes.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "Não foi enviada nenhuma linha de objectivo para avaliar.");
    }

    var avdPorId = comuns.stream().collect(Collectors.toMap(AvaliacaoEntity::getId, Function.identity()));
    var ids = List.copyOf(avdPorId.keySet());
    var objectivoPorUuid = objectivosAtivos(ids).stream()
        .filter(o -> o.getUuid() != null)
        .collect(Collectors.toMap(AvaliacaoObjectivoEntity::getUuid, Function.identity()));

    // Resolve e valida tudo antes de escrever, para um erro não deixar o período meio gravado.
    var aGravar = new LinkedHashMap<AvaliacaoObjectivoEntity, LinhaAvaliacaoComumDTO>();
    var vistos = new HashSet<UUID>();
    resolverLinhas(instituicao, true, ano, avdPorId, objectivoPorUuid, vistos, aGravar);
    resolverLinhas(direcoes, false, ano, avdPorId, objectivoPorUuid, vistos, aGravar);

    aGravar.forEach((o, l) -> periodoService.validarNota(l.getAvaliacao(), descrever(o, avdPorId)));

    var existentes = periodicidadeRepository.findAllByAvaliacao_IdInAndPeriodicidade(ids, periodo).stream()
        .collect(Collectors.toMap(m -> chave(m.getReferencia(), m.getReferenciaId()),
            Function.identity(), (a, b) -> a));

    var alteradas = new ArrayList<AvaliacaoPeriodicidadeEntity>();
    aGravar.forEach((o, l) -> {
      var medicao = existentes.get(chave(ComponenteAvaliacaoRef.OBJECTIVO, o.getId()));
      if (medicao == null) {
        medicao = periodoService.obterOuCriarMedicao(
            avdPorId.get(o.getAvaliacaoObj().getId()), periodo, ComponenteAvaliacaoRef.OBJECTIVO, o.getId());
      }
      medicao.setRealizado(l.getRealizado());
      medicao.setAvaliacaoValor(l.getAvaliacao());
      alteradas.add(medicao);
    });
    periodoService.guardarMedicoes(alteradas);

    var resposta = new SuccessResponseDTO();
    resposta.setSucesso(true);
    resposta.setId(String.valueOf(ano));
    resposta.setMensagem("Avaliação dos objectivos comuns de " + ano + " no período " + periodo
        + " gravada (" + alteradas.size() + " objectivo(s)).");
    return resposta;
  }

  /**
   * Associa cada linha do pedido ao seu objectivo. A linha tem de ser de um objectivo comum
   * deste ano e do bloco em que veio: INPS em {@code instituicao}, DIRECAO em {@code direcoes}.
   */
  private void resolverLinhas(
      List<LinhaAvaliacaoComumDTO> linhas, boolean blocoInstituicao, Integer ano,
      Map<Long, AvaliacaoEntity> avdPorId, Map<UUID, AvaliacaoObjectivoEntity> objectivoPorUuid,
      HashSet<UUID> vistos, Map<AvaliacaoObjectivoEntity, LinhaAvaliacaoComumDTO> aGravar) {

    var bloco = blocoInstituicao ? "instituicao" : "direcoes";
    for (var l : linhas) {
      if (l == null || l.getId() == null) {
        throw IgrpResponseStatusException.badRequest("Linha sem id em " + bloco + ".");
      }
      if (!vistos.add(l.getId())) {
        throw IgrpResponseStatusException.badRequest("O objectivo " + l.getId() + " vem repetido no pedido.");
      }
      var objectivo = objectivoPorUuid.get(l.getId());
      if (objectivo == null) {
        throw IgrpResponseStatusException.badRequest(
            "O objectivo " + l.getId() + " não pertence aos objectivos comuns do ano " + ano + ".");
      }
      var avd = avdPorId.get(objectivo.getAvaliacaoObj().getId());
      if (eInps(avd) != blocoInstituicao) {
        throw IgrpResponseStatusException.badRequest(
            "O objectivo " + l.getId() + " é de abrangência " + avd.getAbrangencia()
                + " e não pertence ao bloco " + bloco + ".");
      }
      aGravar.put(objectivo, l);
    }
  }

  private String descrever(AvaliacaoObjectivoEntity o, Map<Long, AvaliacaoEntity> avdPorId) {
    var avd = avdPorId.get(o.getAvaliacaoObj().getId());
    var onde = avd.getInstitId() != null ? " da direção " + avd.getInstitId().getNome() : " do INPS";
    return "objectivo " + o.getNumeroOrdem() + onde;
  }

  // ------------------------------------------------------------------ comum

  private List<AvaliacaoEntity> comunsDoAno(Integer ano) {
    var comuns = avaliacaoRepository.findComunsDoAno(ano);
    if (comuns.isEmpty()) {
      throw IgrpResponseStatusException.notFound(
          "Não há objectivos comuns definidos para o ano " + ano + ".");
    }
    return comuns;
  }

  private List<AvaliacaoObjectivoEntity> objectivosAtivos(List<Long> avdIds) {
    return objectivoRepository.findAllByAvaliacaoObj_IdIn(avdIds).stream()
        .filter(o -> o.getEstado() == null || !ESTADO_ELIMINADO.equalsIgnoreCase(o.getEstado()))
        .toList();
  }

  private boolean eInps(AvaliacaoEntity avd) {
    return AbrangenciaAvaliacao.INPS.name().equalsIgnoreCase(avd.getAbrangencia());
  }
}
