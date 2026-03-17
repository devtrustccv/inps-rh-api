package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.avaliacao.application.dto.SemestreDTO;
import cv.inps.rh.parametrizacao.domain.models.Dominio;
import cv.inps.rh.parametrizacao.domain.repository.DomainsRepository;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoFinalDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Component
public class GetAvaliacaoFinalQueryHandler implements QueryHandler<GetAvaliacaoFinalQuery, ResponseEntity<AvaliacaoFinalDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetAvaliacaoFinalQueryHandler.class);

  private static final String DOMINIO_PONDERACAO_FINAL = "AVD_PONDERACAO_FINAL";
  private static final String REF_SEMESTRE1 = "SEMESTRE1";
  private static final String REF_SEMESTRE2 = "SEMESTRE2";

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaRepository;
  private final DomainsRepository domainsRepository;

  public GetAvaliacaoFinalQueryHandler(
      AvaliacaoEntityRepository avaliacaoRepository,
      ParamEscalaAvaliacaoEntityRepository escalaRepository,
      DomainsRepository domainsRepository
  ) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.escalaRepository = escalaRepository;
    this.domainsRepository = domainsRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<AvaliacaoFinalDTO> handle(GetAvaliacaoFinalQuery query) {

    LOGGER.debug("GetAvaliacaoFinalQuery: {}", query);

    var uuid = parseUuid(query.getUuid());

    var base = avaliacaoRepository.findByUuidOrThrow(uuid);

    var fun = base.getFuncionario();
    if (fun == null || fun.getId() == null) {
      throw IgrpResponseStatusException.notFound("Colaborador não encontrado para a avaliação: " + uuid);
    }
    if (base.getAno() == null) {
      throw IgrpResponseStatusException.notFound("Ano não encontrado para a avaliação: " + uuid);
    }

    var ponderacaoSem1 = resolvePonderacaoPercent(REF_SEMESTRE1);
    var ponderacaoSem2 = resolvePonderacaoPercent(REF_SEMESTRE2);

    var avaliacoes = avaliacaoRepository.findAllByFuncionario_IdAndAno(fun.getId(), base.getAno());
    var semestre1 = avaliacoes.stream()
        .filter(a -> sameGrupo(a, base))
        .filter(a -> "1".equals(a.getSemestre()))
        .findFirst()
        .orElse(null);

    var semestre2 = avaliacoes.stream()
        .filter(a -> sameGrupo(a, base))
        .filter(a -> "2".equals(a.getSemestre()))
        .findFirst()
        .orElse(null);

    var notaFinal = sumPonderada(
        toBigDecimal(semestre1 != null ? semestre1.getAvaliacaoFinal() : null),
        ponderacaoSem1,
        toBigDecimal(semestre2 != null ? semestre2.getAvaliacaoFinal() : null),
        ponderacaoSem2
    );

    var response = new AvaliacaoFinalDTO();
    response.setPrimeiroSemestre(buildSemestre(semestre1, ponderacaoSem1));
    response.setSegundoSemestre(buildSemestre(semestre2, ponderacaoSem2));
    response.setAvaliacaoExpressivaQuantitativa(formatScale2(notaFinal));
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

  private boolean sameGrupo(AvaliacaoEntity a, AvaliacaoEntity base) {
    if (a == null || base == null) return false;

    Long institA = a.getInstitId() != null ? a.getInstitId().getId() : null;
    Long institB = base.getInstitId() != null ? base.getInstitId().getId() : null;
    if (!eq(institA, institB)) return false;

    Long seccaoA = a.getSeccaoId() != null ? a.getSeccaoId().getId() : null;
    Long seccaoB = base.getSeccaoId() != null ? base.getSeccaoId().getId() : null;
    if (!eq(seccaoA, seccaoB)) return false;

    Long cargoA = a.getCargo() != null ? a.getCargo().getId() : null;
    Long cargoB = base.getCargo() != null ? base.getCargo().getId() : null;
    if (!eq(cargoA, cargoB)) return false;

    Long carreiraA = a.getCarreira() != null ? a.getCarreira().getId() : null;
    Long carreiraB = base.getCarreira() != null ? base.getCarreira().getId() : null;
    return eq(carreiraA, carreiraB);
  }

  private boolean eq(Object a, Object b) {
    return a == null ? b == null : a.equals(b);
  }

  private SemestreDTO buildSemestre(AvaliacaoEntity avaliacao, BigDecimal ponderacaoPercent) {
    var dto = new SemestreDTO();
    dto.setAvaliacaoFinal(formatScale2(toBigDecimal(avaliacao != null ? avaliacao.getAvaliacaoFinal() : null)));
    dto.setPonderacao(ponderacaoPercent != null ? ponderacaoPercent.stripTrailingZeros().toPlainString() : null);
    return dto;
  }

  private BigDecimal resolvePonderacaoPercent(String referencia) {
    List<Dominio> dominios = domainsRepository.findAllByDominio(DOMINIO_PONDERACAO_FINAL, referencia);
    for (var d : dominios) {
      var parsed = parsePercent(d);
      if (parsed != null) return parsed;
    }
    throw IgrpResponseStatusException.notFound("Ponderação final não configurada para: " + referencia);
  }

  private BigDecimal parsePercent(Dominio d) {
    if (d == null) return null;
    var raw = d.getValor();
    if (raw == null || raw.isBlank()) raw = d.getDescricao();
    if (raw == null || raw.isBlank()) return null;

    var normalized = raw.replace("%", "").trim().replace(",", ".");
    try {
      return new BigDecimal(normalized);
    } catch (Exception e) {
      return null;
    }
  }

  private BigDecimal sumPonderada(BigDecimal nota1, BigDecimal p1, BigDecimal nota2, BigDecimal p2) {
    if (nota1 == null && nota2 == null) return null;

    var r1 = multiplyPercent(nota1 != null ? nota1 : BigDecimal.ZERO, p1);
    var r2 = multiplyPercent(nota2 != null ? nota2 : BigDecimal.ZERO, p2);
    return scale2(r1.add(r2));
  }

  private BigDecimal multiplyPercent(BigDecimal nota, BigDecimal percent) {
    if (nota == null || percent == null) return BigDecimal.ZERO;
    return nota.multiply(percent).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
  }

  private BigDecimal scale2(BigDecimal v) {
    if (v == null) return null;
    return v.setScale(2, RoundingMode.HALF_UP);
  }

  private String formatScale2(BigDecimal v) {
    var scaled = scale2(v);
    return scaled != null ? scaled.toPlainString() : null;
  }

  private BigDecimal toBigDecimal(Double v) {
    return v != null ? BigDecimal.valueOf(v) : null;
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal notaFinal) {
    if (notaFinal == null) return null;
    if (escala == null || escala.isEmpty()) return null;

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
