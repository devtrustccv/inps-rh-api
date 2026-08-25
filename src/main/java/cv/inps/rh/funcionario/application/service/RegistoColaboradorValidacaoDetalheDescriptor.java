package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o REGISTO DE COLABORADOR.
 *
 * <p>Fatia vertical (1ª etapa): cobre os DADOS BANCÁRIOS, o único filho do registo já auditado pelo
 * JaVers ({@code DadosBancariosEntityRepository} é {@code @JaversSpringDataAuditable}). Os restantes
 * filhos (contactos, familiares, habilitações, endereço) entram depois — cada um exige tornar o seu
 * repositório auditável e capturar baseline/diff no {@code ValidarRegistoColaboradorService}.
 *
 * <p>{@link #matchByTypeOnly()} = true porque a validação (INSERT/REGISTO_COLABORADOR) tem
 * {@code referenciaId} = id do FUNCIONÁRIO, não o id do bancário — e os dados bancários são uma
 * coleção. O isolamento é feito só pelo tipo, seguro porque cada commit é carimbado com o seu
 * {@code validacaoUuid} (a captura do diff no reenvio C→P).
 *
 * <p>Só valores LEGÍVEIS: a coluna "campo alterado" usa os rótulos PT abaixo; {@code rhbId} é uma FK
 * resolvida para o NOME do banco pelo {@code ReferenciaNomeResolver} (nunca o id).
 *
 * <p>TODO (detalhe do registo — próximos filhos, mesmo molde: anotar repo {@code @JaversSpringDataAuditable}
 * + baseline no CORRIGIR + diff no reenvio em {@code ValidarRegistoColaboradorService} + campos/rótulos):
 * <ul>
 *   <li>Ligar o read-model multi-tipo: usar {@link ValidacaoDetalheDescriptor#entityTypeSuffixes()} no
 *       {@code JaversValidacaoDetalheReadService.isAlvo(...)} (o default já existe) e este descritor
 *       passar a devolver todos os tipos-alvo + união de campos/rótulos.</li>
 *   <li><b>Contactos</b> ({@code ContactoEntity}): {@code tipoContacto}, {@code contacto}.</li>
 *   <li><b>Endereço</b> ({@code EnderecoEntity}): {@code morada} + FKs país/ilha/concelho/freguesia/zona.</li>
 *   <li><b>Familiares</b> ({@code FamiliarEntity}): {@code nome}, {@code numDocumento}, {@code dataNascimento},
 *       {@code sexo}, {@code gdpId}, {@code dependencia}, {@code membroAgr}, {@code responsavel}, {@code tpDocumentoId}.</li>
 *   <li><b>Habilitações</b> ({@code HabilitacaoLiterariaEntity}): {@code nomeCurso}, {@code nivel}, {@code area},
 *       {@code paisId}, {@code estabelecimento}, {@code dataInicio}, {@code dataFim}, {@code concluido}.</li>
 *   <li>Rever {@code REFERENCIAS_RASAS} no {@code JaversAuditConfig}: os novos filhos NÃO rasos (senão não
 *       diffam), as FKs deles rasas (performance). Confirmar getters de nome no {@code ReferenciaNomeResolver}.</li>
 * </ul>
 */
@Component
public class RegistoColaboradorValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.REGISTO_COLABORADOR.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "DadosBancariosEntity";
  }

  @Override
  public boolean matchByTypeOnly() {
    return true;
  }

  @Override
  public Set<String> camposNegocio() {
    return Set.of("rhbId", "numConta", "nib", "dataInicio", "dataFim");
  }

  @Override
  public Map<String, String> rotulos() {
    return Map.of(
        "rhbId", "Entidade bancária",
        "numConta", "Nº de conta",
        "nib", "NIB",
        "dataInicio", "Data início",
        "dataFim", "Data fim");
  }
}
