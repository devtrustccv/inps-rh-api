package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.MarcarDispensaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarDispensaCommand;
import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class DispensaWriteService {

  private final DispensaEntityRepository dispensaRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

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

    var funcionario = funcionarioRepository.findByIdOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("DISPENSA");
    pedido.setOrigem("ASSIDUIDADE");
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P);
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
    if (!StringUtils.hasText(command.getDispensaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da dispensa é obrigatório");

    Long dispensaId;
    try {
      dispensaId = Long.parseLong(command.getDispensaId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador da dispensa inválido");
    }

    var dispensa = dispensaRepository.findByIdOrThrow(dispensaId);
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

    pedido.setEstado(estado);
    pedidoRepository.save(pedido);


    funcionarioRules.getValidacaoPendente(funcionario.getUuid(),
        TipoAcao.INSERT, Referencia.DISPENSA).ifPresent(validacaoEntityRepository::save);


    Map<String, Object> resp = new HashMap<>();
    resp.put("id", dispensa.getId());
    resp.put("estado", dispensa.getEstado().name());
    return resp;
  }

}
