package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.*;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoAtitudePessoalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoCompetenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
public class AvaliacaoReadService {

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;

  public AvaliacaoReadService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository
  ) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
  }

  @Transactional(readOnly = true)
  public AvaliacaoDTO getAvaliacao(String uuid) {
    var id = parseUuid(uuid);

    var avaliacao = avaliacaoRepository.findByUuidOrThrow(id);

    var dto = new AvaliacaoDTO();
    fillHeader(avaliacao, dto);

    var objectivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(id).stream()
        .sorted(Comparator.comparing(AvaliacaoObjectivoEntity::getNumeroOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
        .map(this::toObjectivoAvaliacaoDTO)
        .toList();
    dto.setObjectivos(objectivos);

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(id).stream()
        .sorted(Comparator.comparing(AvaliacaoCompetenciaEntity::getNumeroOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();
    dto.setCompetenciasComportamentais(
        competencias.stream()
            .filter(c -> "COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(c.getComponente()))
            .map(this::toCompetenciaComportAvaliacaoDTO)
            .toList()
    );
    dto.setCompetenciasTecnicas(
        competencias.stream()
            .filter(c -> "COMPETENCIA_TECNICA".equalsIgnoreCase(c.getComponente()))
            .map(this::toCompetenciaTecAvaliacaoDTO)
            .toList()
    );

    dto.setAtitudesPessoais(
        atitudeRepository.findAllByAvaliacao_Uuid(id).stream()
            .sorted(Comparator.comparing(a -> a.getParamObjetivo() != null ? a.getParamObjetivo().getNumeroOrdem() : null,
                Comparator.nullsLast(Comparator.naturalOrder())))
            .map(this::toAtitudePessoalAvaliacaoDTO)
            .toList()
    );

    return dto;
  }

  @Transactional(readOnly = true)
  public AvaliacaoDTO getDefinicaoObjetivo(String uuid) {
    return getAvaliacao(uuid);
  }

  private UUID parseUuid(String raw) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "UUID inválido: " + raw);
    }
  }

  private void fillHeader(AvaliacaoEntity entity, AvaliacaoDTO target) {
    target.setId(entity.getId());
    target.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    target.setAno(entity.getAno());
    target.setSemestre(entity.getSemestre());
    target.setEstado(entity.getEstado());
    ofNullable(entity.getInstitId()).ifPresent(i -> target.setInstitId(i.getId()));
    ofNullable(entity.getSeccaoId()).ifPresent(s -> target.setSeccaoId(s.getId()));
    ofNullable(entity.getCargo()).ifPresent(c -> target.setCargoId(c.getId()));
    ofNullable(entity.getCarreira()).ifPresent(c -> target.setCarrPccsId(c.getId()));
    ofNullable(entity.getFuncionario()).ifPresent(f -> {
      target.setNomeColaborador(f.getNome());
      target.setUuidColaborador(f.getUuid());
    });
  }


  private ObjectivoAvaliacaoDTO toObjectivoAvaliacaoDTO(AvaliacaoObjectivoEntity e) {
    var dto = new ObjectivoAvaliacaoDTO();
    dto.setNumero(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setObjectivo(e.getObjectivos());
    dto.setKpi(e.getKpi());
    dto.setMeta(e.getMeta());
    dto.setAvaliacao(e.getAvaliacao()!=null ? e.getAvaliacao().intValue(): null);
    dto.setRealizado(e.getRealizado());
    return dto;
  }



  private CompetenciaComportAvaliacaoDTO toCompetenciaComportAvaliacaoDTO(AvaliacaoCompetenciaEntity e) {
    var dto = new CompetenciaComportAvaliacaoDTO();
    dto.setNumeroOrdem(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setCompetencia(e.getDescricao());
    dto.setPeso(e.getPeso());
    dto.setPonderacao(e.getPonderacao());
    dto.setAvaliacao(null);
    return dto;
  }

  private CompetenciaTecAvaliacaoDTO toCompetenciaTecAvaliacaoDTO(AvaliacaoCompetenciaEntity e) {
    var dto = new CompetenciaTecAvaliacaoDTO();
    dto.setNumeroOrdem(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setCompetencia(e.getDescricao());
    dto.setPeso(e.getPeso());
    dto.setPonderacao(e.getPonderacao());
    dto.setAvaliacao(null);
    return dto;
  }

  private AtitudePessoalAvaliacaoDTO toAtitudePessoalAvaliacaoDTO(AvaliacaoAtitudePessoalEntity e) {
    var dto = new AtitudePessoalAvaliacaoDTO();
    dto.setNumeroOrdem(e.getParamObjetivo() != null ? e.getParamObjetivo().getNumeroOrdem() : null);
    dto.setAbrangencia(e.getAbrangencia());
    dto.setAtitudePessoal(e.getParamObjetivo() != null ? e.getParamObjetivo().getDescricao() : null);
    dto.setPonderacao(e.getPonderacao());
    dto.setAvaliacao(null);
    return dto;
  }
}

