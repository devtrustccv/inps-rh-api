package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o REGISTO DE COLABORADOR.
 *
 * <p>O registo é um AGREGADO: uma só validação (INSERT/REGISTO_COLABORADOR) toca muitas tabelas. Este
 * descritor cobre-as todas, por {@link #entityTypeSuffixes()} (multi-tipo). Só é capturado no PUT de
 * reenvio de correção (C→P), único momento em que o registo é editável — ver
 * {@code ValidarRegistoColaboradorService}. Valores sempre LEGÍVEIS (rótulos PT; FKs → nome via
 * {@code ReferenciaNomeResolver}, nunca id).
 *
 * <p><b>Reutilização:</b> para os filhos que já têm descritor de módulo próprio (dados bancários e,
 * adiante, carreira/mobilidade/situação…), este descritor <b>injeta-os e agrega</b> os seus
 * campos/rótulos/tipos — os mesmos dos ecrãs próprios, sem duplicar. Para os filhos "dossiê" sem
 * descritor (contactos, endereço, familiares, habilitações, documento pessoal) a config vive em
 * {@link #DOSSIE} aqui.
 *
 * <p>{@link #matchByTypeOnly()} = true: a validação tem {@code referenciaId} = id do FUNCIONÁRIO, não
 * o id de cada filho. Seguro porque cada commit é carimbado com o seu {@code validacaoUuid}.
 *
 * <p>TODO: reutilizar também CarreiraValidacaoDetalheDescriptor, MobilidadeValidacaoDetalheDescriptor e
 * SituacaoLaboralValidacaoDetalheDescriptor (injetar em {@link #reutilizados}) quando a captura da parte
 * contratual for ligada no serviço. TiposRelacionamento NÃO entra (tabela de ligação + shallow ref).
 */
@Component
public class RegistoColaboradorValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  /** Descritores de módulo reutilizados: contribuem os mesmos tipos/campos/rótulos dos seus ecrãs. */
  private final List<ValidacaoDetalheDescriptor> reutilizados;

  public RegistoColaboradorValidacaoDetalheDescriptor(
      DadosBancariosValidacaoDetalheDescriptor dadosBancarios) {
    this.reutilizados = List.of(dadosBancarios);
  }

  /**
   * Filhos "dossiê" sem descritor próprio: {@code entityTypeSuffix → (propriedade Java → rótulo PT)}.
   * A allow-list de campos e os rótulos saem daqui.
   */
  private static final Map<String, Map<String, String>> DOSSIE = Map.of(
      "ContactoEntity", Map.of(
          "tipoContacto", "Tipo de contacto",
          "contacto", "Contacto"),
      "EnderecoEntity", Map.of(
          "morada", "Morada",
          "paisId", "País",
          "ilhaId", "Ilha",
          "concelhoId", "Concelho",
          "freguesiaId", "Freguesia",
          "zonaId", "Zona")
  );

  @Override
  public String referenciaName() {
    return Referencia.REGISTO_COLABORADOR.name();
  }

  /** Não usado na prática (a grelha usa {@link #entityTypeSuffixes()}); mantém o contrato satisfeito. */
  @Override
  public String entityTypeSuffix() {
    return "DadosBancariosEntity";
  }

  @Override
  public boolean matchByTypeOnly() {
    return true;
  }

  @Override
  public Set<String> entityTypeSuffixes() {
    Set<String> tipos = new HashSet<>();
    reutilizados.forEach(d -> tipos.addAll(d.entityTypeSuffixes()));
    tipos.addAll(DOSSIE.keySet());
    return tipos;
  }

  @Override
  public Set<String> camposNegocio() {
    Set<String> campos = new HashSet<>();
    reutilizados.forEach(d -> campos.addAll(d.camposNegocio()));
    DOSSIE.values().forEach(m -> campos.addAll(m.keySet()));
    return campos;
  }

  @Override
  public Map<String, String> rotulos() {
    Map<String, String> rotulos = new HashMap<>();
    reutilizados.forEach(d -> rotulos.putAll(d.rotulos()));
    DOSSIE.values().forEach(rotulos::putAll);
    return rotulos;
  }
}
