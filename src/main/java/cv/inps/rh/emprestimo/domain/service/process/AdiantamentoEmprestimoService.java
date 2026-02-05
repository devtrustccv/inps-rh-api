package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdiantamentoEmprestimoService {

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;

  public void saveUpdatePedidoAdiantamento(List<PedidoAdiantamentoRequestDTO> request) {
    for (var obj : request) {
      var entity = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
      entity.setValorAdiantado(obj.getValorAdiantamento());
      emprestimoEntityRepository.save(entity);
    }
  }

  public void saveAnaliseRh(AnaliseRhAdiantamentoRequestDTO request){
    // TODO 04/02/2026 22:06 implement
  }

  public void anexarComprovativo(){
    // TODO 04/02/2026 22:06 implement
  }

  public void verificar(){
    // TODO 04/02/2026 22:06 implement
  }


}
