package cv.inps.rh.configuracao.infrastructure.mappers;

import cv.inps.rh.configuracao.application.dto.AtitudePessoalLinhaRequestDTO;
import cv.inps.rh.configuracao.application.dto.CompetenciaComportamentalLinhaRequestDTO;
import cv.inps.rh.configuracao.application.dto.CompetenciaTecnicaLinhaRequestDTO;
import cv.inps.rh.configuracao.application.dto.ObjectivoInpsLinhaRequestDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DirecaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCargoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ComponenteAvaliacaoMapper {

  private final ParamCargoEntityRepository cargoRepository;
  private final ParamCarreiraEntityRepository carreiraRepository;
  private final DirecaoEntityRepository instituicaoRepository;

  public ComponenteAvaliacaoMapper(
      ParamCargoEntityRepository cargoRepository,
      ParamCarreiraEntityRepository carreiraRepository,
      DirecaoEntityRepository instituicaoRepository) {
    this.cargoRepository = cargoRepository;
    this.carreiraRepository = carreiraRepository;
    this.instituicaoRepository = instituicaoRepository;
  }

  public ParamObjetivoEntity toEntity(ParamObjetivoDetEntity det, ObjectivoInpsLinhaRequestDTO dto, String componente) {
    if (dto == null)
      return null;

    var entity = baseEntity(det, dto.getAplicarATodos(), dto.getCargoId(), dto.getCarrPccsId(), dto.getPonderacao(),
        componente);
    entity.setNumeroOrdem(dto.getNumeroOrdem());
    entity.setAbrangencia(ValidationUtil.trimToNull(dto.getAbrangencia()));
    entity.setDescricao(ValidationUtil.trimToNull(dto.getDescricao()));
    entity.setKpi(ValidationUtil.trimToNull(dto.getKpi()));

    // Aplicar a todos = todos os colaboradores (sem direção). Caso contrário a direção é opcional:
    // o objetivo pode ser restringido por carreira/cargo e/ou direção, mas nenhum é obrigatório.
    if (!Boolean.TRUE.equals(dto.getAplicarATodos()) && dto.getInstitId() != null) {
      entity.setInstitId(instituicaoRepository.findByIdOrThrow(dto.getInstitId()));
    }

    return entity;
  }

  public ParamObjetivoEntity toEntity(ParamObjetivoDetEntity det, CompetenciaComportamentalLinhaRequestDTO dto,
      String componente, String abrangencia) {
    if (dto == null)
      return null;
    var entity = baseEntity(det, dto.getAplicarATodos(), dto.getCargoId(), dto.getCarrPccsId(), dto.getPonderacao(),
        componente);
    entity.setNumeroOrdem(dto.getNumeroOrdem());
    entity.setAbrangencia(abrangencia);
    return entity;
  }

  public ParamObjetivoEntity toEntity(ParamObjetivoDetEntity det, CompetenciaTecnicaLinhaRequestDTO dto,
      String componente, String abrangencia) {
    if (dto == null)
      return null;
    var entity = baseEntity(det, dto.getAplicarATodos(), dto.getCargoId(), dto.getCarrPccsId(), dto.getPonderacao(),
        componente);
    entity.setNumeroOrdem(dto.getNumeroOrdem());
    entity.setAbrangencia(abrangencia);
    return entity;
  }

  public ParamObjetivoEntity toEntity(ParamObjetivoDetEntity det, AtitudePessoalLinhaRequestDTO dto, String componente,
      String abrangencia, int numeroOrdem) {
    if (dto == null)
      return null;
    var entity = baseEntity(det, dto.getAplicarATodos(), dto.getCargoId(), dto.getCarrPccsId(), dto.getPonderacao(),
        componente);
    entity.setDescricao(ValidationUtil.trimToNull(dto.getDescricao()));
    entity.setNumeroOrdem(numeroOrdem);
    entity.setAbrangencia(abrangencia);
    return entity;
  }

  private ParamObjetivoEntity baseEntity(
      ParamObjetivoDetEntity det,
      Boolean aplicarATodos,
      Long cargoId,
      Long carrPccsId,
      BigDecimal ponderacao,
      String componente) {
    var entity = new ParamObjetivoEntity();
    entity.setParamObjetivoDet(det);

    if (aplicarATodos == null) {
      aplicarATodos = Boolean.FALSE;
    }

    // Aplicar a todos = todos os colaboradores: não se guarda cargo nem carreira.
    // Sem "aplicar a todos", a carreira/cargo é opcional (a regra do cargo obrigatório foi retirada).
    if (!Boolean.TRUE.equals(aplicarATodos)) {
      if (cargoId != null) {
        var cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                "ParamCargoEntity not found for id: " + cargoId));
        entity.setCargo(cargo);
      }
      if (carrPccsId != null) {
        entity.setCarreira(carreiraRepository.findByIdOrThrow(carrPccsId));
      }
    }

    entity.setPonderacao(ponderacao);
    entity.setComponente(componente);
    return entity;
  }
}
