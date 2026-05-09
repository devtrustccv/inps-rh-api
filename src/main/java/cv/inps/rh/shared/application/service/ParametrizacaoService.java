package cv.inps.rh.shared.application.service;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.infrastructure.mappers.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametrizacaoService {

  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final GeografiaEntityRepository geografiaEntityRepository;
  private final InstituicaoEntityRepository instituicaoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EntidadeEntityRepository entidadeEntityRepository;

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
}
