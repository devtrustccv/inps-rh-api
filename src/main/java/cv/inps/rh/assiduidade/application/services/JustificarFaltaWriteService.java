package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaJustificadaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaWriteService {

  private final FaltaEntityRepository faltaRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;
  private final EntityManager entityManager;
  private final DocumentoMapper documentoMapper;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoRepository;
  private final DefPagamentoMapper defPagamentoMapper;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasRepository;
  private final AnoEntityRepository anoRepository;

  @Transactional
  public Map<String, ?> justificarFalta(JustificarFaltaCommand command) {

    // Validar funcionário
    UUID funcionarioUuid;
    try {
      funcionarioUuid = UUID.fromString(command.getFuncionarioId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("Funcionario UUID inválido");
    }

    var funcionario = funcionarioRepository.findByUuid(funcionarioUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Funcionário não encontrado"));

    var dto = command.getJustificarfalta();
    if (dto == null || dto.getItensFalta() == null || dto.getItensFalta().isEmpty())
      throw IgrpResponseStatusException.badRequest("Nenhuma falta informada para justificar");

    // Validar se existe pelo menos uma síntese selecionada
    boolean temSelecionado = dto.getItensFalta()
        .stream()
        .anyMatch(FaltaItemDTO::isSelecionar);

    if (!temSelecionado)
      throw IgrpResponseStatusException.badRequest(
          "Nenhuma falta marcada para justificação");

    // Buscar ParamSituação (obrigatório)
    var paramSituacao = paramSituacaoEntityRepository.findByIdOrThrow(dto.getTipoJustificacao());
    if (!"1".equalsIgnoreCase(paramSituacao.getTipoAusencia())) {
      throw IgrpResponseStatusException.badRequest("Tipo justificativo não permitido para falta");
    }

    // Criar pedido de justificação
    PedidoEntity pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("JUSTIFICACAO_FALTA");
    pedido.setOrigem("RH");
    pedido.setEstado(Estado.P.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    // Criar faltas a partir das sínteses diárias
    List<FaltaEntity> faltas = new ArrayList<>();
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionarioUuid);

    for (var item : dto.getItensFalta()) {

      if (!item.isSelecionar())
        continue;

      AssiduidadeSinteseDiarioEntity sintese;
      try {
        sintese = entityManager.getReference(
            AssiduidadeSinteseDiarioEntity.class,
            item.getId());
      } catch (EntityNotFoundException e) {
        throw IgrpResponseStatusException.badRequest(
            "Síntese diária inválida: " + item.getId());
      }

      // Regra crítica: não permitir falta duplicada para a mesma síntese
      if (faltaRepository.existsBySinteseDiarioId(sintese)) {
        throw IgrpResponseStatusException.badRequest(
            "Já existe uma falta associada à data " + sintese.getData());
      }

      FaltaEntity falta = new FaltaEntity();
      falta.setPedidoId(pedido);
      falta.setSinteseDiarioId(sintese);
      falta.setTiprelId(tipoRelAtual);

      falta.setDataInicio(LocalDateTime.of(
          sintese.getData(),
          LocalTime.MIN));
      falta.setDataFim(LocalDateTime.of(
          sintese.getData(),
          LocalTime.of(23, 59, 59)));

      falta.setHorasAusencia(sintese.getHorasAusencia());
      falta.setValor(
          item.getValorAusencia() != null
              ? BigDecimal.valueOf(item.getValorAusencia())
              : null);

      falta.setDescricaoMotivo(item.getMotivo());
      falta.setFlgJustificativo("SIM");

      falta.setDecisaoResponsavel(dto.getParecerResponsavel());
      falta.setObsResponsavel(dto.getObsResponsavel());
      falta.setDespachoRh(dto.getDespachoRh());

      falta.setParamSitId(paramSituacao);
      falta.setEstado(Estado.P);
      falta.setUuid(UuidCreator.getTimeOrderedEpoch());

      faltas.add(falta);
    }

    // 6 Persistir faltas
    faltaRepository.saveAll(faltas);

    Map<Long, FaltaEntity> faltaPorSinteseId = faltas.stream()
        .filter(f -> f.getSinteseDiarioId() != null)
        .collect(Collectors.toMap(f -> f.getSinteseDiarioId().getId(), Function.identity()));

    List<DocumentoEntity> documentos = new ArrayList<>();
    for (var item : dto.getItensFalta()) {
      if (!item.isSelecionar())
        continue;
      if (item.getDocumento() == null)
        continue;
      FaltaEntity faltaRef = faltaPorSinteseId.get(item.getId());
      if (faltaRef == null)
        continue;

      var doc = documentoMapper.toEntity(
          item.getDocumento(),
          Estado.P,
          Referencia.JUSTIFICAR_FALTA.name(),
          faltaRef.getId(),
          faltaRef.getUuid(),
          1L,
          funcionario);
      doc.setUuid(UuidCreator.getTimeOrderedEpoch());
      documentos.add(doc);
    }
    if (!documentos.isEmpty()) {
      documentoEntityRepository.saveAll(documentos);
    }

    // Criar validação
    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.JUSTIFICAR_FALTA.name(),
        Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());

    validacaoEntityRepository.save(validacao);

    // 8️ Retorno
    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado());
  }

  @Transactional
  public Map<String, ?> validarFaltaJustificada(ValidarFaltaJustificadaCommand command) {

    var pedidoUuid = UUID.fromString(command.getPedidoId());

    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Pedido de justificação de falta não encontrado"));

    var funcionario = pedido.getFunId();

    var dto = command.getJustificarfalta();
    if (dto == null || dto.getItensFalta() == null || dto.getItensFalta().isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Nenhuma falta selecionada para validação");
    }
    // Parametrização da justificação
    var paramSituacao = paramSituacaoEntityRepository.findByIdOrThrow(dto.getTipoJustificacao());

    // Todas as faltas do pedido (já criadas na fase de justificar)
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);
    Map<Long, FaltaEntity> faltaPorSinteseId = faltas.stream()
        .filter(f -> f.getSinteseDiarioId() != null)
        .collect(Collectors.toMap(
            f -> f.getSinteseDiarioId().getId(),
            Function.identity()));

    if (faltas.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "Não existem faltas associadas a este pedido");
    }

    // Estado final
    final Estado estadoFinal = dto.getValidar() == EstadoValidacao.SIM ? Estado.A : Estado.I;

    // Atualizar apenas as faltas correspondentes às sínteses selecionadas
    for (var item : dto.getItensFalta()) {

      if (!item.isSelecionar())
        continue;

      FaltaEntity falta = faltaPorSinteseId.get(item.getId());

      if (falta == null) {
        throw IgrpResponseStatusException.badRequest(
            "Falta não encontrada para a síntese diária ID: " + item.getId());
      }

      falta.setDescricaoMotivo(item.getMotivo());
      falta.setObsResponsavel(dto.getObsResponsavel());
      falta.setDespachoRh(dto.getDespachoRh());
      falta.setParamSitId(paramSituacao);
      falta.setEstado(estadoFinal);

      if (estadoFinal == Estado.A &&
          falta.getParamSitId() != null &&
          falta.getParamSitId().getFlgFaltaDecontoSal() != null &&
          falta.getParamSitId().getFlgFaltaDecontoSal() == 1) {

        var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
        var vinculoId = tipoRel.getContrVinculoId().getVinculoId().getId();

        var mov = paramVinculoMovimentoEntityRepository
            .findByVinculoId_IdAndTipo(vinculoId, "PAG_FALTA")
            .getFirst();

        var valor = falta.getValor() != null ? falta.getValor() : BigDecimal.ZERO;
        var dr = definicaoRemuneracaoMapper.createRenumeracao(
            valor,
            mov.getTmId(),
            falta.getDataInicio().toLocalDate(),
            falta.getDataFim().toLocalDate(),
            funcionario,
            "CVE");

        definicaoRemuneracaoEntityRepository.save(dr);
        falta.setFlgDescontoSal(1);
        falta.setDefRemId(dr);
      }

      // REGRA: Desconto de Férias
      if (Objects.equals(estadoFinal, Estado.A) &&  falta.getParamSitId() != null &&
          Objects.equals(falta.getParamSitId().getFlgAusencia(), 1) &&
          Objects.equals(falta.getParamSitId().getTipoAusencia(), "FERIAS"))  {

        // TODO: Implementar validação de saldo de férias do funcionário

        var ano = anoRepository.findByAno(String.valueOf(falta.getDataInicio().getYear()))
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("Ano de referência não encontrado"));

        var feriasGozadas = new FeriasGozadasEntity();
        feriasGozadas.setFunId(funcionario);
        feriasGozadas.setPedidoId(pedido);
        feriasGozadas.setAnoId(ano);
        feriasGozadas.setDataInicio(falta.getDataInicio().toLocalDate());
        feriasGozadas.setDataFim(falta.getDataFim().toLocalDate());
        feriasGozadas.setNumDia(1); // Assumindo 1 dia de desconto por falta
        feriasGozadas.setEstado(Estado.A);
        feriasGozadas.setUuid(UuidCreator.getTimeOrderedEpoch());
        feriasGozadasRepository.save(feriasGozadas);
      }
    }

    faltaRepository.saveAll(faltas);

    // Atualizar pedido
    pedido.setEstado(estadoFinal.name());
    pedido.setEtapa("FINALIZADO");
    pedidoRepository.save(pedido);

    // Atualizar validação pendente
    funcionarioRules.getValidacaoPendente(
        funcionario.getUuid(),
        TipoAcao.INSERT,
        Referencia.JUSTIFICAR_FALTA)
        .ifPresent(v -> {
          v.setEstado(estadoFinal);
          validacaoEntityRepository.save(v);
        });

    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado());

    /*
     * ================= DÚVIDAS / PONTOS A CONFIRMAR COM ANALISTA =================
     *
     * 1️ Desconto de Salário (RH_T_DEF_PAGAMENTOS)
     * - Qual entidade exata devemos usar para registrar o desconto de salário?
     * - Quais campos são obrigatórios: funId, tiprelId, referenciaId, valor, data,
     * estado?
     * - O desconto é automático ao validar a falta ou apenas registro histórico?
     *
     * 2️ Desconto de Férias (RH_T_FERIAS_GOZADAS)
     * - Existe entidade mapeada para registrar férias gozadas?
     * - Como calcular os dias a descontar por falta?
     * - Só desconta se houver saldo suficiente de férias?
     * - As datas da falta (dataInicio/dataFim) devem ser replicadas no registro de
     * férias?
     *
     * 3️ Desconto de Horas de Dispensa (RH_T_DISPENSA)
     * - Existe entidade mapeada para horas de dispensa?
     * - Como calcular a quantidade de horas a descontar por falta?
     * - Aplica apenas a faltas injustificadas ou todas as faltas?
     *
     * 4️ Valor da Justificação de Falta
     * - Para cada tipoJustificacao (ParamSituacaoEntity.tipoFalta), como calcular o
     * valor?
     * - Valor por hora ou por dia?
     * - É apenas para descontos ou também para relatórios?
     *
     * 5️ Integração com Pedido e Validação
     * - Ao validar a falta, devemos atualizar estados:
     * FaltaEntity.estado = 'A'
     * PedidoEntity.estado = 'A', PedidoEntity.etapa = 'FINALIZADO'
     * ValidacaoEntity.estado = 'A'
     * - Isso deve ocorrer somente se EstadoValidacao enviado for "SIM"?
     *
     * 6️ Observações Gerais
     * - O campo tipoJustificacao já vem no DTO como Long (id de
     * ParamSituacaoEntity), está correto?
     * - Se uma falta já tiver desconto registrado, sobrescrever ou criar novo
     * registro?
     * - Como tratar múltiplas faltas do mesmo colaborador no mesmo mês: justificar
     * todas juntas ou individualmente?
     * - Confirmação do cálculo de horas trabalhadas e horas de ausência para
     * atualização na FaltaEntity.
     *
     * =============================================================================
     */
  }

}
