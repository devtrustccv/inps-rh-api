package cv.inps.rh.funcionario.application.service.registodetalhe;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.audit.JaversAuditConfig;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContactoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosBancariosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoPessoalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EnderecoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FamiliarEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.HabilitacaoLiterariaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SituacaoLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Concentra a captura do "Detalhe de alterações" do REGISTO_COLABORADOR, mantendo o
 * {@code ValidarRegistoColaboradorService} focado no fluxo maker-checker.
 *
 * <p>Duas fases, ambas só no PUT (único momento em que o registo é editável):
 * <ul>
 *   <li>{@link #baseline(FuncionarioEntity)} no CORRIGIR (P→C): grava o estado atual pelos repos
 *       auditáveis SEM {@link ValidacaoAuditContext} (snapshot sem validacaoUuid → não entra na grelha),
 *       para o reenvio produzir um diff antes→depois em vez de um "valor inicial".</li>
 *   <li>{@link #capturar(FuncionarioEntity)} no reenvio (C→P): regrava DENTRO do contexto da validação,
 *       carimbando cada commit com o validacaoUuid — o JaVers diffa contra o baseline.</li>
 * </ul>
 *
 * <p>Os filhos que já têm repositório auditável são gravados por ele (o funcionário é ShallowReference,
 * logo o save em cascata não os fotografa). Os campos-núcleo do funcionário e do contrato — também
 * ShallowReference — são auditados via POJOs dedicados ({@link FuncionarioDadosPessoaisSnapshot},
 * {@link ContratoDadosSnapshot}) commitados diretamente no JaVers, sem tocar na config global.
 */
@Service
@RequiredArgsConstructor
public class RegistoDetalheCapturaService {

  private final FuncionarioRules funcionarioRules;
  private final Javers javers;
  private final AuthorProvider authorProvider;

  private final DadosBancariosEntityRepository dadosBancariosRepo;
  private final ContactoEntityRepository contactoRepo;
  private final EnderecoEntityRepository enderecoRepo;
  private final FamiliarEntityRepository familiarRepo;
  private final HabilitacaoLiterariaEntityRepository habilitacaoRepo;
  private final DocumentoPessoalEntityRepository documentoPessoalRepo;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoRepo;
  private final DefPagamentoEntityRepository defPagamentoRepo;
  private final CarreiraEntityRepository carreiraRepo;
  private final MobilidadeEntityRepository mobilidadeRepo;
  private final SituacaoLaboralEntityRepository situacaoLaboralRepo;

  /** Baseline (CORRIGIR, P→C): grava o estado atual para o reenvio poder diffar. */
  public void baseline(FuncionarioEntity f) {
    TiposRelacionamentoEntity tr = funcionarioRules.getTipoRelacionamentoAtual(f.getUuid());

    grava(f.getDadosBancarios(), dadosBancariosRepo, RegistoDetalheCapturaService::vivoBancario);
    grava(f.getContactos(), contactoRepo, RegistoDetalheCapturaService::vivoContacto);
    grava(um(f.getEndereco()), enderecoRepo, RegistoDetalheCapturaService::vivoEndereco);
    grava(f.getFamiliares(), familiarRepo, RegistoDetalheCapturaService::vivoFamiliar);
    grava(f.getHabilitacoesLiterarias(), habilitacaoRepo, RegistoDetalheCapturaService::vivoHabilitacao);
    grava(um(f.getDocumentoPessoal()), documentoPessoalRepo, RegistoDetalheCapturaService::vivoDocumento);
    grava(f.getDefinicoesRenumeracoes(), definicaoRemuneracaoRepo, RegistoDetalheCapturaService::vivoRem);
    grava(f.getDefinicoesPagamentos(), defPagamentoRepo, RegistoDetalheCapturaService::vivoPag);
    if (tr != null) {
      grava(um(tr.getCarreiraId()), carreiraRepo, RegistoDetalheCapturaService::vivoCarreira);
      grava(um(tr.getMobId()), mobilidadeRepo, RegistoDetalheCapturaService::vivoMobilidade);
      grava(um(tr.getSituacLaboralId()), situacaoLaboralRepo, RegistoDetalheCapturaService::vivoSituacao);
    }

    snapshots(f, tr).forEach((snapshot, tabela) -> javers.commit(authorProvider.provide(), snapshot));
  }

  /** Captura (reenvio, C→P): regrava dentro do contexto da validação para o JaVers registar o diff. */
  public void capturar(FuncionarioEntity f) {
    ValidacaoEntity validacao = funcionarioRules
        .getValidacaoPendente(f.getUuid(), TipoAcao.INSERT, Referencia.REGISTO_COLABORADOR)
        .orElse(null);
    if (validacao == null) {
      return;
    }
    TiposRelacionamentoEntity tr = funcionarioRules.getTipoRelacionamentoAtual(f.getUuid());

    grava(f.getDadosBancarios(), dadosBancariosRepo, validacao, "RH_T_DADOS_BANCARIOS",
        RegistoDetalheCapturaService::vivoBancario);
    grava(f.getContactos(), contactoRepo, validacao, "RH_T_CONTACTO",
        RegistoDetalheCapturaService::vivoContacto);
    grava(um(f.getEndereco()), enderecoRepo, validacao, "RH_T_ENDERECO",
        RegistoDetalheCapturaService::vivoEndereco);
    grava(f.getFamiliares(), familiarRepo, validacao, "RH_T_FAMILIARES",
        RegistoDetalheCapturaService::vivoFamiliar);
    grava(f.getHabilitacoesLiterarias(), habilitacaoRepo, validacao, "RH_T_HABILITACOES_LITERARIAS",
        RegistoDetalheCapturaService::vivoHabilitacao);
    grava(um(f.getDocumentoPessoal()), documentoPessoalRepo, validacao, "RH_T_DOCUMENTO_PESSOAL",
        RegistoDetalheCapturaService::vivoDocumento);
    grava(f.getDefinicoesRenumeracoes(), definicaoRemuneracaoRepo, validacao, "RH_T_DEF_REMUNERACOES",
        RegistoDetalheCapturaService::vivoRem);
    grava(f.getDefinicoesPagamentos(), defPagamentoRepo, validacao, "RH_T_DEF_PAGAMENTOS",
        RegistoDetalheCapturaService::vivoPag);
    if (tr != null) {
      grava(um(tr.getCarreiraId()), carreiraRepo, validacao, "RH_T_CARREIRA",
          RegistoDetalheCapturaService::vivoCarreira);
      grava(um(tr.getMobId()), mobilidadeRepo, validacao, "RH_T_MOBILIDADE",
          RegistoDetalheCapturaService::vivoMobilidade);
      grava(um(tr.getSituacLaboralId()), situacaoLaboralRepo, validacao, "RH_T_SITUACAO_LABORAL",
          RegistoDetalheCapturaService::vivoSituacao);
    }

    snapshots(f, tr).forEach((snapshot, tabela) -> javers.commit(authorProvider.provide(), snapshot, Map.of(
        JaversAuditConfig.PROP_VALIDACAO_UUID, validacao.getUuid().toString(),
        JaversAuditConfig.PROP_VALIDACAO_ID, String.valueOf(validacao.getId()),
        JaversAuditConfig.PROP_TABELA, tabela)));
  }

  /** POJOs dedicados (funcionário/contrato), null-safe, mapeados por tabela. */
  private Map<Object, String> snapshots(FuncionarioEntity f, TiposRelacionamentoEntity tr) {
    var mapa = new java.util.LinkedHashMap<Object, String>();
    var funcionario = FuncionarioDadosPessoaisSnapshot.of(f);
    if (funcionario != null) {
      mapa.put(funcionario, "RH_T_FUNCIONARIOS");
    }
    ContratoEntity contrato = tr != null ? tr.getContrVinculoId() : null;
    var contratoSnap = ContratoDadosSnapshot.of(contrato);
    if (contratoSnap != null) {
      mapa.put(contratoSnap, "RH_T_CONTRATO_VINCULO");
    }
    return mapa;
  }

  // --- Helpers genéricos: gravam cada elemento vivo pelo repo auditável (fora/dentro do contexto) ---

  private <T> void grava(List<T> lista, JpaRepository<T, Long> repo, Predicate<T> vivo) {
    if (lista == null) {
      return;
    }
    lista.stream().filter(Objects::nonNull).filter(vivo).forEach(repo::save);
  }

  private <T> void grava(List<T> lista, JpaRepository<T, Long> repo, ValidacaoEntity validacao,
      String tabela, Predicate<T> vivo) {
    if (lista == null || lista.isEmpty()) {
      return;
    }
    try {
      ValidacaoAuditContext.set(validacao.getId(), validacao.getUuid(), tabela);
      lista.stream().filter(Objects::nonNull).filter(vivo).forEach(repo::save);
    } finally {
      ValidacaoAuditContext.clear();
    }
  }

  /** Adapta um filho 1:1 (ex.: endereço) à API baseada em lista. */
  private <T> List<T> um(T entidade) {
    return entidade == null ? null : List.of(entidade);
  }

  // Predicados de "vivo" por secção (as entidades não partilham interface de estado).
  private static boolean vivoBancario(cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoContacto(cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoEndereco(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoFamiliar(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoHabilitacao(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoDocumento(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoRem(cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoPag(cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoCarreira(cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoMobilidade(cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity e) { return e.getEstado() != Estado.E; }
  private static boolean vivoSituacao(cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity e) { return e.getEstado() != Estado.E; }
}
