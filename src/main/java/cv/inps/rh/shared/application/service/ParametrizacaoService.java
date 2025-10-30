package cv.inps.rh.shared.application.service;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.repository.EntidadeRepository;
import cv.inps.rh.shared.domain.repository.GeografiaRepository;
import cv.inps.rh.shared.domain.repository.InstituicaoRepository;
import cv.inps.rh.shared.domain.repository.TipoMovimentoRepository;
import cv.inps.rh.shared.infrastructure.mappers.EntidadeMapper;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametrizacaoService {

  private final TipoMovimentoRepository tipoMovimentoRepository;
  private final GeografiaRepository geografiaRepository;
  private final InstituicaoRepository instituicaoRepository;
  private final EntidadeRepository entidadeRepository;

  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EntidadeMapper entidadeMapper;


  public List<ParametrizacaoDTO> getTiposMovimentos(){
    return tipoMovimentoRepository.findAll().stream().map(tipoMovimentoMapper::toParametrizacaoDto).toList();
  }
  public List<ParametrizacaoDTO> getInstituicoes(){
    return instituicaoRepository.findAllActive().stream().map(instituicaoMapper::toParametrizacaoDto).toList();
  }

  public List<ParametrizacaoDTO> getGeografias(Long nivelDetalhe, Long geogrId) {
    return geografiaRepository.findByNivelDetalheAndGeogrId(nivelDetalhe, geogrId)
        .stream()
        .map(geografiaMapper::toParametrizacaoDto)
        .toList();
  }

  public List<ParametrizacaoDTO> getEntidades(){
   return entidadeRepository.findAllActive().stream().map(entidadeMapper::toParametrizacaoDto).toList();
  }

}
