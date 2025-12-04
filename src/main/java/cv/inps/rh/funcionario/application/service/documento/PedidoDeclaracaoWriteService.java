package cv.inps.rh.funcionario.application.service.documento;

import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PedidoDeclaracaoWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public UUID saveNovoPedido(String funcionarioId, PedidoDeclaracaoDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    return null;
  }

}
