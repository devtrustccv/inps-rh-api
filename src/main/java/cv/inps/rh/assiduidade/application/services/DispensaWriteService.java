package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.UpdateDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarDispensaCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("DISPENSA");
    pedido.setOrigem("ASSIDUIDADE");
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
    disp.setHoraInicio(req.getHoraSaida());
    disp.setHoraFim(req.getHoraEntrada());
    disp.setEstado(Estado.P);
    disp.setUuid(UuidCreator.getTimeOrderedEpoch());
    disp = dispensaRepository.save(disp);

    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
      java.util.List<DocumentoEntity> documentos = new java.util.ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            Estado.P,
            Referencia.DISPENSA.name(),
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
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    PedidoEntity finalPedido = pedido;

    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(finalPedido.getId());
      v.setReferenciaUuid(finalPedido.getUuid());
      validacaoEntityRepository.save(v);
    });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", disp.getId());
    resp.put("uuid", disp.getUuid());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarDispensa(ValidarDispensaCommand command) {
    var req = command.getDispensareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador da pedido é obrigatório");

    var dispensa = dispensaRepository.findByPedidoId_Uuid(UUID.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Dispensa nao encontrada" +
            "para esse pedido",
            command.getPedidoId()));

    var pedido = dispensa.getPedidoId();

    var funcionario = pedido != null ? pedido.getFunId() : null;
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    dispensa.setDecisaoResponsavel(req.getParecerResponsavel());
    dispensa.setObsResponsavel(req.getObservacaoResponsavel());
    dispensa.setObsRh(req.getObservacaoRh());
    dispensa.setEstado(estado);
    dispensa.setTiprelId(tipoRelAtual);
    if (req.getDataDispensa() != null) {
      dispensa.setData(req.getDataDispensa());
    }
    if (StringUtils.hasText(req.getHoraSaida())) {
      dispensa.setHoraInicio(req.getHoraSaida());
    }
    if (StringUtils.hasText(req.getHoraEntrada())) {
      dispensa.setHoraFim(req.getHoraEntrada());
    }
    if (StringUtils.hasText(req.getMotivo())) {
      dispensa.setDescricaoMotivo(req.getMotivo());
    }
    dispensaRepository.save(dispensa);

    pedido.setEstado(estado.name());
    pedidoRepository.save(pedido);

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(),
        TipoAcao.INSERT, Referencia.DISPENSA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", dispensa.getId());
    resp.put("estado", dispensa.getEstado().name());
    return resp;
  }

  @Transactional
  public Map<String, ?> updateDispensa(UpdateDispensaCommand command) {
    var req = command.getDispensareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getDispensaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da Dispensa é obrigatório");

    var dispensa = dispensaRepository.findByUuid(UUID.fromString(command.getDispensaId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Dispensa nao encontrada",
            command.getDispensaId()));

    var pedido = dispensa.getPedidoId();

    var funcionario = pedido != null ? pedido.getFunId() : null;
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    dispensa.setDecisaoResponsavel(req.getParecerResponsavel());
    dispensa.setObsResponsavel(req.getObservacaoResponsavel());
    dispensa.setObsRh(req.getObservacaoRh());
    dispensa.setEstado(Estado.P);
    dispensa.setTiprelId(tipoRelAtual);
    if (req.getDataDispensa() != null) {
      dispensa.setData(req.getDataDispensa());
    }
    if (StringUtils.hasText(req.getHoraSaida())) {
      dispensa.setHoraInicio(req.getHoraSaida());
    }
    if (StringUtils.hasText(req.getHoraEntrada())) {
      dispensa.setHoraFim(req.getHoraEntrada());
    }
    if (StringUtils.hasText(req.getMotivo())) {
      dispensa.setDescricaoMotivo(req.getMotivo());
    }
    dispensaRepository.save(dispensa);

    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
      var funcionario = pedido.getFunId();
      java.util.List<DocumentoEntity> documentos = new java.util.ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            Estado.P,
            Referencia.DISPENSA.name(),
            dispensa.getId(),
            dispensa.getUuid(),
            1L,
            funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentos.add(doc);
      }
      documentoEntityRepository.saveAll(documentos);
    }

    pedido.setEstado(Estado.P.name());
    pedidoRepository.save(pedido);

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.DISPENSA.name(),
        Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(pedido.getId());
      v.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(v);
    });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", dispensa.getId());
    resp.put("estado", dispensa.getEstado().name());
    return resp;
  }

}
