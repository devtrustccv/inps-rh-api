package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaJustificadaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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


  @Transactional
  public Map<String, ?> justificarFalta(JustificarFaltaCommand command) {

    // 1. Validar funcionário
    var funcionarioUuid = UUID.fromString(command.getFuncionarioId());

    var funcionario = funcionarioRepository.findByUuid(funcionarioUuid)
        .orElseThrow(() ->
            IgrpResponseStatusException.badRequest("Funcionário não encontrado"));

    var dto = command.getJustificarfalta();
    if (dto == null || dto.getItensFalta()== null || dto.getItensFalta().isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Nenhuma falta selecionada para justificar");
    }

    PedidoEntity pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("JUSTIFICACAO_FALTA");
    pedido.setOrigem("RH");
    pedido.setEstado(Estado.P);
    pedido = pedidoRepository.save(pedido);


    var paramSituacao = paramSituacaoEntityRepository.findByIdOrThrow(dto.getTipoJustificacao());

    // 3. Buscar faltas selecionadas
    var faltaIds = dto.getItensFalta().stream()
        .filter(FaltaItemDTO::isSelecionar)
        .map(FaltaItemDTO::getId)
        .toList();

    if (faltaIds.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Nenhuma falta marcada para justificação");
    }

    List<FaltaEntity> faltas = faltaRepository.findAllById(faltaIds);

    if (faltas.size() != faltaIds.size()) {
      throw IgrpResponseStatusException.badRequest("Existem faltas inválidas ou inexistentes");
    }

    /*for (var falta : faltas) {
      var funIdFalta = falta.getTiprelId()
          .getFunId()
          .getId();

      if (!funIdFalta.equals(funcionario.getId())) {
        throw IgrpResponseStatusException.badRequest(
            "Uma ou mais faltas não pertencem ao colaborador informado");
      }
    }*/

    // 4. Atualizar faltas
    for (var item : dto.getItensFalta()) {

      if (!item.isSelecionar()) continue;

      FaltaEntity falta = faltas.stream()
          .filter(f -> f.getId().equals(item.getId()))
          .findFirst()
          .orElseThrow();

      falta.setPedidoId(pedido);
      falta.setDescricaoMotivo(item.getMotivo());
      falta.setDecisaoResponsavel(dto.getParecerResponsavel());
      falta.setObsResponsavel(dto.getObsResponsavel());
      falta.setParamSitId(paramSituacao);
      falta.setEstado(Estado.P);
    }
    faltaRepository.saveAll(faltas);


    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.JUSTIFICAR_FALTA.name(),
        Estado.P
    );

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(pedido.getId());
    validacaoEntityRepository.save(validacao);

    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado()
    );
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
