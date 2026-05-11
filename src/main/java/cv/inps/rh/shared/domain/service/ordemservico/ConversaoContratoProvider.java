package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConversaoContratoProvider implements OrdemServicoProvider {

  private final FuncionarioEntityRepository funcionarioRepository;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.CONVERSAO_CONTRATO;
  }

  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = funcionarioRepository.findByUuidOrThrow(
        UUID.fromString(funcionarioId)
    );

    return Map.of(
        "nomeColaborador", fun.getNome(),
        "dataConversaoContrato", formatNow(),
        "dataReuniao", formatNow()
    );
  }

  private String formatNow() {
    return DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now());
  }
}
