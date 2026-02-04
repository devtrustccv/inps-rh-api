package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.DocumentService;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
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
  private final FuncionarioRules funcionarioRules;
  private final DocumentService documentService;

  public void saveUpdatePedidoAdiantamento(List<PedidoAdiantamentoRequestDTO> request) {
    for (var obj : request) {
      var entity = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
      entity.setValorAdiantado(obj.getValorAdiantamento());
      emprestimoEntityRepository.save(entity);
    }
  }


}
