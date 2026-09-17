package cv.inps.rh.funcionario.application.service.registodetalhe;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.detalhe.DossierCampos;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.detalhe.Campos;
import cv.inps.rh.shared.application.detalhe.DetalheAlteracoes;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * "Detalhe de alterações" do REGISTO_COLABORADOR — o único módulo em que uma validação atravessa
 * VÁRIAS tabelas (funcionário, contrato e 11 secções do dossiê).
 *
 * <p>O "antes" é capturado em memória no próprio pedido do reenvio (C→P), antes de o payload ser
 * aplicado — exactamente como nos outros módulos. Não é preciso gravar nada no CORRIGIR.
 *
 * <p>Cada secção congela-se com o seu {@code TABELA_NAME} e, nas coleções, com o {@code TABELA_ID} da
 * linha: sem isso, dois familiares a mudar o mesmo campo colidiam e a grelha perdia uma alteração.
 */
@Service
@RequiredArgsConstructor
public class RegistoDetalheCongelador {

  private final FuncionarioRules funcionarioRules;
  private final DetalheAlteracoes detalheAlteracoes;
  private final DossierCampos dossierCampos;
  // A mobilidade ja tinha declaracao propria (piloto); reutiliza-se em vez de duplicar.
  private final cv.inps.rh.funcionario.application.service.MobilidadeCampos mobilidadeCampos;

  /**
   * O estado do dossiê inteiro antes de o payload ser aplicado. Opaco para o chamador: só tem de o
   * obter no início do pedido e devolvê-lo ao {@link #congelar}.
   */
  public record Antes(Map<String, Map<Long, DetalheAlteracoes.Estado>> porTabela) {}

  /** Uma secção do dossiê: a tabela, a declaração dos campos e como obter as suas linhas. */
  private record Seccao<T>(String tabela, Campos<T> campos, List<T> linhas, Function<T, Long> id,
      Function<T, Estado> estado) {}

  /** Fotografa o dossiê inteiro. Chamar ANTES de aplicar o payload. */
  public Antes capturar(FuncionarioEntity f) {
    Map<String, Map<Long, DetalheAlteracoes.Estado>> mapa = new HashMap<>();
    for (Seccao<?> seccao : seccoes(f)) {
      mapa.put(seccao.tabela(), capturarSeccao(seccao));
    }
    return new Antes(mapa);
  }

  /**
   * Congela o diff de todas as secções contra o {@code antes}. NO-OP se não houver validação pendente
   * de registo — é o mesmo guard que o serviço anterior fazia.
   */
  public void congelar(FuncionarioEntity f, Antes antes) {
    ValidacaoEntity validacao = funcionarioRules
        .getValidacaoPendente(f.getUuid(), cv.inps.rh.shared.application.constants.custom.TipoAcao.INSERT,
            cv.inps.rh.shared.application.constants.custom.Referencia.REGISTO_COLABORADOR)
        .orElse(null);
    if (validacao == null) {
      return;
    }
    for (Seccao<?> seccao : seccoes(f)) {
      congelarSeccao(validacao, seccao, antes.porTabela().getOrDefault(seccao.tabela(), Map.of()));
    }
  }

  // ------------------------------------------------------------------------------------------------

  private <T> Map<Long, DetalheAlteracoes.Estado> capturarSeccao(Seccao<T> seccao) {
    Map<Long, DetalheAlteracoes.Estado> mapa = new HashMap<>();
    for (T linha : seccao.linhas()) {
      if (linha == null || seccao.id().apply(linha) == null) {
        continue;
      }
      mapa.put(seccao.id().apply(linha), detalheAlteracoes.capturar(seccao.campos(), linha));
    }
    return mapa;
  }

  private <T> void congelarSeccao(ValidacaoEntity validacao, Seccao<T> seccao,
      Map<Long, DetalheAlteracoes.Estado> antes) {
    for (T linha : seccao.linhas()) {
      if (linha == null) {
        continue;
      }
      Long id = seccao.id().apply(linha);
      // Linhas eliminadas no formulário não entram na grelha — o dossiê marca-as com estado E.
      if (id == null || seccao.estado().apply(linha) == Estado.E) {
        continue;
      }
      var estadoAntes = antes.getOrDefault(id, detalheAlteracoes.capturar(seccao.campos(), null));
      detalheAlteracoes.congelar(validacao, seccao.tabela(), id, seccao.campos(),
          estadoAntes, detalheAlteracoes.capturar(seccao.campos(), linha));
    }
  }

  /**
   * As secções do dossiê. O núcleo (funcionário/contrato) é linha única, por isso entra como lista de
   * um; as restantes são coleções. Acrescentar uma secção à grelha é acrescentar uma linha aqui.
   */
  private List<Seccao<?>> seccoes(FuncionarioEntity f) {
    TiposRelacionamentoEntity tr = funcionarioRules.getTipoRelacionamentoAtual(f.getUuid());
    ContratoEntity contrato = tr != null ? tr.getContrVinculoId() : null;

    List<Seccao<?>> seccoes = new ArrayList<>();
    seccoes.add(new Seccao<>(DossierCampos.T_FUNCIONARIOS, dossierCampos.funcionario(),
        um(f), FuncionarioEntity::getId, x -> null));
    seccoes.add(new Seccao<>(DossierCampos.T_CONTRATO_VINCULO, dossierCampos.contrato(),
        um(contrato), ContratoEntity::getId, x -> null));
    seccoes.add(new Seccao<>(DossierCampos.T_DADOS_BANCARIOS, dossierCampos.dadosBancarios(),
        lista(f.getDadosBancarios()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_CONTACTO, dossierCampos.contacto(),
        lista(f.getContactos()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_ENDERECO, dossierCampos.endereco(),
        um(f.getEndereco()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_FAMILIARES, dossierCampos.familiar(),
        lista(f.getFamiliares()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_HABILITACOES, dossierCampos.habilitacao(),
        lista(f.getHabilitacoesLiterarias()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_DOCUMENTO_PESSOAL, dossierCampos.documentoPessoal(),
        um(f.getDocumentoPessoal()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_DEF_REMUNERACOES, dossierCampos.rendimento(),
        lista(f.getDefinicoesRenumeracoes()), e -> e.getId(), e -> e.getEstado()));
    seccoes.add(new Seccao<>(DossierCampos.T_DEF_PAGAMENTOS, dossierCampos.desconto(),
        lista(f.getDefinicoesPagamentos()), e -> e.getId(), e -> e.getEstado()));
    if (tr != null) {
      seccoes.add(new Seccao<>(DossierCampos.T_CARREIRA, dossierCampos.carreira(),
          um(tr.getCarreiraId()), e -> e.getId(), e -> e.getEstado()));
      seccoes.add(new Seccao<>("RH_T_MOBILIDADE", mobilidadeCampos.get(),
          um(tr.getMobId()), e -> e.getId(), e -> e.getEstado()));
      seccoes.add(new Seccao<>(DossierCampos.T_SITUACAO_LABORAL, dossierCampos.situacaoLaboral(),
          um(tr.getSituacLaboralId()), e -> e.getId(), e -> e.getEstado()));
    }
    return seccoes;
  }

  private <T> List<T> um(T entidade) {
    return entidade == null ? List.of() : List.of(entidade);
  }

  private <T> List<T> lista(List<T> l) {
    return l == null ? List.of() : l;
  }
}
