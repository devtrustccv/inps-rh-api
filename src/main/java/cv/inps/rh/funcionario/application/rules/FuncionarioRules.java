package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContactoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;

@Component
@RequiredArgsConstructor
public class FuncionarioRules {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final ContactoEntityRepository contactoEntityRepository;


  public boolean temValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository.existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(),
        referenciaName.name());
  }

  /** Existe validação em correção (estado C) — usada para confirmar que o registo foi devolvido pelo checker. */
  public boolean temValidacaoPorCorrigir(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository.existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.C, tipoAccao.name(),
        referenciaName.name());
  }

  /**
   * Guard de escrita: impede alterar/validar um registo que esteja Inactivo (I) ou Eliminado (E).
   * Lança 400 com mensagem clara para o cliente. Chamar depois de carregar o registo alvo, antes de mutar.
   */
  public void garantirEditavel(Estado estado) {
    if (estado == Estado.I || estado == Estado.E) {
      throw IgrpResponseStatusException.badRequest(
          "Este registo está %s e não pode ser alterado."
              .formatted(estado == Estado.E ? "eliminado" : "inactivo"));
    }
  }

  /** Igual a {@link #garantirEditavel(Estado)} para entidades que guardam o estado como texto. */
  public void garantirEditavel(String estado) {
    if (estado == null) return;
    garantirEditavel(Estado.valueOf(estado));
  }

  /**
   * FLG_PROCESSA do tiprel no REGISTO/VALIDACAO do colaborador, derivado de
   * RH_T_PARAM_SITUACAO.FLG_REMUNERACAO (use case), como nos restantes fluxos do dossie.
   * <p>Diferenca deliberada face aos outros: aqui o default e <b>1</b>. Um colaborador novo tem de
   * processar salario, logo so desce a 0 quando a situacao laboral escolhida diz explicitamente que
   * nao remunera (FLG_REMUNERACAO=0) — nunca por omissao (sem situacao, sem param ou flag nula).
   */
  public int flgProcessaDoRegisto(SituacaoLaboralEntity situacao) {
    var param = situacao != null ? situacao.getSituacaoLaboralId() : null;
    if (param == null || param.getFlgRemuneracao() == null) return 1;
    return Integer.valueOf(0).equals(param.getFlgRemuneracao()) ? 0 : 1;
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoAtual(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid)
        .orElseThrow(()-> IgrpResponseStatusException.badRequest("Funcionario sem tipo de relacionamento atual"));
  }

  /**
   * Igual a {@link #getTipoRelacionamentoAtual} mas devolve {@code null} em vez de lançar quando o
   * funcionário não tem relacionamento atual (est_act_adm=1) — ex.: o contrato atual foi desativado
   * pelo toggle de estado. Usar quando a ausência de vínculo atual é um caso válido a tratar.
   */
  public TiposRelacionamentoEntity getTipoRelacionamentoAtualOrNull(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid).orElse(null);
  }

  /**
   * Vínculo (RH_T_PARAM_VINCULO) do contrato da relação laboral ATUAL (tiprel est_act_adm=1). É este,
   * e não o de contratos anteriores, que decide se o colaborador pode ter carreira/salário — ex.:
   * começou com Estágio (sem carreira) e passou a Contratado (com carreira). Null se não houver.
   */
  public ParamVinculoEntity getVinculoAtual(UUID funUuid) {
    var atual = getTipoRelacionamentoAtualOrNull(funUuid);
    return atual != null && atual.getContrVinculoId() != null ? atual.getContrVinculoId().getVinculoId() : null;
  }

  /** Bloqueia registo/alteração de carreira quando o vínculo atual não tem carreira (FLG_CARREIRA=0). */
  public void garantirVinculoAtualComCarreira(UUID funUuid) {
    var vinculo = getVinculoAtual(funUuid);
    if (vinculo == null || !Objects.equals(1, vinculo.getFlgCarreira())) {
      throw IgrpResponseStatusException.badRequest(
          "O vínculo atual do colaborador" + nomeVinculo(vinculo) + " não tem carreira.");
    }
  }

  /** Bloqueia registo de remunerações/descontos quando o vínculo atual não tem salário (FLG_SALARIO=NAO). */
  public void garantirVinculoAtualComSalario(UUID funUuid) {
    var vinculo = getVinculoAtual(funUuid);
    if (vinculo == null || !cv.inps.rh.shared.application.constants.custom.TipoSalarioVinculo.temSalario(vinculo.getFlgSalario())) {
      throw IgrpResponseStatusException.badRequest(
          "O vínculo atual do colaborador" + nomeVinculo(vinculo)
              + " não tem salário: não é possível registar remunerações nem descontos.");
    }
  }

  private static String nomeVinculo(ParamVinculoEntity vinculo) {
    return vinculo != null && vinculo.getNome() != null ? " (" + vinculo.getNome() + ")" : "";
  }


  public ContratoEntity getContratoComMaiorVersao(UUID funUuid) {
    return contratoEntityRepository.findTopByFunId_UuidOrderByVersaoDesc(funUuid);
  }

  /**
   * Contrato ATUAL do funcionário — determinado de forma SEGURA pelo tiprel com est_act_adm=1, NÃO
   * pela versão. Num funcionário com vários contratos (ex.: após um NOVO CONTRATO), todos os
   * contrato_vinculo têm versao=1 (o versionamento vive no histórico), pelo que ordenar por versão
   * empata e pode devolver o contrato errado. Devolve null se não houver relacionamento atual.
   */
  public ContratoEntity getContratoAtual(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid)
        .map(TiposRelacionamentoEntity::getContrVinculoId)
        .orElse(null);
  }


  public ContratoEntity getPrimeiroContrato(UUID funUuid) {
    return contratoEntityRepository.findPrimeiroContratoFuncionario(funUuid);
  }


  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(UUID funId, UUID contratoId) {
    if (funId == null || contratoId == null) return null;
    var atual = tiposRelacionamentoEntityRepository.findByFunUuidAndContratoUuid(funId, contratoId);
    if (atual != null) return atual;
    // Sem tiprel ÚLTIMO VÍNCULO (EST_ACT_ADM=1) para este contrato — ex.: Novo Contrato pendente, que só
    // passa a último vínculo na validação positiva: usa-se o tiprel mais recente do contrato.
    return tiposRelacionamentoEntityRepository
        .findFirstByFunId_UuidAndContrVinculoId_UuidOrderByIdDesc(funId, contratoId).orElse(null);
  }


  public List<TipoRelRemPagEntity> getRemuneracoesPagamentosAssociados(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelId_Id(tipoRelacionamentoId);


  }
    public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociados(Long tipoRelacionamentoId) {

      return tipoRelRemPagEntityRepository
          .findByTiprelId_Id(tipoRelacionamentoId)
          .stream()
          .map(TipoRelRemPagEntity::getRemId)
          .filter(Objects::nonNull)
          //.distinct()
          .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociados(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelId_Id(tipoRelacionamentoId)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }


  public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosAtivos(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
        .stream()
        .map(TipoRelRemPagEntity::getRemId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }

  public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosPendentes(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.P)
        .stream()
        .map(TipoRelRemPagEntity::getRemId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosAtivos(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosPendentes(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.P)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }

  /** Remunerações associadas num estado específico — usado pelo get-by-id de validação para
   *  devolver o snapshot no MESMO estado do funcionário (P pendente ou C em correção). */
  public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosPorEstado(Long tipoRelacionamentoId, Estado estado) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, estado)
        .stream()
        .map(TipoRelRemPagEntity::getRemId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }

  /** Pagamentos/descontos associados num estado específico — ver {@link #getRemuneracoesAssociadosPorEstado}. */
  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosPorEstado(Long tipoRelacionamentoId, Estado estado) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, estado)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }



  /**
   * Tiprel do Novo Contrato em validação (pendente P ou em correção C), lido da própria validação
   * (RH_T_VALIDACAO.TIPREL_ID). O novo tiprel só passa a último vínculo (EST_ACT_ADM=1) na validação
   * positiva, por isso NÃO se pode localizar pelo "atual". Null se não houver contrato em validação.
   */
  public TiposRelacionamentoEntity getTiprelContratoEmValidacao(UUID funUuid) {
    return getValidacao(funUuid, Estado.P, TipoAcao.INSERT, Referencia.CONTRATO)
        .or(() -> getValidacao(funUuid, Estado.C, TipoAcao.INSERT, Referencia.CONTRATO))
        .map(ValidacaoEntity::getTiprelId)
        .orElse(null);
  }

  public Optional<ValidacaoEntity> getValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {

    return validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(), referenciaName.name());
  }

  /** Validação de um funcionário num estado específico (P pendente, C em correção, ...). */
  public Optional<ValidacaoEntity> getValidacao(UUID funUuid, Estado estado, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, estado, tipoAccao.name(), referenciaName.name());
  }

  public Optional<ValidacaoEntity> getValidacaoPendenteByReferenciaUuid(UUID referenciaUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository
        .findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(referenciaUuid, Estado.P, tipoAccao.name(), referenciaName.name());
  }

  /**
   * Variante genérica por estado (o método acima fixa P). Usada pelo ciclo maker-checker da
   * mobilidade para localizar a validação DESTA referência num estado concreto — ex.: a que o
   * checker deixou em correção (Estado.C) e o maker vai reactivar (C -> P).
   */
  public Optional<ValidacaoEntity> getValidacaoByReferenciaUuid(UUID referenciaUuid, Estado estado, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository
        .findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(referenciaUuid, estado, tipoAccao.name(), referenciaName.name());
  }

  /**
   * Ciclo CORRIGIR (metade do CHECKER), partilhada por todos os serviços do dossiê. Devolve um
   * registo para correção: exige que a entidade esteja pendente (P) e exista validação pendente
   * desta referência (INSERT tem precedência sobre UPDATE), e passa essa validação a C. O caller
   * passa a própria entidade a Estado.C e grava ambas. Devolve a validação já em C.
   */
  public ValidacaoEntity devolverParaCorrecao(UUID referenciaUuid, Estado estadoEntidade, Referencia referenciaName) {
    if (estadoEntidade != Estado.P) {
      throw IgrpResponseStatusException.badRequest(
          "Só é possível devolver para correção um registo pendente de validação.");
    }
    var validacao = getValidacaoPendenteByReferenciaUuid(referenciaUuid, TipoAcao.INSERT, referenciaName)
        .or(() -> getValidacaoPendenteByReferenciaUuid(referenciaUuid, TipoAcao.UPDATE, referenciaName))
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Só é possível devolver para correção um registo pendente de validação."));
    validacao.setEstado(Estado.C);
    return validacao;
  }

  /**
   * Ciclo CORRIGIR (metade do MAKER), partilhada. Localiza a validação que o checker deixou em C
   * (INSERT tem precedência sobre UPDATE) e volta a pô-la em P. O caller passa a própria entidade a
   * Estado.P e grava. Devolve a validação já em P.
   */
  public ValidacaoEntity reabrirParaValidacao(UUID referenciaUuid, Referencia referenciaName) {
    var validacao = getValidacaoByReferenciaUuid(referenciaUuid, Estado.C, TipoAcao.INSERT, referenciaName)
        .or(() -> getValidacaoByReferenciaUuid(referenciaUuid, Estado.C, TipoAcao.UPDATE, referenciaName))
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Este registo não está por corrigir."));
    validacao.setEstado(Estado.P);
    return validacao;
  }

  public List<String> validarContactosDuplicados(List<ContactoReqDTO> contactos, UUID funUuid) {
    if (contactos == null || contactos.isEmpty()) return List.of();
    List<String> alertas = new ArrayList<>();
    for (var c : contactos) {
      if (c.getContacto() == null || c.getContacto().isBlank()) continue;
      boolean duplicado = (funUuid == null)
          ? contactoEntityRepository.existsByContactoAndEstadoNot(c.getContacto(), Estado.E)
          : contactoEntityRepository.existsByContactoAndFunId_UuidNotAndEstadoNot(c.getContacto(), funUuid, Estado.E);
      if (duplicado) {
        alertas.add("O contacto informado já está associado a outro colaborador: " + c.getContacto());
      }
    }
    return alertas;
  }


}
