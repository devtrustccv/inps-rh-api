package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoFinalDTO;
import cv.inps.rh.avaliacao.application.dto.PonderacaoPeriodoDTO;
import cv.inps.rh.avaliacao.application.services.AvaliacaoPeriodoService;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Avaliação final do ano.
 *
 * <p>Reescrito no refactor de 21/09: deixou de somar "semestre 1 + semestre 2" lidos de duas
 * linhas de RH_T_AVD e passa a percorrer os períodos do ciclo parametrizado no ano, lendo
 * cada nota de RH_T_AVD_DETALHE e o respectivo peso do domínio AVD_PONDERACAO_FINAL. Assim
 * funciona igual para ciclos semestrais, trimestrais ou anuais.</p>
 */
@Component
public class GetAvaliacaoFinalQueryHandler implements QueryHandler<GetAvaliacaoFinalQuery, ResponseEntity<AvaliacaoFinalDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacaoFinalQueryHandler.class);

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaRepository;
  private final AvaliacaoPeriodoService periodoService;

  public GetAvaliacaoFinalQueryHandler(
      AvaliacaoEntityRepository avaliacaoRepository,
      ParamEscalaAvaliacaoEntityRepository escalaRepository,
      AvaliacaoPeriodoService periodoService
  ) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.escalaRepository = escalaRepository;
    this.periodoService = periodoService;
  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoFinalDTO> handle(GetAvaliacaoFinalQuery query) {

    LOGGER.debug("GetAvaliacaoFinalQuery: {}", query);

    var uuid = parseUuid(query.getUuid());
    var base = avaliacaoRepository.findByUuidOrThrow(uuid);

    if (base.getAno() == null) {
      throw IgrpResponseStatusException.notFound("Ano não encontrado para a avaliação: " + uuid);
    }

    var tipo = periodoService.tipoDoAno(base.getAno());
    var ponderacoes = periodoService.ponderacoesDoCiclo(tipo);
    var rotulos = periodoService.descricoesDosPeriodos();

    var detalhesPorPeriodo = periodoService.detalhesDe(base.getId()).stream()
        .collect(Collectors.toMap(
            AvaliacaoDetalheEntity::getPeriodicidade, Function.identity(), (a, b) -> a));

    var periodos = new ArrayList<PonderacaoPeriodoDTO>(tipo.periodos().size());
    BigDecimal notaFinal = null;

    for (var periodo : tipo.periodos()) {
      var detalhe = detalhesPorPeriodo.get(periodo);
      var ponderacao = ponderacoes.get(periodo);
      var nota = detalhe != null ? detalhe.getAvaliacaoFinal() : null;
      var contributo = nota != null ? AvaliacaoPeriodoService.aplicarPercentagem(nota, ponderacao) : null;

      var dto = new PonderacaoPeriodoDTO();
      dto.setPeriodicidade(periodo);
      dto.setDescricao(rotulos.getOrDefault(periodo, periodo));
      dto.setAvaliacaoFinal(AvaliacaoPeriodoService.escala2(nota));
      dto.setPonderacao(ponderacao);
      dto.setContributo(AvaliacaoPeriodoService.escala2(contributo));
      periodos.add(dto);

      if (contributo != null) {
        notaFinal = notaFinal == null ? contributo : notaFinal.add(contributo);
      }
    }

    notaFinal = AvaliacaoPeriodoService.escala2(notaFinal);

    var response = new AvaliacaoFinalDTO();
    response.setPeriodos(periodos);
    response.setAvaliacaoExpressivaQuantitativa(notaFinal != null ? notaFinal.toPlainString() : null);
    response.setAvaliacaoExpressivaQualitativa(resolveQualitativa(escalaRepository.findAll(), notaFinal));

    return ResponseEntity.ok(response);
  }

  private UUID parseUuid(String raw) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "UUID inválido: " + raw);
    }
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal notaFinal) {
    if (notaFinal == null || escala == null || escala.isEmpty()) {
      return null;
    }

    for (var e : escala) {
      if (e == null || e.getEstado() != Estado.A) continue;
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null) continue;
      boolean ge = notaFinal.compareTo(e.getQuantitativaDe()) >= 0;
      boolean le = notaFinal.compareTo(e.getQuantitativaAte()) <= 0;
      if (ge && le) {
        return e.getQualitativa();
      }
    }

    return null;
  }

}
