package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para a MOBILIDADE. É este o único ponto mobilidade-
 * específico da leitura: campos de negócio + rótulos + tipo-alvo. Para ligar outro módulo à grelha,
 * copiar esta classe, trocar {@code referenciaName}/{@code entityTypeSuffix} e os campos/rótulos.
 */
@Component
public class MobilidadeValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.MOBILIDADE.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "MobilidadeEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    // Fora, por omissão: estado (workflow), funId/mobId/validacoes (estruturais), created*/lastModified*.
    return Set.of("secaoId", "localTrabId", "instidId", "tipoSituacao", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "secaoId", "Secção",
        "localTrabId", "Local de trabalho",
        "instidId", "Direcção",
        "tipoSituacao", "Tipo de mobilidade",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
