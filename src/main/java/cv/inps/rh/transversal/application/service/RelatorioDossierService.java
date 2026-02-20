package cv.inps.rh.transversal.application.service;

import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.transversal.application.dto.DossierColaboradorListDTO;
import cv.inps.rh.transversal.application.queries.RelatorioDossierColaboradorQuery;
import org.springframework.stereotype.Service;

@Service
public class RelatorioDossierService {


  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public RelatorioDossierService(TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository) {
    this.tiposRelacionamentoEntityRepository = tiposRelacionamentoEntityRepository;
  }

  public DossierColaboradorListDTO get(RelatorioDossierColaboradorQuery query){
    return null;
  }

}
