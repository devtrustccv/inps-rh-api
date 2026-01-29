package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEmprestimoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EmprestimoReadService {

  private final ParamEmprestimoEntityRepository paramEmprestimoEntityRepository;
  private final ParamCarreiraEntityRepository paramCarreiraEntityRepository;

  public List<InformacaoEmprestimoRequestDTO> getAllConfiguracaoEmprestimo() {
    return paramEmprestimoEntityRepository.findAll()
        .stream()
        .map(entity -> new InformacaoEmprestimoRequestDTO(
            entity.getCarrPccs().getUuid().toString(),
            entity.getValorLimite(),
            entity.getNumeroLimite(),
            entity.getEstado(),
            entity.getUuid()
        ))
        .toList();
  }
}

