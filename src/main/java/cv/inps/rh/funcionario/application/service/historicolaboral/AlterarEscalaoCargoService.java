package cv.inps.rh.funcionario.application.service.historicolaboral;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.AlterarEscalaoCargoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.constants.custom.TipoSalarioVinculo;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Melhoria 2.2.1 — "Alterar Escalão / Cargo" (referência de validação ALTERACAO_ESCALAO) para colaboradores com salário do PCCS
 * mas SEM carreira (o escalão vive no tiprel: RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID).
 *
 * <p>É a "progressão sem carreira": ao alterar o ESCALÃO, cria-se um tiprel pendente e, na validação,
 * fecha-se o vencimento anterior (por DATA_FIM, mantendo 'A') e abre-se um novo RH_T_DEF_REMUNERACOES
 * com o valor do novo escalão, re-associando os restantes RH_T_TIPREL_REM_PAG (subsídios/descontos) ao
 * novo tiprel. O dinheiro é escrito pelo Java (a PKG_AUMENTO_SALARIAL filtra por CARREIRA_ID e não se
 * aplica a vínculos sem carreira). Alterar só o CARGO é imediato (não vai a validação).</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AlterarEscalaoCargoService {

  // TIPO_SITUACAO (coluna do tiprel) usa valores do domínio TIPO_MOV_LABORAL.
  private static final String TIPO_SIT_ESCALAO = "ESCALAO_NOVO";
  private static final String TIPO_SIT_CARGO = "CARGO_NOVO";

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;
  private final EscalaoDetalheDiffWriter escalaoDetalheDiffWriter;

  /** Cria o movimento: CARGO só → imediato; ESCALÃO (com ou sem cargo) → pendente para validação. */
  public SuccessResponseDTO alterar(String funcionarioId, AlterarEscalaoCargoDTO dto) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var atual = tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funcionario.getUuid())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("O colaborador não tem relação laboral atual."));

    garantirPccsSemCarreira(atual);

    // "Tipo Alteração" (multiselect, DOMAINS=TIPO_MOV_LABORAL; referência de validação ALTERACAO_ESCALAO):
    // o frontend envia os valores selecionados (ESCALAO_NOVO/CARGO_NOVO) separados por vírgulas e guardam-se
    // tal-e-qual em TIPO_SITUACAO. A decisão do que alterar NÃO interpreta os códigos do domínio — usa apenas
    // a presença de novoEscalaoId / novoCargoId no pedido.
    boolean alterarEscalao = dto.getNovoEscalaoId() != null;
    boolean alterarCargo = dto.getNovoCargoId() != null;
    if (!alterarEscalao && !alterarCargo)
      throw IgrpResponseStatusException.badRequest("Indique o novo escalão e/ou o novo cargo.");
    // Sem seleção do form, cai no valor de domínio do fluxo (escalão → ESCALAO_NOVO; cargo-só → CARGO_NOVO).
    var tipoSituacao = org.springframework.util.StringUtils.hasText(dto.getTipoAlteracao())
        ? dto.getTipoAlteracao().trim() : (alterarEscalao ? TIPO_SIT_ESCALAO : TIPO_SIT_CARGO);

    // Não permitir novo movimento enquanto houver um pendente sobre o mesmo tiprel.
    if (tiposRelacionamentoEntityRepository.findFirstByTiprelId_IdAndEstado(atual.getId(), Estado.P).isPresent())
      throw IgrpResponseStatusException.conflict("Existe uma alteração de escalão/cargo por validar.");

    var dataEfetiva = dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now();

    // CARGO só (sem escalão) → aplica direto no tiprel atual, sem validação.
    if (alterarCargo && !alterarEscalao) {
      if (dto.getNovoCargoId() != null)
        atual.setCargoId(ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getNovoCargoId()));
      atual.setTipoSituacao(tipoSituacao);
      atual.setDataInicio(dataEfetiva);
      atual.setDataFim(dto.getDataFim());
      if (dto.getObservacao() != null) atual.setObs(dto.getObservacao());
      tiposRelacionamentoEntityRepository.save(atual);
      return new SuccessResponseDTO(true, atual.getUuid().toString(), "Cargo alterado.", List.of());
    }

    // ESCALÃO (com ou sem cargo) → cria tiprel pendente para validação.
    var novoEscalao = dto.getNovoEscalaoId() != null
        ? entityManager.find(ParamEscalaoEntity.class, dto.getNovoEscalaoId()) : null;
    if (novoEscalao == null)
      throw IgrpResponseStatusException.badRequest("Novo escalão inválido.");
    var novoCargo = dto.getNovoCargoId() != null
        ? ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getNovoCargoId()) : null;

    // CORREÇÃO reenviada pelo MAKER: se houver um movimento em C derivado do atual, reabre-se (C→P) e
    // reaplicam-se os campos, em vez de criar um pendente novo. Ciclo maker-checker (como a carreira).
    var emCorrecao = tiposRelacionamentoEntityRepository
        .findFirstByTiprelId_IdAndEstado(atual.getId(), Estado.C).orElse(null);
    if (emCorrecao != null) {
      emCorrecao.setEscalaoId(novoEscalao);
      if (novoCargo != null) emCorrecao.setCargoId(novoCargo);
      if (novoEscalao.getValor() != null) emCorrecao.setSalario(novoEscalao.getValor());
      emCorrecao.setDataInicio(dataEfetiva);
      emCorrecao.setDataFim(dto.getDataFim());
      emCorrecao.setTipoSituacao(tipoSituacao);
      if (dto.getObservacao() != null) emCorrecao.setObs(dto.getObservacao());
      emCorrecao.setEstado(Estado.P);
      var validacaoC = funcionarioRules.reabrirParaValidacao(emCorrecao.getUuid(), Referencia.ALTERACAO_ESCALAO);
      try {
        ValidacaoAuditContext.set(validacaoC.getId(), validacaoC.getUuid(), "RH_T_TIPOS_RELACIONAMENTO");
        tiposRelacionamentoEntityRepository.save(emCorrecao);
      } finally {
        ValidacaoAuditContext.clear();
      }
      validacaoC.setTiprelId(emCorrecao);
      validacaoEntityRepository.save(validacaoC);
      // Detalhe de alterações: regrava do zero (predecessor → movimento corrigido) via javers.compare.
      escalaoDetalheDiffWriter.limpar(validacaoC.getUuid());
      escalaoDetalheDiffWriter.persistir(validacaoC, emCorrecao.getTiprelId(), emCorrecao);
      return new SuccessResponseDTO(true, emCorrecao.getUuid().toString(),
          "Correção reenviada para validação.", List.of());
    }

    // Pré-gera o UUID da validação para carimbar JÁ o save do tiprel — é esse save que cria o snapshot
    // JaVers da grelha "Detalhe de alterações". Carimbar um save posterior seria no-op.
    var validacaoUuid = UuidCreator.getTimeOrderedEpoch();

    var novoTiprel = contratuaisEntityMapper.clone(atual);
    novoTiprel.setTiprelId(atual);
    novoTiprel.setCarreiraId(null);
    novoTiprel.setEscalaoId(novoEscalao);
    if (novoCargo != null) novoTiprel.setCargoId(novoCargo);
    if (novoEscalao.getValor() != null) novoTiprel.setSalario(novoEscalao.getValor());
    novoTiprel.setEstado(Estado.P);
    novoTiprel.setEstActAdm(0);
    novoTiprel.setDataInicio(dataEfetiva);
    novoTiprel.setDataFim(dto.getDataFim());
    novoTiprel.setTipoSituacao(tipoSituacao);
    novoTiprel.setObs(dto.getObservacao() != null ? dto.getObservacao() : tipoSituacao);
    novoTiprel.setReferente(Referencia.ALTERACAO_ESCALAO.name());
    try {
      ValidacaoAuditContext.set(null, validacaoUuid, "RH_T_TIPOS_RELACIONAMENTO");
      tiposRelacionamentoEntityRepository.save(novoTiprel);
    } finally {
      ValidacaoAuditContext.clear();
    }

    var validacao = new ValidacaoEntity();
    validacao.setTipoAccao(TipoAcao.UPDATE.name());
    validacao.setReferenciaName(Referencia.ALTERACAO_ESCALAO.name());
    validacao.setReferenciaId(novoTiprel.getId());
    validacao.setReferenciaUuid(novoTiprel.getUuid());
    validacao.setTiprelId(novoTiprel);
    validacao.setEstado(Estado.P);
    validacao.setUuid(validacaoUuid); // mesmo UUID já carimbado no baseline
    validacao.setFunId(funcionario);
    validacaoEntityRepository.save(validacao);

    // Detalhe de alterações persistido no momento (predecessor → novo tiprel) com o motor de diff do
    // JaVers (javers.compare de dois snapshots) → uma linha por campo em RH_T_VALIDACAO_DETALHE.
    escalaoDetalheDiffWriter.persistir(validacao, atual, novoTiprel);

    return new SuccessResponseDTO(true, novoTiprel.getUuid().toString(),
        "Alteração de escalão registada para validação.", List.of());
  }

  /** Valida o movimento pendente: SIM consolida (fecha vencimento antigo + abre novo + reassocia). */
  public SuccessResponseDTO validar(String funcionarioId, String tiprelUuid, AlterarEscalaoCargoDTO dto) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var pendente = tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(tiprelUuid));
    if (!Objects.equals(pendente.getFunId() != null ? pendente.getFunId().getId() : null, funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Movimento não pertence a este colaborador.");
    if (pendente.getEstado() != Estado.P)
      throw IgrpResponseStatusException.badRequest("Este movimento não está pendente de validação.");

    var validacao = funcionarioRules
        .getValidacaoPendenteByReferenciaUuid(pendente.getUuid(), TipoAcao.UPDATE, Referencia.ALTERACAO_ESCALAO)
        .orElse(null);
    var validar = dto != null ? dto.getValidar() : null;

    if (EstadoValidacao.CORRIGIR.equals(validar)) {
      var vC = funcionarioRules.devolverParaCorrecao(pendente.getUuid(), pendente.getEstado(), Referencia.ALTERACAO_ESCALAO);
      pendente.setEstado(Estado.C);
      tiposRelacionamentoEntityRepository.save(pendente);
      if (vC != null) validacaoEntityRepository.save(vC);
      return new SuccessResponseDTO(true, pendente.getUuid().toString(), "Devolvido para correção.", List.of());
    }

    boolean aprovado = EstadoValidacao.SIM.equals(validar);
    if (!aprovado) {
      pendente.setEstado(Estado.I);
      pendente.setObs("Não Validado");
      tiposRelacionamentoEntityRepository.save(pendente);
      if (validacao != null) { validacao.setEstado(Estado.I); validacaoEntityRepository.save(validacao); }
      return new SuccessResponseDTO(true, pendente.getUuid().toString(), "Alteração rejeitada.", List.of());
    }

    // === Aprovado: consolidar. O Java escreve o dinheiro (vínculo sem carreira → proc não se aplica). ===
    var atual = pendente.getTiprelId(); // o tiprel anterior (o que estava ativo)
    var dataEfetiva = pendente.getDataInicio() != null ? pendente.getDataInicio() : LocalDate.now();

    if (atual != null) {
      // 1. Fecha o vencimento antigo (REM base do vínculo) por DATA_FIM, mantendo 'A' (o 'I' é só rejeição).
      Long salarioTmId = salarioTmIdDoContrato(atual.getContrVinculoId());
      var fimAntigo = dataEfetiva.minusDays(1);
      var salariosFechados = new HashSet<Long>();
      funcionarioRules.getRemuneracoesAssociadosAtivos(atual.getId()).stream()
          .filter(r -> salarioTmId != null && r.getTmId() != null && Objects.equals(r.getTmId().getId(), salarioTmId))
          .forEach(o -> { o.setDataFim(fimAntigo); definicaoRemuneracaoEntityRepository.save(o); salariosFechados.add(o.getId()); });

      // 2. Cria o novo vencimento (valor do novo escalão), associado ao novo tiprel.
      var novasRem = new ArrayList<DefinicaoRemuneracaoEntity>();
      var novoEscalao = pendente.getEscalaoId();
      if (novoEscalao != null && novoEscalao.getValor() != null) {
        var salarioNovo = new DefinicaoRemuneracaoEntity();
        salarioNovo.setValor(novoEscalao.getValor());
        salarioNovo.setEstado(Estado.A);
        salarioNovo.setObs(TIPO_SIT_ESCALAO);
        // Doc (2.2.1): ao alterar escalão, o novo RH_T_DEF_REMUNERACOES herda DATA_INICIO e DATA_FIM do
        // formulário (as mesmas gravadas no tiprel pendente).
        salarioNovo.setDataInicio(dataEfetiva);
        salarioNovo.setDataFim(pendente.getDataFim());
        salarioNovo.setMoeda(pendente.getMoeda());
        salarioNovo.setFunId(funcionario);
        salarioNovo.setUuid(UuidCreator.getTimeOrderedEpoch());
        if (salarioTmId != null)
          salarioNovo.setTmId(entityManager.find(TipoMovimentoEntity.class, salarioTmId));
        definicaoRemuneracaoEntityRepository.save(salarioNovo);
        novasRem.add(salarioNovo);
      }

      // 3. Re-associa os restantes ativos (subsídios/descontos) ao novo tiprel, excluindo o salário fechado.
      tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(atual, pendente,
          novasRem, new ArrayList<DefPagamentoEntity>(), salariosFechados, Collections.emptySet());

      // 4. Fecha o tiprel antigo.
      atual.setDataFim(dataEfetiva);
      atual.setEstActAdm(0);
      atual.setFlgProcessa(0);
      atual.setEstado(Estado.I);
      tiposRelacionamentoEntityRepository.save(atual);
    }

    // 5. Ativa o novo tiprel.
    pendente.setEstActAdm(1);
    pendente.setFlgProcessa(1);
    pendente.setEstado(Estado.A);
    tiposRelacionamentoEntityRepository.save(pendente);

    if (validacao != null) { validacao.setEstado(Estado.A); validacaoEntityRepository.save(validacao); }
    return new SuccessResponseDTO(true, pendente.getUuid().toString(), "Alteração de escalão validada.", List.of());
  }

  /** Guard: só se aplica a vínculos com salário do PCCS (SIM_PCCS) e SEM carreira. */
  private void garantirPccsSemCarreira(TiposRelacionamentoEntity atual) {
    if (atual.getCarreiraId() != null)
      throw IgrpResponseStatusException.badRequest(
          "Alterar Escalão/Cargo só se aplica a vínculos sem carreira. Para carreira use Progressão/Promoção.");
    var vinculo = atual.getContrVinculoId() != null ? atual.getContrVinculoId().getVinculoId() : null;
    if (vinculo == null || !TipoSalarioVinculo.ehPccs(vinculo.getFlgSalario()))
      throw IgrpResponseStatusException.badRequest(
          "Alterar Escalão/Cargo só se aplica a vínculos com salário do PCCS (SIM_PCCS).");
  }

  /** Tm do movimento de SALÁRIO (REM) do vínculo do contrato — identifica o vencimento a fechar/criar. */
  private Long salarioTmIdDoContrato(ContratoEntity contrato) {
    Long vinculoId = contrato != null && contrato.getVinculoId() != null ? contrato.getVinculoId().getId() : null;
    if (vinculoId == null) return null;
    var movREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, "REM", Estado.A).stream().findFirst().orElse(null);
    return movREM != null && movREM.getTmId() != null ? movREM.getTmId().getId() : null;
  }
}
