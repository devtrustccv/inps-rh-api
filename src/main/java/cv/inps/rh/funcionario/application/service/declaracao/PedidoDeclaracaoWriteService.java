package cv.inps.rh.funcionario.application.service.declaracao;

import cv.inps.rh.funcionario.application.commands.NovoPedidoDeclaracaoCommand;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PedidoDeclaracaoWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public Map<String, ?> saveNovoPedido(NovoPedidoDeclaracaoCommand command) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(command.getPedidodeclaracao().getFunId());

    return null;
  }

}
