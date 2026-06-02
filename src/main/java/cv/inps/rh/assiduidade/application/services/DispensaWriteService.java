package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.UpdateDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarDispensaCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DispensaWriteService {

  private final DispensaEntityRepository dispensaRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final DocumentoMapper documentoMapper;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final AusenciaEntityRepository ausenciaEntityRepository;
  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;
  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final DispensaHorasService dispensaHorasService;
  private final OrdemServicoWriteService ordemServicoWriteService;



  @Transactional
  public Map<String, ?> marcarDispensa(MarcarDispensaCommand command) {
    var req = command.getDispensareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de dispensa ausentes");
    if (req.getColaborador() == null)
      throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
    if (req.getDataDispensa() == null)
      throw IgrpResponseStatusException.badRequest("Data da dispensa obrigatória");
    if (!StringUtils.hasText(req.getHoraSaida()) || !StringUtils.hasText(req.getHoraEntrada()))
      throw IgrpResponseStatusException.badRequest("Intervalo de horas obrigatório");

    var dispensaStatus = dispensaHorasService.getHorasStatus(req.getColaborador(), req.getDataDispensa());
    var horasDisponiveis = TimeUtils.hhmmToMinutes(dispensaStatus.getHorasDisponiveis());
    var horasUsadas= TimeUtils.hhmmToMinutes(dispensaStatus.getHorasUsadas());

    if (horasUsadas > horasDisponiveis) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionário não tem horas suficientes para dispensa"
      );
    }


    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    ResponsavelEntity responsavel = null;
    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
      () -> IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));

    }

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("DISPENSA");
    pedido.setOrigem("RH");
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    var disp = new DispensaEntity();
    disp.setPedidoId(pedido);
    disp.setTiprelId(tipoRelAtual);
    disp.setTipoDispensa(req.getTipoMotivo());
    disp.setDescricaoMotivo(req.getMotivo());
    disp.setData(req.getDataDispensa());
    disp.setHoraInicio(TimeUtils.hhmmToIntervalFormat(req.getHoraSaida()));
    disp.setHoraFim(TimeUtils.hhmmToIntervalFormat(req.getHoraEntrada()));
    disp.setTotalHora(TimeUtils.diffMinutes(
        TimeUtils.hhmmToIntervalFormat(req.getHoraSaida()),
        TimeUtils.hhmmToIntervalFormat(req.getHoraEntrada())));
    disp.setResponsavelId(responsavel != null ? responsavel.getId() : null);
    disp.setEstado(Estado.P);
    disp.setUuid(UuidCreator.getTimeOrderedEpoch());
    disp = dispensaRepository.save(disp);

    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
      java.util.List<DocumentoEntity> documentos = new java.util.ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            Estado.P,
            TableName.RH_T_DISPENSA.name(),
            disp.getId(),
            disp.getUuid(),
            1L,
            funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentos.add(doc);
      }
      documentoEntityRepository.saveAll(documentos);
    }

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.DISPENSA.name(),
        Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());
    validacaoEntityRepository.save(validacao);


    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid());
    resp.put("dispensaId", disp.getId());
    resp.put("dispensaUuid", disp.getUuid());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarDispensa(ValidarDispensaCommand command) {
    var req = command.getDispensareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");

    var dispensa = dispensaRepository.findByPedidoId_Uuid(UUID.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Dispensa não encontrada para esse pedido",
            command.getPedidoId()));

    var pedido = dispensa.getPedidoId();
    if (pedido == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var funcionario = pedido.getFunId();
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    // Usar funcionario do pedido (não do request — pode não vir preenchido na validação)
    var dispensaStatus = dispensaHorasService.getHorasStatus(funcionario.getUuid(), dispensa.getData());
    var horasDisponiveis = TimeUtils.hhmmToMinutes(dispensaStatus.getHorasDisponiveis());
    var horasUsadas= TimeUtils.hhmmToMinutes(dispensaStatus.getHorasUsadas());

    if (horasUsadas > horasDisponiveis) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionário não tem horas suficientes para dispensa"
      );
    }

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    ResponsavelEntity responsavel = null;

    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
          () -> IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));

    }

    // Atualizar campos da dispensa
    dispensa.setDecisaoResponsavel(req.getParecerResponsavel());
    dispensa.setObsResponsavel(req.getObservacaoResponsavel());
    dispensa.setObsRh(req.getObservacaoRh());
    dispensa.setDecisaoRh(req.getValidar()); // SIM ou NAO — spec: RH_T_DISPENSA.DECISAO_RH
    dispensa.setEstado(estado);
    dispensa.setResponsavelId(responsavel != null ? responsavel.getId() : null);
    dispensa.setTiprelId(tipoRelAtual);
    if (req.getDataDispensa() != null) dispensa.setData(req.getDataDispensa());
    if (StringUtils.hasText(req.getHoraSaida())) dispensa.setHoraInicio(TimeUtils.hhmmToIntervalFormat(req.getHoraSaida()));
    if (StringUtils.hasText(req.getHoraEntrada())) dispensa.setHoraFim(TimeUtils.hhmmToIntervalFormat(req.getHoraEntrada()));
    if (StringUtils.hasText(req.getMotivo())) dispensa.setDescricaoMotivo(req.getMotivo());

    dispensaRepository.save(dispensa);

    // Documentos
    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
      List<DocumentoEntity> documentos = new ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            estado,
            TableName.RH_T_DISPENSA.name(),
            dispensa.getId(),
            dispensa.getUuid(),
            1L,
            funcionario
        );
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentos.add(doc);
      }
      documentoEntityRepository.saveAll(documentos);
    }

    // Propagar estado para anexos existentes da dispensa
    var anexosExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_DISPENSA.name(), dispensa.getUuid());
    if (anexosExistentes != null && !anexosExistentes.isEmpty()) {
      anexosExistentes.forEach(a -> a.setEstado(estado));
      documentoEntityRepository.saveAll(anexosExistentes);
    }

    // Criar ausência quando validação for SIM (estado A)
    if (estado == Estado.A) {
      ordemServicoWriteService.criar(funcionario, tipoRelAtual, req.getTipoOrdemServico());
      var paramsDispensa = paramSituacaoEntityRepository.findByFlgAusenciaAndTipoAusencia(1, "DISPENSA");
      if (paramsDispensa != null && !paramsDispensa.isEmpty()) {
        ParamSituacaoEntity param = paramsDispensa.getFirst();
        var ausencia = new AusenciaEntity();
        ausencia.setFunId(funcionario);
        ausencia.setParamSitId(param);
        ausencia.setReferenciaName(TableName.RH_T_DISPENSA.name());
        ausencia.setReferenciaId(dispensa.getId());
        ausencia.setObs(dispensa.getTipoDispensa());
        ausencia.setDataInicio(dispensa.getData());
        ausencia.setDataFim(dispensa.getData());
        var minutos = TimeUtils.diffMinutes(dispensa.getHoraInicio(), dispensa.getHoraFim());
        ausencia.setHora(minutos);
        ausencia.setEstado(Estado.A);
        ausencia.setUuid(UuidCreator.getTimeOrderedEpoch());
        ausenciaEntityRepository.save(ausencia);
      }
    }

    // Atualizar estado do pedido
    pedido.setEstado(estado.name());
    pedidoRepository.save(pedido);

    // Atualizar validação pendente
    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.DISPENSA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", dispensa.getId());
    resp.put("estado", dispensa.getEstado().name()); // mantido .name() se Estado for enum
    return resp;
  }


  @Transactional
  public Map<String, ?> updateDispensa(UpdateDispensaCommand command) {

    var req = command.getDispensareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de dispensa ausentes");
    if (!StringUtils.hasText(command.getDispensaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da Dispensa é obrigatório");

    var dispensa = dispensaRepository.findByUuid(UUID.fromString(command.getDispensaId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Dispensa nao encontrada",
            command.getDispensaId()));

    var dispensaStatus = dispensaHorasService.getHorasStatus(req.getColaborador(), dispensa.getData());
    var horasDisponiveis = TimeUtils.hhmmToMinutes(dispensaStatus.getHorasDisponiveis());
    var horasUsadas= TimeUtils.hhmmToMinutes(dispensaStatus.getHorasUsadas());

    if (horasUsadas > horasDisponiveis) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionário não tem horas suficientes para dispensa"
      );
    }

    var pedido = dispensa.getPedidoId();
    if (pedido == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var funcionario = pedido.getFunId();
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    ResponsavelEntity responsavel = null;
    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
          () -> IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));

    }
    // Atualizar campos da dispensa
    dispensa.setDecisaoResponsavel(req.getParecerResponsavel());
    dispensa.setObsResponsavel(req.getObservacaoResponsavel());
    dispensa.setObsRh(req.getObservacaoRh());
    dispensa.setResponsavelId(responsavel != null ? responsavel.getId() : null);
    dispensa.setEstado(Estado.P);
    dispensa.setTiprelId(tipoRelAtual);
    if (req.getDataDispensa() != null) dispensa.setData(req.getDataDispensa());
    if (StringUtils.hasText(req.getHoraSaida())) dispensa.setHoraInicio(TimeUtils.hhmmToIntervalFormat(req.getHoraSaida()));
    if (StringUtils.hasText(req.getHoraEntrada())) dispensa.setHoraFim(TimeUtils.hhmmToIntervalFormat(req.getHoraEntrada()));
    if (StringUtils.hasText(req.getMotivo())) dispensa.setDescricaoMotivo(req.getMotivo());
    // Recalcular TOTAL_HORA se horas foram alteradas
    if (dispensa.getHoraInicio() != null && dispensa.getHoraFim() != null)
      dispensa.setTotalHora(TimeUtils.diffMinutes(dispensa.getHoraInicio(), dispensa.getHoraFim()));

    dispensaRepository.save(dispensa);

    // Documentos - substituição/sincronização
    var anexosExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_DISPENSA.name(), dispensa.getUuid());
    var sincronizados = documentoMapper.syncDocumentos(
        anexosExistentes != null ? anexosExistentes : new ArrayList<>(),
        req.getDocumentos(),
        TableName.RH_T_DISPENSA.name(),
        dispensa.getId(),
        dispensa.getUuid(),
        1L,
        funcionario);
    if (sincronizados != null && !sincronizados.isEmpty()) {
      sincronizados.forEach(d -> { if (d.getUuid() == null) d.setUuid(UuidCreator.getTimeOrderedEpoch()); });
      documentoEntityRepository.saveAll(sincronizados);
    }

    // Atualizar pedido
    pedido.setEstado(Estado.P.name());
    pedidoRepository.save(pedido);

    // Registo de UPDATE — editar cria UPDATE (não um novo INSERT) para não duplicar a validação pendente
    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.UPDATE.name(),
        Referencia.DISPENSA.name(),
        Estado.P
    );
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());
    validacaoEntityRepository.save(validacao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", dispensa.getId());
    resp.put("estado", dispensa.getEstado()); // sem .name() se já for String
    return resp;
  }


}
