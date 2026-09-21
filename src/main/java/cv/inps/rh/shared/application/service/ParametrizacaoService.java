package cv.inps.rh.shared.application.service;

import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.parametrizacao.application.dto.EstabelecimentoComboDTO;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.application.dto.TipoMovimentoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.mappers.*;
import cv.inps.rh.shared.infrastructure.persistence.entity.EntidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EntidadeEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PesquisaTexto;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParametrizacaoService {

  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final GeografiaEntityRepository geografiaEntityRepository;
  private final DirecaoEntityRepository instituicaoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EntidadeEntityRepository entidadeEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final EstabelecimentoEntityRepository estabelecimentoEntityRepository;

  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final DirecaoMapper direcaoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EntidadeMapper entidadeMapper;
  private final BancoMapper bancoMapper;

  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;


  private static final String ESTADO_ACTIVO = "ACTIVO";
  private static final Long AMB_APL_ID = 30L;
  private static final int NOME_PESQUISA_MAX = 150;   // = NOME VARCHAR2(150) em INPSSIGOF.ENTIDADES
  private static final int LIMITE_PESQUISA_DEFAULT = 50;
  private static final int LIMITE_PESQUISA_MAX = 200;

  public List<TipoMovimentoDTO> getTiposMovimentosRenumeracao() {
    return tipoMovimentoEntityRepository
        .findAllByTipoInAndEstadoAndAmbAplIdAndShortDescNot(List.of("REM", "ABA"), ESTADO_ACTIVO, AMB_APL_ID, "SALL")
        .stream().map(tipoMovimentoMapper::toParametrizacaoDto).toList();
  }

  public List<TipoMovimentoDTO> getTiposMovimentosPagamentosDesconto() {
    return tipoMovimentoEntityRepository
        .findAllByTipoInAndEstadoAndAmbAplId(List.of("PAG", "IMP"), ESTADO_ACTIVO, AMB_APL_ID)
        .stream().map(tipoMovimentoMapper::toParametrizacaoDto).toList();
  }

  @Transactional
  public List<EstabelecimentoComboDTO> getEstabelecimentosAtivos(Long paisId) {
    List<Long> countries = Objects.nonNull(paisId) ? List.of(paisId) : null;
    return estabelecimentoEntityRepository.findByPaisId(countries);
  }

  public List<ParametrizacaoDTO> getInstituicoes() {
    return instituicaoEntityRepository.findDirecoesList()
        .stream()
        .map(p -> new ParametrizacaoDTO(p.getNome(), p.getId()))
        .toList();
  }

  public List<ParametrizacaoDTO> getGeografias(Long nivelDetalhe, Long geogrId) {
    return geografiaEntityRepository.findByNivelDetalheAndGeogrId(nivelDetalhe, geogrId)
        .stream()
        .map(geografiaMapper::toParametrizacaoDto)
        .toList();
  }

  /**
   * Entidades do SIGOF. Sem {@code nome} devolve todas (comportamento de sempre). Com {@code nome}:
   * cada termo tem de aparecer no nome, ignorando maiúsculas e acentos; as que começam pelo primeiro
   * termo vêm primeiro, depois por ordem alfabética; no máximo {@code limite} resultados.
   */
  @Transactional(readOnly = true)
  public List<ParametrizacaoDTO> getEntidades(String nome, Integer limite) {
    var termos = PesquisaTexto.termos(nome);
    if (termos.isEmpty()) {
      return entidadeEntityRepository.findAll().stream().map(entidadeMapper::toParametrizacaoDto).toList();
    }
    if (nome.trim().length() > NOME_PESQUISA_MAX) {
      throw IgrpResponseStatusException.badRequest(
          "O nome a pesquisar não pode ter mais de " + NOME_PESQUISA_MAX + " caracteres.");
    }

    var max = limite == null ? LIMITE_PESQUISA_DEFAULT : Math.clamp(limite, 1, LIMITE_PESQUISA_MAX);
    Specification<EntidadeEntity> spec = (root, query, cb) -> {
      var nomeNormalizado = PesquisaTexto.normalizar(cb, root.get(EntidadeEntity_.nome));
      var comecaPor = cb.like(nomeNormalizado, PesquisaTexto.comecaPor(termos.getFirst()), PesquisaTexto.ESCAPE);
      query.orderBy(
          cb.asc(cb.<Integer>selectCase().when(comecaPor, 0).otherwise(1)),
          cb.asc(root.get(EntidadeEntity_.nome)),
          cb.asc(root.get(EntidadeEntity_.id)));
      return cb.and(termos.stream()
          .map(t -> cb.like(nomeNormalizado, PesquisaTexto.contem(t), PesquisaTexto.ESCAPE))
          .toArray(Predicate[]::new));
    };

    return entidadeEntityRepository.findBy(spec, q -> q.limit(max).all())
        .stream().map(entidadeMapper::toParametrizacaoDto).toList();
  }

  public List<ParametrizacaoDTO> getBancos() {
    return bancoEntityRepository.findAll().stream().map(bancoMapper::toParametrizacaoDto).toList();
  }

  public String getCentroByInstituicao(Long institId) {
    return instituicaoEntityRepository.getNomeCentroCusto(institId);
  }

  @Transactional(readOnly = true)
  public List<VinculoMovimentoResponseDTO> getRemuneracoesByVinculo(Long vinculoId, String vinculoUuid) {
    return getTiposMovimentoByVinculoAndTipo(vinculoId, vinculoUuid, "REM");
  }

  @Transactional(readOnly = true)
  public List<VinculoMovimentoResponseDTO> getEncargosDescontosByVinculo(Long vinculoId, String vinculoUuid) {
    return getTiposMovimentoByVinculoAndTipo(vinculoId, vinculoUuid, "PAG");
  }

  private List<VinculoMovimentoResponseDTO> getTiposMovimentoByVinculoAndTipo(Long vinculoId, String vinculoUuid, String tipo) {
    boolean hasId = vinculoId != null;
    boolean hasUuid = vinculoUuid != null && !vinculoUuid.isBlank();

    if (hasId == hasUuid) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST,
          "Forneça exactamente um dos parâmetros: vinculoId ou vinculoUuid");
    }

    var movimentos = hasId
        ? paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoId, tipo, Estado.A)
        : paramVinculoMovimentoEntityRepository.findByVinculoId_UuidAndTipoAndEstado(UUID.fromString(vinculoUuid), tipo, Estado.A);

    return movimentos.stream()
        .filter(m -> m.getTmId() != null)
        .map(this::toVinculoMovimentoResponse)
        .toList();
  }

  private VinculoMovimentoResponseDTO toVinculoMovimentoResponse(ParamVinculoMovimentoEntity entity) {
    var resp = new VinculoMovimentoResponseDTO();
    resp.setId(entity.getId());
    resp.setUuid(entity.getUuid());
    resp.setTipoMovimentoId(entity.getTmId().getId());
    resp.setTipoMovimentoDescricao(entity.getTmId().getDescricao());
    resp.setTipo(entity.getTipo());
    resp.setPercentagem(entity.getPercentagem());
    resp.setValor(entity.getValor());
    resp.setEstado(entity.getEstado() != null ? entity.getEstado().getCode() : null);
    return resp;
  }
}
