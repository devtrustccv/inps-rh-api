package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisReqDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FamiliarEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoMovimentoEntityRepository;
import cv.inps.rh.shared.service.NifSearchService;
import cv.inps.rh.shared.service.model.nif.EntriesDTO;
import cv.inps.rh.shared.service.model.nif.EntryDTO;
import cv.inps.rh.shared.service.model.nif.RootResponseDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ColaboradorValidationRules {

  private static final Logger LOGGER = LoggerFactory.getLogger(ColaboradorValidationRules.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final FamiliarEntityRepository familiarEntityRepository;
  private final EntityManager entityManager;
  private final NifSearchService nifSearchService;

  public void validarDadosPessoais(DadosPessoaisReqDTO dp, UUID uuidExistente) {
    validarDocumentoUnico(dp, uuidExistente);
    validarNif(dp.getNif(), uuidExistente);
    validarNifCorrespondeColaborador(dp);
    validarCamposObrigatorios(dp);
  }

  private void validarDocumentoUnico(DadosPessoaisReqDTO dp, UUID uuidExistente) {
    boolean duplicado = (uuidExistente == null)
        ? funcionarioEntityRepository.existsByTipoDocumentoId_idAndNumDocumento(
            dp.getTipoDocumentoId(), dp.getNumDocumento())
        : funcionarioEntityRepository.existsByTipoDocumentoId_IdAndNumDocumentoAndUuidNot(
            dp.getTipoDocumentoId(), dp.getNumDocumento(), uuidExistente);

    if (duplicado) {
      throw IgrpResponseStatusException.conflict(
          "Já existe um colaborador registado com este número de documento de identificação. Verifique os dados introduzidos.");
    }
  }

  private static final int NIF_LENGTH = 9;

  private void validarNif(Long nif, UUID uuidExistente) {
    if (nif == null || nif <= 0) {
      throw IgrpResponseStatusException.badRequest("O NIF é obrigatório e deve ser um número positivo.");
    }
    if (String.valueOf(nif).length() != NIF_LENGTH) {
      throw IgrpResponseStatusException.badRequest("O NIF deve ter exactamente " + NIF_LENGTH + " dígitos.");
    }

    boolean duplicado = (uuidExistente == null)
        ? funcionarioEntityRepository.existsByNifAndEstadoNot(nif, Estado.E)
        : funcionarioEntityRepository.existsByNifAndUuidNotAndEstadoNot(nif, uuidExistente, Estado.E);

    if (duplicado) {
      throw IgrpResponseStatusException.conflict(
          "Já existe um colaborador registado com o NIF informado. Verifique os dados introduzidos.");
    }
  }

  /**
   * Regra (spec DOSSIÊ): o NIF indicado tem de corresponder efetivamente ao colaborador, validado
   * contra a API de pesquisa NIF por nome, data de nascimento, nome da mãe e nome do pai.
   *
   * <p>Consulta autoritativa pelo NIF; compara os campos normalizados (sem acentos/caixa/espaços a
   * mais). Só invalida quando ambos os lados têm o valor e diferem — campos em falta não geram falso
   * negativo. Falha de rede/API indisponível é <em>fail-open</em> (regista e segue): uma integração
   * em baixo não é o mesmo que "NIF não corresponde", e não deve bloquear o registo.
   *
   * <p>Quando invalida, a mensagem diz <b>que campos</b> divergem e <b>que valor</b> a API tem, para
   * o utilizador poder corrigir sem adivinhar.
   */
  private void validarNifCorrespondeColaborador(DadosPessoaisReqDTO dp) {
    Long nif = dp.getNif();
    if (nif == null) return; // obrigatoriedade já tratada em validarNif

    RootResponseDTO resposta;
    try {
      resposta = nifSearchService.getEntries(null, null, nif);
    } catch (RestClientException e) {
      LOGGER.warn("Validação NIF↔colaborador ignorada: API de pesquisa NIF indisponível (nif={}): {}",
          nif, e.getMessage());
      return;
    }

    // Resposta sem corpo/sem bloco de entries = integração a responder mal, não "NIF errado" →
    // fail-open, como na falha de rede. Só uma lista de entries vazia é resposta legítima.
    var entries = Optional.ofNullable(resposta).map(RootResponseDTO::getEntries).orElse(null);
    if (entries == null) {
      LOGGER.warn("Validação NIF↔colaborador ignorada: API de pesquisa NIF devolveu resposta vazia (nif={})", nif);
      return;
    }

    var entry = Optional.ofNullable(entries.getEntry())
        .orElse(List.of()).stream()
        .filter(e -> e != null && Objects.equals(e.getNuNif(), nif))
        .findFirst()
        .orElse(null);

    if (entry == null) {
      throw IgrpResponseStatusException.conflict(
          "O NIF %d não foi encontrado no cadastro de contribuintes. Confirme o número introduzido."
              .formatted(nif));
    }

    var divergencias = divergenciasNif(dp, entry);
    if (!divergencias.isEmpty()) {
      throw IgrpResponseStatusException.conflict(
          "O NIF %d pertence a outra pessoa: %s. Confirme o NIF e os dados do colaborador."
              .formatted(nif, String.join("; ", divergencias)));
    }
  }

  /**
   * Lista legível dos campos que não coincidem com o cadastro do NIF, no formato
   * "Nome: introduziu 'X', o NIF está registado em nome de 'Y'". Vazia = tudo bate.
   */
  private List<String> divergenciasNif(DadosPessoaisReqDTO dp, EntryDTO entry) {
    var divergencias = new ArrayList<String>();
    if (!nomeNifBate(dp.getNome(), entry.getNmContribuinte(), entry.getNmPesquisa()))
      divergencias.add(descreverDivergencia("Nome", dp.getNome(),
          primeiroPreenchido(entry.getNmContribuinte(), entry.getNmPesquisa())));
    if (!nomeNifBate(dp.getNomeMae(), entry.getNmMae(), entry.getNmPesquisaMae()))
      divergencias.add(descreverDivergencia("Nome da mãe", dp.getNomeMae(),
          primeiroPreenchido(entry.getNmMae(), entry.getNmPesquisaMae())));
    if (!nomeNifBate(dp.getNomePai(), entry.getNmPai(), entry.getNmPesquisaPai()))
      divergencias.add(descreverDivergencia("Nome do pai", dp.getNomePai(),
          primeiroPreenchido(entry.getNmPai(), entry.getNmPesquisaPai())));
    if (!dataNifBate(dp.getDataNascimento(), entry.getDtNasc()))
      divergencias.add(descreverDivergencia("Data de nascimento",
          String.valueOf(dp.getDataNascimento()), String.valueOf(parseDataApiNif(entry.getDtNasc()))));
    return divergencias;
  }

  private static String descreverDivergencia(String campo, String introduzido, String noCadastro) {
    return "%s: introduziu \"%s\", no cadastro do NIF consta \"%s\"".formatted(campo, introduzido, noCadastro);
  }

  private static String primeiroPreenchido(String a, String b) {
    return StringUtils.hasText(a) ? a : b;
  }

  /** Bate se o valor do colaborador coincide com o da API (ou a sua variante de pesquisa). Valores
   *  em falta de um dos lados não invalidam — só invalida quando ambos existem e diferem. */
  private boolean nomeNifBate(String doColaborador, String daApi, String daApiPesquisa) {
    String a = normalizarParaComparacao(doColaborador);
    if (a == null) return true;
    String b1 = normalizarParaComparacao(daApi);
    String b2 = normalizarParaComparacao(daApiPesquisa);
    if (b1 == null && b2 == null) return true;
    return a.equals(b1) || a.equals(b2);
  }

  private boolean dataNifBate(LocalDate doColaborador, String daApi) {
    if (doColaborador == null) return true;
    LocalDate b = parseDataApiNif(daApi);
    if (b == null) return true; // formato desconhecido/em falta → não invalida
    return doColaborador.isEqual(b);
  }

  private static String normalizarParaComparacao(String v) {
    String n = NifSearchService.normalizeName(v);
    if (n == null) return null;
    n = n.trim().toUpperCase();
    return n.isEmpty() ? null : n;
  }

  private static final List<DateTimeFormatter> FORMATOS_DATA_NIF = List.of(
      DateTimeFormatter.ISO_LOCAL_DATE,
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd"));

  private static LocalDate parseDataApiNif(String v) {
    if (!StringUtils.hasText(v)) return null;
    String s = v.trim();
    if (s.length() > 10) s = s.substring(0, 10); // corta a hora, se vier um datetime
    for (var f : FORMATOS_DATA_NIF) {
      try {
        return LocalDate.parse(s, f);
      } catch (DateTimeParseException ignored) {
        // tenta o próximo formato
      }
    }
    return null;
  }

  private void validarCamposObrigatorios(DadosPessoaisReqDTO dp) {
    if (!StringUtils.hasText(dp.getNomePai())) {
      throw IgrpResponseStatusException.badRequest("Nome do Pai é obrigatório.");
    }
    if (!StringUtils.hasText(dp.getNacionalidade())) {
      throw IgrpResponseStatusException.badRequest("Nacionalidade é obrigatória.");
    }
    if (dp.getNaturalidadeId() == null) {
      throw IgrpResponseStatusException.badRequest("Naturalidade é obrigatória.");
    }

    if (dp.getTipoDocumentoId() != null
        && entityManager.find(TipoDocumentoEntity.class, dp.getTipoDocumentoId()) == null) {
      throw IgrpResponseStatusException.badRequest("Tipo de documento inválido: o valor indicado não existe.");
    }
    if (entityManager.find(GeografiaEntity.class, dp.getNaturalidadeId()) == null) {
      throw IgrpResponseStatusException.badRequest("Naturalidade inválida: o valor indicado não existe.");
    }
  }

  public void validarHabilitacoesLiterarias(List<HabilitacaoLiterariaReqDTO> habilitacoes) {
    if (habilitacoes == null) return;
    for (var h : habilitacoes) {
      if (h.getPaisId() == null) {
        throw IgrpResponseStatusException.badRequest("O campo País é obrigatório para cada habilitação literária.");
      }
      if (!StringUtils.hasText(h.getGrauAcademico())) {
        throw IgrpResponseStatusException.badRequest("O campo Grau Académico é obrigatório para cada habilitação literária.");
      }
    }
  }

  public void verificarDuplicidadeFamiliares(List<AgregadoDependenteReqDTO> novos,
                                              List<FamiliarEntity> existentes) {
    if (novos == null) return;
    var docsExistentes = new HashSet<String>();
    if (existentes != null) {
      for (var f : existentes) {
        if (f.getEstado() != Estado.E && StringUtils.hasText(f.getNumDocumento())) {
          docsExistentes.add(f.getNumDocumento().trim().toUpperCase());
        }
      }
    }
    var docsNovos = new HashSet<String>();
    for (var dto : novos) {
      if (dto.getId() != null) continue;
      if (!StringUtils.hasText(dto.getNumDocumento())) continue;
      String doc = dto.getNumDocumento().trim().toUpperCase();
      String docOriginal = dto.getNumDocumento().trim();
      String quem = StringUtils.hasText(dto.getNome()) ? " (" + dto.getNome().trim() + ")" : "";
      // Já existe na BD, no agregado deste colaborador.
      if (docsExistentes.contains(doc)) {
        throw IgrpResponseStatusException.conflict(
            "Já existe um agregado/dependente" + quem + " com o número de documento " + docOriginal
                + " associado a este colaborador. Cada agregado deve ter um número de documento único.");
      }
      // Repetido dentro do próprio pedido (dois ou mais agregados com o mesmo documento no formulário).
      if (!docsNovos.add(doc)) {
        throw IgrpResponseStatusException.conflict(
            "Adicionou mais do que um agregado/dependente com o número de documento " + docOriginal
                + ". Cada agregado deve ter um número de documento único.");
      }
    }
  }

  /**
   * Regra de negócio: um dependente/familiar só pode ter UM colaborador responsável pelo seu agregado.
   * Uma pessoa pode pertencer a agregados de colaboradores diferentes, mas apenas 1 é responsável.
   * Verifica, para cada familiar em que o colaborador atual é responsável, se já existe outro
   * colaborador registado como responsável pelo mesmo documento.
   */
  public void verificarResponsavelUnicoAgregado(List<AgregadoDependenteReqDTO> novos, UUID funcionarioUuid) {
    if (CollectionUtils.isEmpty(novos)) return;
    for (var dto : novos) {
      if (!isResponsavel(dto.getResponsavel()) || !StringUtils.hasText(dto.getNumDocumento())) continue;
      var doc = dto.getNumDocumento().trim();
      boolean outroResponsavel = familiarEntityRepository
          .findByNumDocumentoAndEstadoIn(doc, List.of(Estado.A, Estado.P)).stream()
          .filter(f -> f.getFunId() == null || funcionarioUuid == null
              || !funcionarioUuid.equals(f.getFunId().getUuid()))
          .anyMatch(f -> isResponsavel(f.getResponsavel()));
      if (outroResponsavel) {
        throw IgrpResponseStatusException.conflict(
            "O referido familiar já possui outro colaborador associado como seu responsável.");
      }
    }
  }

  private boolean isResponsavel(String valor) {
    if (valor == null) return false;
    var v = valor.trim().toUpperCase();
    return v.equals("SIM") || v.equals("S") || v.equals("1") || v.equals("TRUE") || v.equals("Y");
  }

  public void validarEncargosDescontosDuplicados(List<EncargosDescontosReqDTO> encargos) {
    if (CollectionUtils.isEmpty(encargos)) return;
    var seen = new HashSet<Long>();
    for (var e : encargos) {
      if (e.getTipoEncargoId() != null && !seen.add(e.getTipoEncargoId())) {
        var descricao = tipoMovimentoEntityRepository.findById(e.getTipoEncargoId())
            .map(TipoMovimentoEntity::getDescricao).orElse("Desconhecido");
        throw IgrpResponseStatusException.conflict(
            "O encargo/desconto '" + descricao + "' foi adicionado mais do que uma vez.");
      }
    }
  }

  public void validarSubsidiosDuplicados(List<SubsidioReqDTO> subsidios) {
    if (CollectionUtils.isEmpty(subsidios)) return;
    var seen = new HashSet<Long>();
    for (var s : subsidios) {
      if (s.getTipoSubsidioId() != null && !seen.add(s.getTipoSubsidioId())) {
        var descricao = tipoMovimentoEntityRepository.findById(s.getTipoSubsidioId())
            .map(TipoMovimentoEntity::getDescricao).orElse("Desconhecido");
        throw IgrpResponseStatusException.conflict(
            "O subsídio/remuneração '" + descricao + "' foi adicionado mais do que uma vez.");
      }
    }
  }

  public Set<Long> getTipoMovimentoIdsDePagamentos(List<DefPagamentoEntity> pagamentos) {
    if (CollectionUtils.isEmpty(pagamentos)) return Set.of();
    return pagamentos.stream()
        .filter(p -> p.getTmId() != null)
        .map(p -> p.getTmId().getId())
        .collect(Collectors.toSet());
  }

  public Set<Long> getTipoMovimentoIdsDeRemuneracoes(List<DefinicaoRemuneracaoEntity> remuneracoes) {
    if (CollectionUtils.isEmpty(remuneracoes)) return Set.of();
    return remuneracoes.stream()
        .filter(r -> r.getTmId() != null)
        .map(r -> r.getTmId().getId())
        .collect(Collectors.toSet());
  }

  /** true se o tipo de vínculo tem salário (RH_T_PARAM_VINCULO.FLG_SALARIO = 1). */
  public boolean vinculoTemSalario(Long tipoVinculoId) {
    if (tipoVinculoId == null) return false;
    var vinculo = entityManager.find(ParamVinculoEntity.class, tipoVinculoId);
    return vinculo != null && Objects.equals(1, vinculo.getFlgSalario());
  }

  /**
   * Regra (spec DOSSIÊ, formulário "Dados Bancários"): se o vínculo do colaborador tem salário
   * (flgSalario = 1), o NIB é obrigatório — tem de existir pelo menos um registo bancário e cada
   * registo enviado tem de ter NIB preenchido. Sem salário, o NIB é opcional.
   * Usar nos fluxos onde o request traz o estado completo dos bancários (registo / validar registo).
   *
   * @param tipoVinculoId id do tipo de vínculo (RH_T_PARAM_VINCULO); se null, não valida.
   * @param bancarios registos bancários enviados no request.
   */
  public void validarNibObrigatorioSeSalario(Long tipoVinculoId, List<DadosBancariosReqDTO> bancarios) {
    if (!vinculoTemSalario(tipoVinculoId)) return;

    if (CollectionUtils.isEmpty(bancarios)) {
      throw IgrpResponseStatusException.badRequest("Erro: O Nib é Obrigatório");
    }
    for (var banco : bancarios) {
      if (banco == null || !StringUtils.hasText(banco.getNib())) {
        throw IgrpResponseStatusException.badRequest("Erro: O Nib é Obrigatório");
      }
    }
  }

  /**
   * Variante para o fluxo standalone: valida o estado EFETIVO dos dados bancários (existentes +
   * enviados, após o sync), considerando apenas registos activos/pendentes (ignora E e I). Assim
   * não falha ao aprovar/rejeitar uma validação em que o request não reenvia os registos.
   * Se o vínculo tem salário: tem de existir pelo menos um registo A/P e cada um com NIB.
   */
  public void validarNibObrigatorioSeSalarioEfetivo(Long tipoVinculoId, List<DadosBancariosEntity> bancariosEfetivos) {
    if (!vinculoTemSalario(tipoVinculoId)) return;

    var ativos = bancariosEfetivos == null ? List.<DadosBancariosEntity>of()
        : bancariosEfetivos.stream()
            .filter(b -> b != null && b.getEstado() != Estado.E && b.getEstado() != Estado.I)
            .collect(Collectors.toList());

    if (ativos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Erro: O Nib é Obrigatório");
    }
    for (var banco : ativos) {
      if (!StringUtils.hasText(banco.getNib())) {
        throw IgrpResponseStatusException.badRequest("Erro: O Nib é Obrigatório");
      }
    }
  }
}