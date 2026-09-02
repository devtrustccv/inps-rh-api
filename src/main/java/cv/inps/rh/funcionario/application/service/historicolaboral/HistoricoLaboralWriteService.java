package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.commands.AtualizarRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.commands.NovaRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralReqDTO;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByTiprelUuidQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoLaboralWriteService {

  @PersistenceContext
  private EntityManager entityManager;

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;
  private final HistoricoLaboralReadService historicoLaboralReadService;

  @Transactional
  public SuccessResponseDTO novo(NovaRelacaoLaboralCommand command) {

    // Registo de Relacao Laboral (use case): so a SITUACAO e editavel — mobilidade/carreira sao
    // SOMENTE LEITURA (tem ecras proprios) e este fluxo NAO passa por validacao (aplica direto).
    var dto = command.getRelacaolaboral();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // "Processado" (EXISTS em RH_T_PROC_FUNCIONARIOS por TIPREL_ID): nao processado -> UPDATE
    // in-place da situacao; processado -> novo registo/tiprel (preserva o historico salarial).
    boolean processado = processamentoFuncionarioRepository.existsByTiprel_Id(atual.getId());

    if (!processado) {
      if (temSituacao(dto)) {
        var sitLab = atual.getSituacLaboralId();
        if (sitLab == null) {
          sitLab = new SituacaoLaboralEntity();
          sitLab.setUuid(IdentificadorUnico.create().valor());
          sitLab.setContrVinculoId(atual.getContrVinculoId());
          atual.setSituacLaboralId(sitLab);
        }
        populateSituacao(sitLab, dto);
        sitLab.setEstado(Estado.A);
        situacaoLaboralEntityRepository.save(sitLab);
        derivarFlgProcessa(atual, sitLab);
        // In-place (nao processado): mantem-se o TIPO_SITUACAO/OBS/REFERENTE do tiprel (ex.: "INICIO").
        // So o ramo processado (tiprel novo) carimba "MUDANCA_SITUACAO_LABORAL" — como AlterarSituacaoLaboral.
        funcionarioEntityRepository.saveAndFlush(funcionario);
        ordemServicoWriteService.criar(funcionario, atual, dto.getTipoOrdemServico());
      }
      funcionarioEntityRepository.save(funcionario);
      return new SuccessResponseDTO(true, atual.getUuid().toString(), "Relação laboral registada.", List.of());
    }

    var hoje = LocalDate.now();
    atual.setEstActAdm(0);
    // Regra do analista: o relacionamento fechado termina em inicio-1 (o novo abre em `hoje`), para os
    // períodos ficarem contíguos sem sobreposição de 1 dia.
    atual.setDataFim(hoje.minusDays(1));

    var novoRelacionamento = dadosContratuaisMapper.clone(atual);
    // Registo de Relação Laboral aplica-se DIRETAMENTE (use case não prevê validação).
    novoRelacionamento.setEstado(Estado.A);
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setTiprelId(atual);
    // Mudanca de situacao laboral (unico campo editavel): REFERENTE = 'SITUACAO_LABORAL' (doc l.2030).
    novoRelacionamento.setReferente("SITUACAO_LABORAL");
    // Use case (RH_T_TIPOS_RELACIONAMENTO): novo registo DATA_INICIO = data do registo, DATA_FIM = nulo.
    // O clone copia as datas do anterior; repõe-se aqui.
    novoRelacionamento.setDataInicio(hoje);
    novoRelacionamento.setDataFim(null);

    novoRelacionamento.setUltProc(hoje);

    boolean alterou = false;
    if (temSituacao(dto)) {
      var novaSit = new SituacaoLaboralEntity();
      populateSituacao(novaSit, dto);
      novaSit.setEstado(Estado.A);
      novaSit.setUuid(IdentificadorUnico.create().valor());
      novaSit.setContrVinculoId(atual.getContrVinculoId());
      situacaoLaboralEntityRepository.save(novaSit);
      novoRelacionamento.setSituacLaboralId(novaSit);
      derivarFlgProcessa(novoRelacionamento, novaSit);
      // TIPO_SITUACAO do novo tiprel = tipo do movimento (doc DOSSIÊ l.2031 / AlterarSituacaoLaboral).
      novoRelacionamento.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
      // OBS tambem vai para o tiprel (doc mapeamento l.1980 / AlterarSituacaoLaboral l.186).
      if (dto.getObservacao() != null)
        novoRelacionamento.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
      alterou = true;
    }

    funcionario.getTiposrelacionamentos().add(novoRelacionamento);
    funcionarioEntityRepository.saveAndFlush(funcionario);

    // Copia as rem/pag do tiprel anterior para o novo (a situacao nao mexe no salario/rem).
    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(atual, novoRelacionamento, java.util.List.of(), Collections.emptyList());

    // Sem validacao (use case): o novo tiprel fica ativo de imediato. OS opcional.
    if (alterou) {
      ordemServicoWriteService.criar(funcionario, novoRelacionamento, dto.getTipoOrdemServico());
    }

    funcionarioEntityRepository.save(funcionario);
    return new SuccessResponseDTO(true, novoRelacionamento.getUuid().toString(), "Relação laboral registada.", List.of());
  }

  @Transactional
  public SuccessResponseDTO atualizar(AtualizarRelacaoLaboralCommand command) {
    var dto = command.getRelacaolaboral();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    // A relação laboral é identificada pelo UUID do TIPO DE RELACIONAMENTO (tiprel),
    // exposto na lista como `tiprelUuid` — e não pelo UUID da carreira (a carreira é
    // somente leitura). O path {carreiraId} transporta agora o tiprelUuid.
    var relacionamento = tiposRelacionamentoEntityRepository
        .findByUuid(UUID.fromString(command.getTiprelUuid()))
        .orElse(null);
    if (relacionamento == null) {
      throw IgrpResponseStatusException.notFound("Histórico Laboral não encontrado");
    }

    if (relacionamento.getFunId() == null || !relacionamento.getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Histórico laboral não pertence ao funcionário");

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(relacionamento.getEstado());

    // A edição da relação laboral só é permitida se ainda NÃO houver processamento salarial.
    // Deteção feita como na vista (RH_V_CONTRATO/RH_V_RELACAO_LABORAL): EXISTS em
    // RH_T_PROC_FUNCIONARIOS por TIPREL_ID. Se já processado, bloquear — alterar exigiria
    // novo registo/validação, não uma simples edição in-place.
    if (processamentoFuncionarioRepository.existsByTiprel_Id(relacionamento.getId()))
      throw IgrpResponseStatusException.badRequest(
          "Não é possível editar a relação laboral: já tem processamento salarial.");

    // Mobilidade e Carreira sao SOMENTE LEITURA na relacao laboral — so se atualiza a SITUACAO,
    // in-place e sem alterar o ESTADO do registo (nao passa por validacao — use case).
    if (temSituacao(dto)) {
      var sitLab = relacionamento.getSituacLaboralId();
      if (sitLab == null) {
        sitLab = new SituacaoLaboralEntity();
        sitLab.setEstado(Estado.A);
        sitLab.setUuid(IdentificadorUnico.create().valor());
        sitLab.setContrVinculoId(relacionamento.getContrVinculoId());
      }
      populateSituacao(sitLab, dto);
      situacaoLaboralEntityRepository.save(sitLab);
      relacionamento.setSituacLaboralId(sitLab);
      // In-place: nao se recarimba TIPO_SITUACAO/OBS/REFERENTE do tiprel (mantem os originais).
      // FLG_PROCESSA reflete a nova situacao (RH_T_PARAM_SITUACAO.FLG_REMUNERACAO) — como no ramo nao-processado.
      derivarFlgProcessa(relacionamento, sitLab);
    }

    tiposRelacionamentoEntityRepository.save(relacionamento);
    funcionarioEntityRepository.save(funcionario);
    return new SuccessResponseDTO(true, relacionamento.getUuid().toString(), "Relação laboral actualizada.", List.of());
  }

  /** Ha alteracao de situacao a aplicar? (situacao/motivo/datas/observacao). */
  private boolean temSituacao(RelacaoLaboralReqDTO dto) {
    return dto != null && (dto.getSituacaoLaboral() != null || dto.getMotivo() != null
        || dto.getDataInicioSituacao() != null || dto.getDataFimSituacao() != null
        || dto.getObservacao() != null);
  }

  /** FLG_PROCESSA do tiprel deriva de RH_T_PARAM_SITUACAO.FLG_REMUNERACAO (use case). */
  private void derivarFlgProcessa(TiposRelacionamentoEntity tiprel, SituacaoLaboralEntity sitLab) {
    var paramSit = sitLab.getSituacaoLaboralId();
    if (paramSit != null)
      tiprel.setFlgProcessa(Integer.valueOf(1).equals(paramSit.getFlgRemuneracao()) ? 1 : 0);
  }

  /** Devolve o detalhe atualizado da relacao laboral (inclui mobilidade/carreira read-only). */
  private RelacaoLaboralDTO detalhe(TiposRelacionamentoEntity tiprel) {
    return historicoLaboralReadService.getRelacaoLaboralByTiprelUuid(
        new GetRelacaoLaboralByTiprelUuidQuery(tiprel.getUuid().toString()));
  }

  private void populateSituacao(SituacaoLaboralEntity sit, RelacaoLaboralReqDTO dto) {
    var sitRef = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class, dto.getSituacaoLaboral());
    if (sitRef != null) sit.setSituacaoLaboralId(sitRef);
    if (dto.getMotivo() != null) {
      try {
        var mid = Long.parseLong(dto.getMotivo());
        var motRef = ValidationUtil.ref(entityManager, ParamSituacaoDetalheEntity.class, mid);
        if (motRef != null) sit.setMotivoSitLabId(motRef);
      } catch (NumberFormatException ignored) {
      }
      sit.setTipoSituacao(ValidationUtil.trimToNull(dto.getMotivo()));
    }
    if (dto.getDataInicioSituacao() != null)
      sit.setDataInicio(dto.getDataInicioSituacao());
    if (dto.getDataFimSituacao() != null)
      sit.setDataFim(dto.getDataFimSituacao());
    if (dto.getObservacao() != null)
      sit.setObs(ValidationUtil.trimToNull(dto.getObservacao()));
  }


}
