package cv.inps.rh.funcionario.application.service.carreira;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para a CARREIRA. Único ponto carreira-específico da
 * leitura: campos de negócio + rótulos + tipo-alvo. Cópia do modelo da mobilidade — o read-service
 * partilhado não muda.
 */
@Component
public class CarreiraValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.CARREIRA.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "CarreiraEntity";
  }

  @Override
  public Set<String> camposNegocio() {
    // Fora, por omissão: estado/estActAdm (workflow), contrVinculoId (estrutural), created*/lastModified*.
    return Set.of(
        "cargoId", "escalaoId", "categoriaId", "carrPccsId",
        "salario", "flgProcessa", "tipoCarreira", "dataInicio", "dataFim", "obs");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "cargoId", "Cargo",
        "escalaoId", "Escalão",
        "categoriaId", "Categoria",
        "carrPccsId", "Carreira (PCCS)",
        "salario", "Salário",
        "flgProcessa", "Processa salário",
        "tipoCarreira", "Tipo de carreira",
        "dataInicio", "Data início",
        "dataFim", "Data fim",
        "obs", "Observações");
  }
}
