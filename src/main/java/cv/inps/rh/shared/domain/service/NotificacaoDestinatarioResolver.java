package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.application.constants.TipoDestinatarioNotificacao;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
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
 * e-mail". O frontend envia apenas as etiquetas do domínio DESTINATARIO_NOTIFICACAO — nunca
 * emails — com excepção do campo "Email do Responsável", que já vem resolvido e entra aqui
 * por {@link #comEmailsAdicionais}.
 */
@Service
@RequiredArgsConstructor
public class NotificacaoDestinatarioResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoDestinatarioResolver.class);

  private static final String TIPO_CONTACTO_EMAIL = "EMAIL";

  private final ResponsavelEntityRepository responsavelRepository;
  private final MobilidadeEntityRepository mobilidadeRepository;

  /**
   * Um destinatário já resolvido. O {@code funcionario} pode ser nulo — por exemplo num email
   * adicional escrito à mão, que não corresponde a nenhum funcionário conhecido — e nesse caso
   * a notificação fica gravada sem FUN_ID.
   */
  public record Destinatario(String tipo, String nome, String email, FuncionarioEntity funcionario) {}

  /**
   * Resolve os tipos escolhidos no multiselect. Destinatários sem email conhecido são omitidos
   * com um aviso: a alternativa — rebentar o pedido inteiro — impediria o envio para os
   * restantes, que é o oposto do que o utilizador pediu.
   *
   * <p>O resultado vem deduplicado por email (o primeiro a aparecer ganha), porque o responsável
   * do colaborador e um email adicional escolhido à mão são com frequência a mesma pessoa, e
   * enviar-lhe o mesmo email duas vezes gravaria também duas linhas em RH_T_NOTIFICACAO.</p>
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
        // TODO RESPONSAVEL_REGISTO: enviar para o utilizador que fez login e criou o registo.
        // Bloqueado — AuditEntityListener.getCurrentUserId() é um stub (devolve 1L/2L fixo) e
        // ApplicationAuditorAware devolve "local" hardcoded em development/staging, portanto não
        // há hoje forma de chegar à pessoa. A resolver replicando o padrão IAM do inss_core_service
        // (perfil por claim `sub` do JWT + sync filter + AuthenticatedUserHelper); esse perfil já
        // traz o email, logo bastará ir de createdBy (= sub) ao perfil. Ver o javadoc de
        // NotificacaoDispatchService para o detalhe das peças a copiar.
        case RESPONSAVEL_REGISTO -> LOGGER.warn(
            "Destinatário RESPONSAVEL_REGISTO pedido mas ainda não implementado — "
            + "requer identidade do utilizador autenticado (ver TODO acima)");
      }
    }

    return deduplicar(destinatarios);
  }

  /**
   * Junta aos destinatários resolvidos os emails escolhidos no campo "Email do Responsável".
   * Estes já vêm prontos do frontend — vindos de RH_T_RESPONSAVEL — e por isso não passam por
   * nenhuma resolução, apenas por validação de formato mínima e deduplicação.
   */
  public List<Destinatario> comEmailsAdicionais(List<Destinatario> resolvidos,
                                                Collection<String> emailsAdicionais) {
    if (emailsAdicionais == null || emailsAdicionais.isEmpty()) return deduplicar(resolvidos);

    var todos = new ArrayList<>(resolvidos);
    emailsAdicionais.stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .forEach(email -> todos.add(new Destinatario(
            TipoDestinatarioNotificacao.RESPONSAVEL_COLABORADOR.name(), email, email, null)));

    return deduplicar(todos);
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
   * mobilidade activa e sem data de fim — o mesmo critério usado no resto do módulo.
   *
   * <p>Devolve uma lista e não um único responsável: uma secção pode ter mais do que uma linha
   * em RH_T_RESPONSAVEL, e a spec diz explicitamente "deve ser possível selecionar mais do que
   * um destinatário".</p>
   */
  private List<Destinatario> resolverResponsavelColaborador(FuncionarioEntity colaborador) {
    if (colaborador == null) return List.of();

    var mobilidade = mobilidadeRepository.findByFunIdAndEstadoAndDataFimIsNull(colaborador, Estado.A);
    if (mobilidade == null || mobilidade.getInstidId() == null) {
      LOGGER.warn("Colaborador {} sem mobilidade activa — não é possível determinar o responsável",
          colaborador.getUuid());
      return List .of();
    }

    var direcaoId = mobilidade.getInstidId().getId();
    var secao = mobilidade.getSecaoId();

    // Sem secção conhecida cai-se para os responsáveis da direção; é preferível notificar a
    // chefia de nível acima a não notificar ninguém.
    var responsaveis = secao != null
        ? responsavelRepository.findAllByInstitId_idAndSecaoId_uuid(direcaoId, secao.getUuid())
        : responsavelRepository.findAllByInstitId_id(direcaoId);

    if (responsaveis.isEmpty()) {
      LOGGER.warn("Sem responsáveis em RH_T_RESPONSAVEL para direcao={} secao={}",
          direcaoId, secao != null ? secao.getId() : null);
      return List.of();
    }

    return responsaveis.stream()
        .filter(r -> StringUtils.hasText(r.getEmail()))
        .map(this::paraDestinatario)
        .toList();
  }

  private Destinatario paraDestinatario(ResponsavelEntity responsavel) {
    var funcionario = responsavel.getFunId();
    var nome = funcionario != null && StringUtils.hasText(funcionario.getNome())
        ? funcionario.getNome()
        : responsavel.getEmail();
    return new Destinatario(
        TipoDestinatarioNotificacao.RESPONSAVEL_COLABORADOR.name(),
        nome, responsavel.getEmail().trim(), funcionario);
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
