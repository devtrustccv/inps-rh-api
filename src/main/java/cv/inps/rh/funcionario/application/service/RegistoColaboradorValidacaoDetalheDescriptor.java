package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.service.carreira.CarreiraValidacaoDetalheDescriptor;
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
 * <p>Cobertura atual: dossiê (dados bancários, contactos, endereço, familiares, habilitações) +
 * contratual reutilizada (carreira, mobilidade, situação laboral). TiposRelacionamento e Contrato
 * (campos próprios) NÃO entram (tabela de ligação / shallow ref). TODO opcional: documento pessoal e
 * def. remunerações/pagamentos, se se quiser detalhá-los também.
 */
@Component
public class RegistoColaboradorValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  /** Descritores de módulo reutilizados: contribuem os mesmos tipos/campos/rótulos dos seus ecrãs. */
  private final List<ValidacaoDetalheDescriptor> reutilizados;

  public RegistoColaboradorValidacaoDetalheDescriptor(
      DadosBancariosValidacaoDetalheDescriptor dadosBancarios,
      CarreiraValidacaoDetalheDescriptor carreira,
      MobilidadeValidacaoDetalheDescriptor mobilidade,
      SituacaoLaboralValidacaoDetalheDescriptor situacaoLaboral) {
    // Reutiliza os mesmos tipos/campos/rótulos dos ecrãs próprios destes módulos, agora sob a validação
    // do REGISTO. TiposRelacionamento e Contrato (campos próprios) NÃO entram: ligação / shallow ref.
    this.reutilizados = List.of(dadosBancarios, carreira, mobilidade, situacaoLaboral);
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
          "zonaId", "Zona"),
      "FamiliarEntity", Map.ofEntries(
          Map.entry("nome", "Nome"),
          Map.entry("numDocumento", "Nº de documento"),
          Map.entry("tpDocumentoId", "Tipo de documento"),
          Map.entry("dataNascimento", "Data de nascimento"),
          Map.entry("sexo", "Género"),
          Map.entry("gdpId", "Grau de parentesco"),
          Map.entry("dependencia", "Dependente"),
          Map.entry("membroAgr", "Membro do agregado"),
          Map.entry("responsavel", "Responsável")),
      "HabilitacaoLiterariaEntity", Map.ofEntries(
          Map.entry("nomeCurso", "Curso"),
          Map.entry("nivel", "Grau académico"),
          Map.entry("area", "Área"),
          Map.entry("paisId", "País"),
          Map.entry("estabelecimento", "Estabelecimento"),
          Map.entry("dataInicio", "Data início"),
          Map.entry("dataFim", "Data fim"),
          Map.entry("concluido", "Concluído"))
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
