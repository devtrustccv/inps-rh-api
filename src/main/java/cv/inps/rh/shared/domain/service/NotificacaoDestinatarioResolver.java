package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.application.constants.TipoDestinatarioNotificacao;
import cv.inps.rh.shared.application.services.AuthenticatedUserHelper;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.application.constants.Estado;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Traduz os valores do multiselect "Destinatários da notificação" para endereços de email
 * concretos, conforme a regra da spec: "o sistema deve obter automaticamente o endereço de
 * e-mail".
 *
 * <p>O frontend envia apenas as etiquetas do domínio DESTINATARIO_NOTIFICACAO — nunca emails.
 * Os três tipos são todos resolúveis do lado do servidor: o colaborador pelos seus contactos, o
 * responsável pela colocação activa, e o responsável do registo pelo perfil IAM do utilizador
 * autenticado.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificacaoDestinatarioResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoDestinatarioResolver.class);

  private static final String TIPO_CONTACTO_EMAIL = "EMAIL";

  private final ResponsavelEntityRepository responsavelRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;
  private final AuthenticatedUserHelper authenticatedUserHelper;

  /**
   * Um destinatário já resolvido. O {@code funcionario} pode ser nulo — o utilizador que fez o
   * registo é uma identidade IAM e não tem necessariamente linha em RH_T_FUNCIONARIO — e nesse
   * caso a notificação fica gravada sem FUN_ID.
   */
  public record Destinatario(String tipo, String nome, String email, FuncionarioEntity funcionario) {}

  /**
   * Resolve os tipos escolhidos no multiselect. Destinatários sem email conhecido são omitidos
   * com um aviso: a alternativa — rebentar o pedido inteiro — impediria o envio para os
   * restantes, que é o oposto do que o utilizador pediu.
   *
   * <p>O resultado vem deduplicado por email (o primeiro tipo a aparecer ganha), porque quem faz
   * o registo é com frequência o próprio responsável do colaborador, e enviar-lhe o mesmo email
   * duas vezes gravaria também duas linhas em RH_T_NOTIFICACAO.</p>
   */
  @Transactional(readOnly = true)
  public List<Destinatario> resolver(FuncionarioEntity colaborador,
                                     Collection<TipoDestinatarioNotificacao> tipos) {
    if (tipos == null || tipos.isEmpty()) return List.of();

    var destinatarios = new ArrayList<Destinatario>();

    for (var tipo : tipos) {
      switch (tipo) {
        case COLABORADOR -> resolverColaborador(colaborador).ifPresent(destinatarios::add);
        case RESPONSAVEL_COLABORADOR -> destinatarios.addAll(resolverResponsavelColaborador(colaborador));
        case RESPONSAVEL_REGISTO -> resolverResponsavelRegisto().ifPresent(destinatarios::add);
      }
    }

    return deduplicar(destinatarios);
  }

  /**
   * Utilizador autenticado que está a fazer o registo. O email vem do perfil IAM sincronizado a
   * partir do JWT ({@code sub}), o mesmo valor que fica em {@code createdBy} — não é preciso
   * passar por RH_T_FUNCIONARIO.
   *
   * <p>Sem sessão autenticada (jobs, ou dev com segurança desligada) não há a quem enviar: fica
   * o aviso e o destinatário é omitido, em vez de rebentar o envio para os restantes.</p>
   */
  private Optional<Destinatario> resolverResponsavelRegisto() {
    var perfil = authenticatedUserHelper.getProfile();
    if (perfil.isEmpty()) {
      LOGGER.warn("RESPONSAVEL_REGISTO pedido mas não há utilizador autenticado com perfil IAM "
          + "(sub={}) — destinatário omitido", authenticatedUserHelper.getSub());
      return Optional.empty();
    }

    var p = perfil.get();
    if (!StringUtils.hasText(p.getEmail())) {
      LOGGER.warn("Perfil IAM {} sem email — RESPONSAVEL_REGISTO omitido", p.getUsername());
      return Optional.empty();
    }

    var nome = StringUtils.hasText(p.getFullName()) ? p.getFullName() : p.getUsername();
    return Optional.of(new Destinatario(
        TipoDestinatarioNotificacao.RESPONSAVEL_REGISTO.name(), nome, p.getEmail().trim(), null));
  }

  private Optional<Destinatario> resolverColaborador(FuncionarioEntity colaborador) {
    if (colaborador == null) return Optional.empty();

    var email = emailDe(colaborador);
    if (email.isEmpty()) {
      LOGGER.warn("Colaborador {} sem contacto do tipo EMAIL — notificação não enviada",
          colaborador.getUuid());
      return Optional.empty();
    }

    return Optional.of(new Destinatario(
        TipoDestinatarioNotificacao.COLABORADOR.name(), colaborador.getNome(), email.get(), colaborador));
  }

  /**
   * Responsáveis da direção/secção onde o colaborador está colocado. A colocação vem da
   * mobilidade apontada pelo tiprel atual (est_act_adm=1).
   *
   * <p>Procura em dois níveis: primeiro a chefia da secção, e só se essa não existir a da direção.</p>
   *
   * <p>Só responsáveis com estado A: o soft-delete de RH_T_RESPONSAVEL põe estado='I' e notificar
   * uma chefia já removida da direção seria fuga de informação.</p>
   *
   * <p>Devolve uma lista e não um único responsável: uma secção pode ter mais do que uma linha
   * em RH_T_RESPONSAVEL, e a spec diz explicitamente "deve ser possível selecionar mais do que
   * um destinatário".</p>
   */
  private List<Destinatario> resolverResponsavelColaborador(FuncionarioEntity colaborador) {
    if (colaborador == null) return List.of();

    // A colocação vem da mobilidade referenciada pelo tiprel ATUAL (est_act_adm=1), e não de uma
    // pesquisa por estado/data_fim em RH_T_MOBILIDADE: a FK MOB_ID é a fonte de verdade da
    // colocação, é a definição de "relação laboral atual" usada no resto do módulo, e a query já
    // traz a direção e a secção em fetch (open-in-view está desligado).
    var mobilidade = tiposRelacionamentoRepository.findAtualByFuncionarioUuid(colaborador.getUuid())
        .map(TiposRelacionamentoEntity::getMobId)
        .orElse(null);

    if (mobilidade == null || mobilidade.getInstidId() == null) {
      LOGGER.warn("Colaborador {} sem colocação no tipo de relacionamento atual — não é possível "
          + "determinar o responsável", colaborador.getUuid());
      return List.of();
    }

    var direcaoId = mobilidade.getInstidId().getId();
    var secao = mobilidade.getSecaoId();

    // A chefia mais próxima primeiro: a da secção onde o colaborador está colocado. Só quando essa
    // não existe — secção sem responsável activo, ou colocação sem secção — se sobe para a
    // direção; é preferível notificar o nível acima a não notificar ninguém.
    var responsaveis = secao != null
        ? responsavelRepository.findAllBySecaoId_uuidAndEstado(secao.getUuid(), Estado.A.name())
        : List.<ResponsavelEntity>of();

    if (responsaveis.isEmpty()) {
      responsaveis = responsavelRepository.findAllByInstitId_idAndSecaoIdIsNullAndEstado(
          direcaoId, Estado.A.name());
      LOGGER.debug("Secção {} sem responsável activo — a subir para a chefia da direção {}",
          secao != null ? secao.getId() : null, direcaoId);
    }

    if (responsaveis.isEmpty()) {
      LOGGER.warn("Sem responsáveis activos em RH_T_RESPONSAVEL para direcao={} secao={}",
          direcaoId, secao != null ? secao.getId() : null);
      return List.of();
    }

    return responsaveis.stream()
        .map(this::resolverResponsavel)
        .flatMap(Optional::stream)
        .toList();
  }

  /**
   * Resolve uma linha de RH_T_RESPONSAVEL num destinatário. Público porque os fluxos que já sabem
   * qual é o responsável — por exemplo o envio de direito a férias, que o traz no próprio pedido —
   * precisam da mesma regra de email sem repetir a resolução da chefia.
   *
   * <p>O email vem dos contactos do funcionário que ocupa o lugar — a mesma fonte do COLABORADOR.
   * A coluna RH_T_RESPONSAVEL.EMAIL fica como fallback para dados legados: nenhum fluxo da
   * aplicação a escreve (saveResponsaveis grava só funId/secaoId/institId/estado), pelo que
   * confiar só nela deixaria esta resolução sem nunca encontrar ninguém.</p>
   */
  @Transactional(readOnly = true)
  public Optional<Destinatario> resolverResponsavel(ResponsavelEntity responsavel) {
    var funcionario = responsavel.getFunId();

    var email = (funcionario != null ? emailDe(funcionario) : Optional.<String>empty())
        .or(() -> Optional.ofNullable(responsavel.getEmail())
            .filter(StringUtils::hasText)
            .map(String::trim));

    if (email.isEmpty()) {
      LOGGER.warn("Responsável {} sem email — nem nos contactos do funcionário nem na coluna "
          + "RH_T_RESPONSAVEL.EMAIL — omitido", responsavel.getId());
      return Optional.empty();
    }

    var nome = funcionario != null && StringUtils.hasText(funcionario.getNome())
        ? funcionario.getNome()
        : email.get();

    return Optional.of(new Destinatario(
        TipoDestinatarioNotificacao.RESPONSAVEL_COLABORADOR.name(), nome, email.get(), funcionario));
  }

  private Optional<String> emailDe(FuncionarioEntity funcionario) {
    var contactos = funcionario.getContactos();
    if (contactos == null) return Optional.empty();

    return contactos.stream()
        .filter(c -> TIPO_CONTACTO_EMAIL.equalsIgnoreCase(c.getTipoContacto()))
        .map(ContactoEntity::getContacto)
        .filter(StringUtils::hasText)
        .map(String::trim)
        .findFirst();
  }

  private List<Destinatario> deduplicar(List<Destinatario> destinatarios) {
    var porEmail = new LinkedHashMap<String, Destinatario>();
    for (var d : destinatarios) {
      if (StringUtils.hasText(d.email())) {
        porEmail.putIfAbsent(d.email().toLowerCase(Locale.ROOT), d);
      }
    }
    return List.copyOf(porEmail.values());
  }
}
