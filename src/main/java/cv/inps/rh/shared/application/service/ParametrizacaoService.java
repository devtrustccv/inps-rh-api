package cv.inps.rh.shared.application.service;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.mappers.*;
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
  private final InstituicaoEntityRepository instituicaoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EntidadeEntityRepository entidadeEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;

  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EntidadeMapper entidadeMapper;
  private final BancoMapper bancoMapper;

  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;


  public List<ParametrizacaoDTO> getTiposMovimentosRenumeracao(){
    return tipoMovimentoEntityRepository.findAllByTipo("REM").stream().map(tipoMovimentoMapper::toParametrizacaoDto).toList();
  }

  public List<ParametrizacaoDTO> getTiposMovimentosPagamentosDesconto(){
    return tipoMovimentoEntityRepository.findAllByTipo("PAG").stream().map(tipoMovimentoMapper::toParametrizacaoDto).toList();
  }

  public List<ParametrizacaoDTO> getInstituicoes() {
    return instituicaoEntityRepository.findInstituicoesList()
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

  public List<ParametrizacaoDTO> getEntidades(){
   return entidadeEntityRepository.findAll().stream().map(entidadeMapper::toParametrizacaoDto).toList();
  }

  public List<ParametrizacaoDTO> getBancos(){
    return bancoEntityRepository.findAll().stream().map(bancoMapper::toParametrizacaoDto).toList();
  }

  public String getCentroByInstituicao(Long institId) {
    return instituicaoEntityRepository.getNomeCentroCusto(institId);
  }

  @Transactional(readOnly = true)
  public List<ParametrizacaoDTO> getTiposMovimentoByVinculo(Long vinculoId, String vinculoUuid) {
    boolean hasId = vinculoId != null;
    boolean hasUuid = vinculoUuid != null && !vinculoUuid.isBlank();

    if (hasId == hasUuid) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST,
          "Forneça exactamente um dos parâmetros: vinculoId ou vinculoUuid");
    }

    var movimentos = hasId
        ? paramVinculoMovimentoEntityRepository.findByVinculoId_Id(vinculoId)
        : paramVinculoMovimentoEntityRepository.findByVinculoId_Uuid(UUID.fromString(vinculoUuid));

    return movimentos.stream()
        .filter(m -> m.getTmId() != null)
        .map(m -> tipoMovimentoMapper.toParametrizacaoDto(m.getTmId()))
        .toList();
  }
}
