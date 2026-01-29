package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaJustificadaCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JustificarFaltaWriteService {

  private final FaltaEntityRepository faltaRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  @Transactional
  public Map<String, ?> justificarFalta(JustificarFaltaCommand command) {
    var req = command.getJustificarfalta();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de justificacao de falta ausentes");
    if (!StringUtils.hasText(command.getFaltaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da falta é obrigatório");

    Long faltaId;
    try {
      faltaId = Long.parseLong(command.getFaltaId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador da falta inválido");
    }

    FaltaEntity falta = faltaRepository.findByIdOrThrow(faltaId);
    PedidoEntity pedido = falta.getPedidoId();
    var funcionario = pedido != null ? pedido.getFunId() : null;
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    falta.setDecisaoResponsavel(req.getParecerResponsavel());
    falta.setObsResponsavel(req.getObsResponsavel());
    falta.setDespachoRh(req.getDespachoRh());
    falta.setFlgJustificativo("SIM");
    faltaRepository.save(falta);

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.FALTA.name(), Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(pedido != null ? pedido.getId() : null);
    validacaoEntityRepository.save(validacao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", falta.getId());
    resp.put("uuid", falta.getUuid() != null ? falta.getUuid().toString() : null);
    return resp;
  }

  @Transactional
  public Map<String, ?> validarFaltaJustificada(ValidarFaltaJustificadaCommand command) {
    var req = command.getJustificarfalta();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getFaltaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da falta é obrigatório");

    Long faltaId;
    try {
      faltaId = Long.parseLong(command.getFaltaId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador da falta inválido");
    }

    FaltaEntity falta = faltaRepository.findByIdOrThrow(faltaId);
    PedidoEntity pedido = falta.getPedidoId();
    var funcionario = pedido != null ? pedido.getFunId() : null;
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    falta.setDecisaoResponsavel(req.getParecerResponsavel());
    falta.setObsResponsavel(req.getObsResponsavel());
    falta.setDespachoRh(req.getDespachoRh());
    falta.setEstado(estado);
    faltaRepository.save(falta);

    if (pedido != null) {
      pedido.setEstado(estado);
      pedidoRepository.save(pedido);
    }

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", falta.getId());
    resp.put("uuid", falta.getUuid() != null ? falta.getUuid().toString() : null);
    resp.put("estado", falta.getEstado().name());
    return resp;
  }

}
