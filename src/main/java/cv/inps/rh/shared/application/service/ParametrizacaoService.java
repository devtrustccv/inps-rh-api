package cv.inps.rh.shared.application.service;

import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.parametrizacao.application.dto.EstabelecimentoComboDTO;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.application.dto.TipoMovimentoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.mappers.*;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    return estabelecimentoEntityRepository.findByPaisId(List.of(paisId));
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

  public List<ParametrizacaoDTO> getEntidades() {
    return entidadeEntityRepository.findAll().stream().map(entidadeMapper::toParametrizacaoDto).toList();
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
