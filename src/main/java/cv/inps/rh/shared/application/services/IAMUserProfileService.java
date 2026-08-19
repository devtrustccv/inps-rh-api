package cv.inps.rh.shared.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Ponto central de acesso ao perfil IAM ({@code t_iam_user_profile}) e de tradução de identidade.
 *
 * <p>Um utilizador pode aparecer nas colunas de utilizador do negócio sob duas "caras": o
 * <b>sub</b> (UUID do JWT, gravado pelos fluxos autenticados) e o <b>user_id_old</b> (id numérico
 * do sistema legacy, presente nos dados importados). Este serviço centraliza a ligação entre os
 * dois e é o único ponto que toca no {@link IAMUserProfileEntityRepository}.</p>
 */
@Service
public class IAMUserProfileService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IAMUserProfileService.class);

  /** Tecto defensivo: os perfis activos sao poucos, mas o registo nao pode crescer sem limite. */
  private static final int MAX_SYNC_MARKS = 10_000;

  private final IAMUserProfileEntityRepository repository;

  /**
   * Marca da ultima sincronizacao: {@code sub -> hash dos claims gravados}. Nao guarda perfis — as
   * leituras vao sempre a base de dados. E so memoria de "ja escrevi isto e nada mudou desde entao",
   * para nao tocar na BD em todos os pedidos. Ver {@link #syncFromJwt}.
   *
   * <p>A entrada expira sozinha ao fim do TTL e as menos usadas sao descartadas ao atingir o tecto.
   * Perder uma entrada nao tem consequencia: apenas provoca uma sincronizacao a mais.</p>
   */
  private final Cache<String, Integer> syncMarks;

  public IAMUserProfileService(IAMUserProfileEntityRepository repository,
                               @Value("${iam.profile.sync.ttl-minutes:30}") int syncTtlMinutes) {
    this.repository = repository;
    this.syncMarks = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(syncTtlMinutes))
        .maximumSize(MAX_SYNC_MARKS)
        .build();
  }

  // ---------------------------------------------------------------------------------------------
  // Resolução de identidade
  // ---------------------------------------------------------------------------------------------

  /** Perfil pelo sub (UUID do JWT). */
  @Transactional(readOnly = true)
  public Optional<IAMUserProfileEntity> findBySub(String sub) {
    if (!StringUtils.hasText(sub)) return Optional.empty();
    return repository.findBySub(sub);
  }

  /**
   * Resolve um perfil a partir de um identificador do NEGÓCIO, que tanto pode ser o sub (UUID novo)
   * como o id numérico legacy ({@code user_id_old}). Procura primeiro por sub, depois por
   * user_id_old. Caminho inverso: dado um id guardado no negócio, obter o utilizador/nome.
   */
  @Transactional(readOnly = true)
  public Optional<IAMUserProfileEntity> findBySubOrUserIdOld(String id) {
    if (!StringUtils.hasText(id)) return Optional.empty();
    return repository.findBySub(id)
        .or(() -> repository.findByUserIdOld(id));
  }

  /**
   * Todos os ids pelos quais a MESMA pessoa pode aparecer nas colunas de utilizador do negócio:
   * {@code sub} e {@code user_id_old} (legacy). Aceita qualquer um dos dois como entrada — o sub do
   * utilizador autenticado, ou o valor que vier num filtro de ecrã, que tanto pode ser um uuid como
   * um número.
   *
   * <p>Usar para filtrar listas por utilizador: {@code WHERE user_id IN (:ids)}. Uma comparação
   * directa {@code = sub} falha em silêncio para tudo o que tenha sido gravado com o id legacy, e
   * vice-versa — é a mesma pessoa a não se ver a si própria.</p>
   *
   * <p>O id recebido vem sempre incluído, mesmo sem perfil correspondente: sem perfil, filtrar por
   * ele é o melhor que se pode fazer, e é o comportamento antigo.</p>
   */
  @Transactional(readOnly = true)
  public List<String> resolverIdsEquivalentes(String idDoNegocio) {
    if (!StringUtils.hasText(idDoNegocio)) return List.of();

    // LinkedHashSet: sem repetidos (o id recebido é quase sempre um dos do perfil) e com o id
    // pedido em primeiro lugar, que ajuda a ler os logs de SQL.
    var ids = new java.util.LinkedHashSet<String>();
    ids.add(idDoNegocio);

    resolverPerfil(idDoNegocio).ifPresent(perfil -> {
      if (StringUtils.hasText(perfil.getSub())) ids.add(perfil.getSub());
      if (StringUtils.hasText(perfil.getUserIdOld())) ids.add(perfil.getUserIdOld());
    });

    return List.copyOf(ids);
  }

  /**
   * Resolve um perfil a partir de um identificador do NEGÓCIO, seja ele o {@code sub} (UUID, gravado
   * pelos fluxos autenticados) ou o {@code user_id_old} (id numérico do sistema legacy).
   *
   * <p>É o ponto de entrada único para o caminho inverso — dado um id guardado no negócio, obter o
   * utilizador. Qualquer leitura que faça {@code findBySub} directamente devolve vazio para as
   * linhas gravadas com o id legacy, que é a causa dos nomes "N/A".</p>
   */
  @Transactional(readOnly = true)
  public Optional<IAMUserProfileEntity> resolverPerfil(String idDoNegocio) {
    if (!StringUtils.hasText(idDoNegocio)) return Optional.empty();
    return repository.findBySub(idDoNegocio)
        .or(() -> repository.findByUserIdOld(idDoNegocio));
  }

  /**
   * Versão em lote do {@link #resolverPerfil}: devolve um mapa {@code id do negócio -> perfil}, em
   * que a chave é exactamente o valor que veio na colecção. Ids sem correspondência ficam de fora.
   *
   * <p>Usar nas listagens. A alternativa — chamar o {@link #resolverPerfil} dentro do loop — faz
   * duas queries por linha da página.</p>
   */
  @Transactional(readOnly = true)
  public java.util.Map<String, IAMUserProfileEntity> resolverPerfis(Collection<String> idsDoNegocio) {
    if (idsDoNegocio == null || idsDoNegocio.isEmpty()) return java.util.Map.of();

    var ids = idsDoNegocio.stream().filter(StringUtils::hasText).distinct().toList();
    if (ids.isEmpty()) return java.util.Map.of();

    var resultado = new java.util.HashMap<String, IAMUserProfileEntity>();

    // Duas queries no total, independentemente do tamanho da página. A ordem espelha a do
    // resolverPerfil: o sub manda, e a chave legacy só preenche o que sobrar.
    repository.findBySubIn(ids).forEach(p -> resultado.put(p.getSub(), p));

    var emFalta = ids.stream().filter(id -> !resultado.containsKey(id)).toList();
    if (!emFalta.isEmpty()) {
      repository.findByUserIdOldIn(emFalta).forEach(p -> resultado.put(p.getUserIdOld(), p));
    }

    return resultado;
  }

  /**
   * Nome legível para cada id do negócio, com o próprio id como valor de recurso quando o perfil
   * não existe. Conveniência sobre o {@link #resolverPerfis} para as listagens que só querem o nome.
   */
  @Transactional(readOnly = true)
  public java.util.Map<String, String> resolverNomes(Collection<String> idsDoNegocio) {
    var perfis = resolverPerfis(idsDoNegocio);
    var nomes = new java.util.HashMap<String, String>();
    perfis.forEach((id, perfil) -> nomes.put(id, nomeDe(perfil, id)));
    return nomes;
  }

  private static String nomeDe(IAMUserProfileEntity perfil, String recurso) {
    if (StringUtils.hasText(perfil.getFullName())) return perfil.getFullName();
    if (StringUtils.hasText(perfil.getUsername())) return perfil.getUsername();
    return recurso;
  }

  // ---------------------------------------------------------------------------------------------
  // Sincronização a partir do JWT (centralizado aqui; delegado pelo IAMUserProfileSyncFilter)
  // ---------------------------------------------------------------------------------------------

  /**
   * Sincroniza o perfil IAM a partir do JWT: se já existir linha pelo sub, atualiza os dados; caso
   * contrário tenta reconciliar com a linha legacy importada (match por email, mantendo id e
   * user_id_old) antes de criar uma nova.
   *
   * <p><b>Chamado em todos os pedidos autenticados</b> (ver {@code IAMUserProfileSyncFilter}), daí o
   * curto-circuito inicial: se este {@code sub} já foi sincronizado, os claims não mudaram e ainda
   * estamos dentro do TTL, retorna sem tocar na base de dados. Antes, cada pedido — cada listagem,
   * cada dropdown — abria uma transação de escrita e consumia uma ligação do pool para sincronizar
   * dados que mudam talvez uma vez por mês.</p>
   *
   * <p>O método não é {@code @Transactional} de propósito: a transação abria <em>antes</em> do
   * curto-circuito, o que anulava o ganho. As escritas continuam atómicas porque cada chamada ao
   * repositório do Spring Data traz a sua própria transação, e a operação é um upsert de uma única
   * linha — não há invariante entre statements a proteger. Se dois primeiros logins do mesmo
   * utilizador correrem em paralelo, um deles falha no PK (o id é o próprio sub), o filtro regista o
   * aviso e o pedido seguinte sincroniza.</p>
   */
  public void syncFromJwt(Jwt jwt) {
    String sub = jwt.getSubject();
    if (!StringUtils.hasText(sub)) return;

    int claimsHash = claimsHash(jwt);
    Integer marca = syncMarks.getIfPresent(sub);

    if (java.util.Objects.equals(marca, claimsHash)) {
      // Caminho esmagadoramente mais comum. TRACE de proposito: a INFO seria uma linha por pedido.
      LOGGER.trace("iam.profile.skip sub={} motivo=marca_valida", sub);
      return;
    }

    // Sem marca => primeiro pedido do utilizador, TTL expirado, ou entrada descartada pelo limite.
    // Marca diferente => os claims mudaram no Keycloak; ha mesmo algo novo para gravar.
    LOGGER.debug("iam.profile.sync sub={} motivo={}", sub,
        marca == null ? "sem_marca" : "claims_alterados");

    doSync(jwt, sub);
    syncMarks.put(sub, claimsHash);
  }

  private void doSync(Jwt jwt, String sub) {
    var existing = repository.findBySub(sub);

    if (existing.isPresent()) {
      var alterados = applyChanges(existing.get(), jwt);
      if (alterados.isEmpty()) {
        // Viemos a BD por causa do TTL, nao por mudanca de dados: 1 SELECT, zero escritas.
        LOGGER.debug("iam.profile.unchanged sub={} (revalidado, sem escrita)", sub);
        return;
      }
      repository.save(existing.get());
      LOGGER.info("iam.profile.updated sub={} campos={}", sub, alterados);
      return;
    }

    // Sem linha por sub: primeiro login neste Keycloak. Reconciliar com a linha legacy
    // importada (match por email, case-insensitive) antes de criar.
    reconcileOrCreate(jwt, sub);
  }

  /**
   * Impressao digital dos claims que gravamos. E o que permite detetar uma alteracao no Keycloak
   * sem ir a base de dados: token novo com claims diferentes => hash diferente => a marca deixa de
   * bater certo e sincronizamos no pedido seguinte, sem esperar pelo TTL.
   *
   * <p><b>INVARIANTE: este hash tem de cobrir exatamente os mesmos claims que o
   * {@link #applyChanges(IAMUserProfileEntity, Jwt)} grava.</b> Hoje sao quatro:
   * {@code preferred_username}, {@code email}, {@code given_name}, {@code family_name} — os mesmos
   * de onde saem os campos username/email/firstName/lastName/fullName. Um campo gravado no
   * applyChanges mas ausente daqui produz uma falha silenciosa: a alteracao fica por escrever ate
   * o TTL expirar. Se mexeres num, mexe no outro.</p>
   */
  private static int claimsHash(Jwt jwt) {
    return java.util.Objects.hash(
        jwt.getClaimAsString("preferred_username"),
        jwt.getClaimAsString("email"),
        jwt.getClaimAsString("given_name"),
        jwt.getClaimAsString("family_name"));
  }

  private void reconcileOrCreate(Jwt jwt, String sub) {
    String email = normalizeEmail(jwt.getClaimAsString("email"));

    if (email != null) {
      var byEmail = repository.findByEmailIgnoreCase(email);
      if (byEmail.isPresent()) {
        var profile = byEmail.get();
        if (!StringUtils.hasText(profile.getSub())) {
          profile.setSub(sub);
          applyChanges(profile, jwt);
          repository.save(profile);
          // Acontece UMA vez na vida de cada utilizador importado. A INFO e deliberada: e a
          // ligacao entre a identidade nova (sub) e o historico legacy (user_id_old), e se
          // alguma vez houver duvidas sobre dados antigos e por aqui que se comeca.
          LOGGER.info("iam.profile.reconciled sub={} email={} userIdOld={}",
              sub, email, profile.getUserIdOld());
        } else if (!profile.getSub().equals(sub)) {
          LOGGER.warn("iam.profile.email_conflito email={} pertence ao sub={}; sub={} nao reconciliado",
              email, profile.getSub(), sub);
        }
        return;
      }
    }

    var criado = repository.save(buildEntity(jwt));
    LOGGER.info("iam.profile.created sub={} username={}", sub, criado.getUsername());
  }

  /**
   * Copia para a entidade os campos do JWT que mudaram e devolve os NOMES dos campos alterados
   * (lista vazia = nada a gravar). A lista serve para o log dizer o que mudou, e nao apenas que
   * mudou alguma coisa.
   *
   * <p><b>Ao acrescentar aqui um campo novo, acrescenta o claim correspondente ao
   * {@link #claimsHash(Jwt)}.</b> Os dois metodos tem de cobrir exatamente o mesmo conjunto de
   * claims. Se um campo for gravado aqui mas ficar de fora do hash, uma alteracao desse campo no
   * Keycloak nao muda o hash, a marca em memoria continua a bater certo e a alteracao so chega a
   * base de dados quando o TTL expirar — ate 30 minutos depois, em silencio. Falha discreta e
   * dificil de diagnosticar, por isso confirma sempre os dois lados.</p>
   */
  private List<String> applyChanges(IAMUserProfileEntity entity, Jwt jwt) {
    String preferredUsername = jwt.getClaimAsString("preferred_username");
    String email = jwt.getClaimAsString("email");
    String firstName = jwt.getClaimAsString("given_name");
    String lastName = jwt.getClaimAsString("family_name");
    String fullName = buildFullName(firstName, lastName);

    String username = (preferredUsername != null && !preferredUsername.isBlank())
        ? preferredUsername
        : email;

    var alterados = new ArrayList<String>();

    if (username != null && !username.equals(entity.getUsername())) {
      entity.setUsername(username);
      alterados.add("username");
    }
    if (!java.util.Objects.equals(email, entity.getEmail())) {
      entity.setEmail(email);
      alterados.add("email");
    }
    if (!java.util.Objects.equals(firstName, entity.getFirstName())) {
      entity.setFirstName(firstName);
      alterados.add("firstName");
    }
    if (!java.util.Objects.equals(lastName, entity.getLastName())) {
      entity.setLastName(lastName);
      alterados.add("lastName");
    }
    if (!java.util.Objects.equals(fullName, entity.getFullName())) {
      entity.setFullName(fullName);
      alterados.add("fullName");
    }

    return alterados;
  }

  private IAMUserProfileEntity buildEntity(Jwt jwt) {
    String sub = jwt.getSubject();
    String preferredUsername = jwt.getClaimAsString("preferred_username");
    String email = jwt.getClaimAsString("email");
    String firstName = jwt.getClaimAsString("given_name");
    String lastName = jwt.getClaimAsString("family_name");

    String username = (preferredUsername != null && !preferredUsername.isBlank())
        ? preferredUsername
        : email;

    if (username == null || username.isBlank()) {
      throw new IllegalStateException("Could not determine username from JWT claims. sub=" + sub);
    }

    var entity = new IAMUserProfileEntity();
    entity.setId(UUID.fromString(sub));
    entity.setSub(sub);
    entity.setUsername(username);
    entity.setEmail(email);
    entity.setFirstName(firstName);
    entity.setLastName(lastName);
    entity.setFullName(buildFullName(firstName, lastName));
    return entity;
  }

  private static String normalizeEmail(String email) {
    return (email != null && !email.isBlank()) ? email.trim() : null;
  }

  private static String buildFullName(String firstName, String lastName) {
    String first = StringUtils.hasText(firstName) ? firstName.strip() : null;
    String last = StringUtils.hasText(lastName) ? lastName.strip() : null;
    if (first == null && last == null) return null;
    if (first == null) return last;
    if (last == null) return first;
    return first + " " + last;
  }
}
