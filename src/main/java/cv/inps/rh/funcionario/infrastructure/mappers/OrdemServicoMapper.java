package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoMapper {

  private final EntityManager entityManager;

  public OrdemServicoEntity toEntity(OrdemServico domain) {
     if (domain == null) return null;
     OrdemServicoEntity entity = new OrdemServicoEntity();
     entity.setNuOrdem(domain.getFunId().toString());
     entity.setDescricao(domain.getDescricao());
     entity.setReferente(domain.getReferente());
     entity.setFunId(entityManager.getReference(FuncionarioEntity.class,domain.getFunId()));
     entity.setTiprelId(entityManager.getReference(TiposRelacionamentoEntity.class,domain.getTiprelId()));
     entity.setValidacaoId(entityManager.getReference(ValidacaoEntity.class,domain.getValidacaoId()));
     entity.setEstado(domain.getEstado());
     entity.setObs(domain.getObs());
     entity.setUuid(domain.getUuid().getValor());
     return entity;
  }


}
