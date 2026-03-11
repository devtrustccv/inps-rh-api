package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.CreateComponentesAvaliacaoCommand;
import cv.inps.rh.configuracao.infrastructure.mappers.ComponenteAvaliacaoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ComponenteAvaliacaoService {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);
  private static final String ESTADO_ATIVO = "A";
  private static final String ABRANGENCIA_DEFAULT = "INPS";

  private final ParamObjetivoDetEntityRepository detRepository;
  private final ComponenteAvaliacaoMapper mapper;

  public ComponenteAvaliacaoService(
      ParamObjetivoDetEntityRepository detRepository,
      ComponenteAvaliacaoMapper mapper) {
    this.detRepository = detRepository;
    this.mapper = mapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> registar(CreateComponentesAvaliacaoCommand command) {

    var dto = command.getComponenteavaliacaorequest();

    if (detRepository.existsByAno(dto.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + dto.getAno());
    }

    var somaPonderacoes = dto.getPonderacaoObjetivo()
        .add(dto.getPonderacaoCompetencia())
        .add(dto.getPonderacaoAtitudePessoal());
    if (somaPonderacoes.compareTo(CEM) != 0) {
      throw IgrpResponseStatusException.badRequest("A soma das ponderações globais deve ser 100%");
    }

    var somaPesosCompetencias = dto.getPesoComportamentais().add(dto.getPesoTecnica());
    if (somaPesosCompetencias.compareTo(CEM) != 0) {
      throw IgrpResponseStatusException.badRequest("A soma dos pesos das competências deve ser 100%");
    }

    var det = new ParamObjetivoDetEntity();
    det.setUuid(UuidCreator.getTimeOrderedEpoch());
    det.setAno(dto.getAno());
    det.setPesoComportamentais(dto.getPesoComportamentais());
    det.setPesoTecnica(dto.getPesoTecnica());
    det.setPonderacaoObjetivo(dto.getPonderacaoObjetivo());
    det.setPonderacaoCompetencia(dto.getPonderacaoCompetencia());
    det.setPonderacaoAtitudePess(dto.getPonderacaoAtitudePessoal());
    det.setEstado(ESTADO_ATIVO);

    var linhas = new ArrayList<ParamObjetivoEntity>(dto.getObjectivosInps().size()
        + dto.getCompetenciasComportamentais().size()
        + dto.getCompetenciasTecnicas().size()
        + dto.getAtitudesPessoais().size());

    dto.getObjectivosInps().forEach(r -> {
      var e = mapper.toEntity(det, r, "OBJETIVO");
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasComportamentais().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_COMPORTAMENTAL", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasTecnicas().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_TECNICA", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    for (int i = 0; i < dto.getAtitudesPessoais().size(); i++) {
      var r = dto.getAtitudesPessoais().get(i);
      var e = mapper.toEntity(det, r, "ATITUDE_PESSOAL", ABRANGENCIA_DEFAULT, i + 1);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    }

    det.setObjetivos(linhas);

    detRepository.save(det);

    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", det.getUuid()));
  }
}
