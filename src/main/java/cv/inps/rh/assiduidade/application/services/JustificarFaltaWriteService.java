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

    var funcionarioUuid = UUID.fromString(command.getFuncionarioId());
    var funcionario = funcionarioRepository.findByUuid(funcionarioUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Funcionário não encontrado"));

    var dto = command.getJustificarfalta();
    if (dto == null || dto.getItensFalta() == null || dto.getItensFalta().isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Nenhuma falta selecionada para validação");
    }

//    var pedido = pedidoRepository.findByIdOrThrow(dto.getPedidoId());

       var pedido = pedidoRepository.findByIdOrThrow(1L);


    // Atualizar faltas com observações e tipo de situação
    var paramSituacao = paramSituacaoEntityRepository.findByIdOrThrow(dto.getTipoJustificacao());

    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);

    for (var item : dto.getItensFalta()) {
      if (!item.isSelecionar()) continue;

      FaltaEntity falta = faltas.stream()
          .filter(f -> f.getId().equals(item.getId()))
          .findFirst()
          .orElseThrow();

      falta.setDescricaoMotivo(item.getMotivo());
      falta.setObsResponsavel(dto.getObsResponsavel());
      falta.setDespachoRh(dto.getDespachoRh());
      falta.setParamSitId(paramSituacao);
    }
    faltaRepository.saveAll(faltas);

    final Estado estadoParaAtualizar = (dto.getValidar() == EstadoValidacao.SIM) ? Estado.A : Estado.I;


    faltas.forEach(f -> f.setEstado(estadoParaAtualizar));
    faltaRepository.saveAll(faltas);

    pedido.setEstado(estadoParaAtualizar);
    pedido.setEtapa("FINALIZADO");
    pedidoRepository.save(pedido);

    // Atualizar validação
    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
            Referencia.JUSTIFICAR_FALTA)
        .ifPresent(v -> {
          v.setEstado(estadoParaAtualizar);
          validacaoEntityRepository.save(v);
        });

    /*
     * ================= DÚVIDAS / PONTOS A CONFIRMAR COM ANALISTA =================
     *
     * 1️⃣ Desconto de Salário (RH_T_DEF_PAGAMENTOS)
     *    - Qual entidade exata devemos usar para registrar o desconto de salário?
     *    - Quais campos são obrigatórios: funId, tiprelId, referenciaId, valor, data, estado?
     *    - O desconto é automático ao validar a falta ou apenas registro histórico?
     *
     * 2️⃣ Desconto de Férias (RH_T_FERIAS_GOZADAS)
     *    - Existe entidade mapeada para registrar férias gozadas?
     *    - Como calcular os dias a descontar por falta?
     *    - Só desconta se houver saldo suficiente de férias?
     *    - As datas da falta (dataInicio/dataFim) devem ser replicadas no registro de férias?
     *
     * 3️⃣ Desconto de Horas de Dispensa (RH_T_DISPENSA)
     *    - Existe entidade mapeada para horas de dispensa?
     *    - Como calcular a quantidade de horas a descontar por falta?
     *    - Aplica apenas a faltas injustificadas ou todas as faltas?
     *
     * 4️⃣ Valor da Justificação de Falta
     *    - Para cada tipoJustificacao (ParamSituacaoEntity.tipoFalta), como calcular o valor?
     *    - Valor por hora ou por dia?
     *    - É apenas para descontos ou também para relatórios?
     *
     * 5️⃣ Integração com Pedido e Validação
     *    - Ao validar a falta, devemos atualizar estados:
     *        FaltaEntity.estado = 'A'
     *        PedidoEntity.estado = 'A', PedidoEntity.etapa = 'FINALIZADO'
     *        ValidacaoEntity.estado = 'A'
     *    - Isso deve ocorrer somente se EstadoValidacao enviado for "SIM"?
     *
     * 6️⃣ Observações Gerais
     *    - O campo tipoJustificacao já vem no DTO como Long (id de ParamSituacaoEntity), está correto?
     *    - Se uma falta já tiver desconto registrado, sobrescrever ou criar novo registro?
     *    - Como tratar múltiplas faltas do mesmo colaborador no mesmo mês: justificar todas juntas ou individualmente?
     *    - Confirmação do cálculo de horas trabalhadas e horas de ausência para atualização na FaltaEntity.
     *
     * =============================================================================
     */

    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado()
    );
  }


}
