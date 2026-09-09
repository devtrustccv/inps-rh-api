package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.JustificarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaJustificadaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.TipoDescontoFalta;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.NotificacaoDispatchService;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.TimeUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(JustificarFaltaWriteService.class);

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
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final NotificacaoDispatchService notificacaoDispatchService;
  private final FaltaDescontoService faltaDescontoService;
  private final FaltaValorCalculator faltaValorCalculator;
  private final ResponsavelEntityRepository responsavelEntityRepository;

  /**
   * Responsável do parecer. O DTO envia a PK de RH_T_RESPONSAVEL — a mesma que a leitura
   * devolve em {@code responsavelId} — para o round-trip do formulário fechar. Sem isto o
   * campo era aceite e descartado: o dropdown "Responsável" nunca era gravado.
   */
  private ResponsavelEntity resolverResponsavel(Long responsavelId) {
    if (responsavelId == null)
      return null;
    return responsavelEntityRepository.findByIdOrThrow(responsavelId);
  }

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

    var selecionados = dto.getItensFalta().stream().filter(FaltaItemDTO::isSelecionar).toList();

    // O tipo de justificação só existe no formulário quando "Com Justificativo" = SIM
    // (spec: "os campos abaixo só aparecem caso Com Justificativo = SIM"). Marcar a
    // falta como não justificada é um acto legítimo e não precisa de tipo. O radio é do
    // cabeçalho — aplica-se a todas as faltas seleccionadas, não é escolha por dia.
    boolean comJustificativo = "SIM".equalsIgnoreCase(dto.getComJustificativo());

    var paramSituacao = resolverTipoJustificacao(dto.getTipoJustificacao(), comJustificativo);

    // Regra: só vai a validação se forem mais de 3 dias E o tipo de justificação
    // descontar no salário. Caso contrário fica logo activo.
    boolean requerValidacao = faltaDescontoService.requerValidacao(selecionados.size(), paramSituacao);
    var estadoInicial = requerValidacao ? Estado.P : Estado.A;

    var deducao = StringUtils.hasText(dto.getDeduzirFaltaEm())
        ? TipoDescontoFalta.fromCodeOrThrow(dto.getDeduzirFaltaEm()).getCode()
        : null;

    var responsavel = resolverResponsavel(dto.getResponsavelId());

    // Criar pedido de justificação
    PedidoEntity pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("JUSTIFICACAO_FALTA");
    pedido.setOrigem("RH");
    pedido.setEtapa(requerValidacao ? "DESPACHO_RH" : "FINALIZADO");
    pedido.setEstado(estadoInicial.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    // Criar faltas a partir das sínteses diárias
    List<FaltaEntity> faltas = new ArrayList<>();
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionarioUuid);
    BigDecimal valorTotal = BigDecimal.ZERO;

    for (var item : selecionados) {

      AssiduidadeSinteseDiarioEntity sintese;
      try {
        sintese = entityManager.getReference(
            AssiduidadeSinteseDiarioEntity.class,
            item.getId());
      } catch (EntityNotFoundException e) {
        throw IgrpResponseStatusException.badRequest(
            "Síntese diária inválida: " + item.getId());
      }

      // Não permitir duas faltas vivas no mesmo dia. A verificação é pelo dia e não
      // pela síntese: o mesmo dia pode ter uma síntese importada e outra manual, e
      // verificar por síntese deixava passar uma falta em cada.
      if (faltaRepository.existeFaltaVivaNoDia(funcionario.getId(), sintese.getData())) {
        throw IgrpResponseStatusException.badRequest(
            "Já existe uma falta associada à data " + sintese.getData());
      }

      FaltaEntity falta = new FaltaEntity();
      falta.setPedidoId(pedido);
      falta.setSinteseDiarioId(sintese);
      falta.setTiprelId(tipoRelAtual);
      falta.setTipo(FaltaDescontoService.TIPO_FALTA);

      var dia = sintese.getData();
      falta.setDataInicio(LocalDateTime.of(dia, LocalTime.MIN));
      falta.setDataFim(LocalDateTime.of(dia, LocalTime.of(23, 59, 59)));

      falta.setHorasAusencia(sintese.getHorasAusencia());

      // Valor à hora x horas de ausência do dia — via CALCULO_FALTA_DIARIO com
      // fallback em Java. O valor enviado pelo cliente é meramente indicativo.
      var horasAusencia = TimeUtils.intervalFormatToHHmm(sintese.getHorasAusencia());
      var valor = faltaValorCalculator.valorDia(tipoRelAtual.getId(), dia, horasAusencia);
      falta.setValor(valor);
      valorTotal = valorTotal.add(valor);

      // Motivo e "Com justificativo?" são do bloco "Justificar Faltas Selecionadas": uma
      // caixa e um radio únicos, aplicados a todas as faltas seleccionadas ("A justificativa
      // será aplicada às N faltas selecionadas"). Antes vinham por item, o que obrigava o
      // frontend a repetir o mesmo valor em cada linha e deixava o mesmo pedido com motivos
      // diferentes se falhasse numa.
      falta.setDescricaoMotivo(dto.getMotivo());
      falta.setFlgJustificativo(
          StringUtils.hasText(dto.getComJustificativo()) ? dto.getComJustificativo() : "SIM");

      falta.setDecisaoResponsavel(dto.getParecerResponsavel());
      falta.setResponsavelId(responsavel);
      falta.setObsResponsavel(dto.getObsResponsavel());

      falta.setParamSitId(paramSituacao);
      falta.setFlgDescontoFalta(deducao);
      falta.setEstado(estadoInicial);
      falta.setUuid(UuidCreator.getTimeOrderedEpoch());

      faltas.add(falta);
    }

    // 6 Persistir faltas
    faltaRepository.saveAll(faltas);

    // Os anexos são SEMPRE do pedido, nunca de um dia: justificar cria um RH_T_PEDIDO e todas
    // as faltas seleccionadas nascem com esse PEDIDO_ID, e o ecrã só tem um bloco de anexos
    // ("Anexar Documentos", no painel da justificação). Deixou por isso de existir anexo por
    // item — ver FaltaItemDTO.
    List<DocumentoEntity> documentos = new ArrayList<>();
    // Documentos do bloco "Justificar Faltas Selecionadas" — o formulário permite anexar
    // vários e aplicam-se a TODAS as faltas seleccionadas, não a um dia. Ficam por isso
    // ligados ao PEDIDO, que é o agrupador do conjunto (RH_T_FALTA.PEDIDO_ID).
    //
    // A spec diz REFERENCIA_NAME='RH_T_FALTA' para os anexos, mas essa regra só funciona
    // para o anexo de um dia (acima). Prendê-los à primeira falta — como se fazia antes —
    // tornava-os indistinguíveis do anexo dessa falta: a leitura devolvia um e escondia o
    // outro, e eliminar o dia âncora deixava o anexo do grupo órfão. Decidido com o
    // utilizador: o anexo do grupo pertence ao pedido.
    if (dto.getDocumentos() != null && !dto.getDocumentos().isEmpty()) {
      for (var anexo : dto.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            anexo,
            estadoInicial,
            TableName.RH_T_PEDIDO.name(),
            pedido.getId(),
            pedido.getUuid(),
            1L,
            funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentos.add(doc);
      }
    }

    if (!documentos.isEmpty()) {
      documentoEntityRepository.saveAll(documentos);
    }

    if (requerValidacao) {
      var validacao = dadosContratuaisMapper.toValidacaoInsert(
          TipoAcao.INSERT.name(),
          Referencia.JUSTIFICAR_FALTA.name(),
          Estado.P);
      validacao.setFunId(funcionario);
      validacao.setTiprelId(tipoRelAtual);
      validacao.setReferenciaId(pedido.getId());
      validacao.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(validacao);
    } else {
      // Sem validação, os descontos são aplicados de imediato.
      for (var falta : faltas)
        faltaDescontoService.aplicar(falta, pedido, tipoRelAtual);
      faltaRepository.saveAll(faltas);
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid());
    resp.put("estado", pedido.getEstado());
    resp.put("requerValidacao", requerValidacao);
    resp.put("totalRegistos", faltas.size());
    if (!faltas.isEmpty())
      resp.put("valorDiario", faltas.getFirst().getValor());
    resp.put("valorTotal", valorTotal);
    return resp;
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
    // Parametrização da justificação — mesma tolerância ao "0" do formulário. O radio
    // "Com Justificativo?" é do cabeçalho, como no registo (não é escolha por dia).
    boolean comJustificativo = "SIM".equalsIgnoreCase(dto.getComJustificativo());
    var paramSituacao = resolverTipoJustificacao(dto.getTipoJustificacao(), comJustificativo);

    // Todas as faltas do pedido (já criadas na fase de justificar)
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoIdOrderByDataInicioAsc(pedido);
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
    var responsavelValidacao = resolverResponsavel(dto.getResponsavelId());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // Atualizar apenas as faltas correspondentes às sínteses selecionadas
    for (var item : dto.getItensFalta()) {

      if (!item.isSelecionar())
        continue;

      FaltaEntity falta = faltaPorSinteseId.get(item.getId());

      if (falta == null) {
        throw IgrpResponseStatusException.badRequest(
            "Falta não encontrada para a síntese diária ID: " + item.getId());
      }

      // Do cabeçalho, como no registo. Só sobrepõe o motivo se veio no payload, para uma
      // validação sem alterações não apagar o que o maker escreveu.
      if (StringUtils.hasText(dto.getMotivo()))
        falta.setDescricaoMotivo(dto.getMotivo());
      falta.setObsResponsavel(dto.getObsResponsavel());
      // Só sobrepõe se o checker indicou um responsável — senão mantém o da justificação.
      if (responsavelValidacao != null)
        falta.setResponsavelId(responsavelValidacao);
      // Só sobrepõe se veio no payload — caso contrário mantém o que foi gravado na
      // justificação, em vez de o apagar.
      if (paramSituacao != null)
        falta.setParamSitId(paramSituacao);
      falta.setEstado(estadoFinal);

      if (StringUtils.hasText(dto.getDeduzirFaltaEm()))
        falta.setFlgDescontoFalta(TipoDescontoFalta.fromCodeOrThrow(dto.getDeduzirFaltaEm()).getCode());

      if (estadoFinal == Estado.A)
        faltaDescontoService.aplicar(falta, pedido, tipoRelAtual);
    }

    faltaRepository.saveAll(faltas);

    // Anexos do pedido: o checker pode juntar um documento ao despachar, substituir ou
    // retirar um que o maker anexou. Semântica dos arrays da casa (syncDocumentos):
    // documentos == null preserva o que está; item sem id cria; item que desaparece do
    // array fica 'E'. Os anexos são do PEDIDO — ver justificarFalta.
    if (dto.getDocumentos() != null) {
      var existentes = documentoEntityRepository
          .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_PEDIDO.name(), pedido.getUuid());
      var sincronizados = documentoMapper.syncDocumentos(
          new ArrayList<>(existentes),
          dto.getDocumentos(),
          TableName.RH_T_PEDIDO.name(),
          pedido.getId(),
          pedido.getUuid(),
          1L,
          funcionario);
      for (var doc : sincronizados) {
        if (doc.getUuid() == null) doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        // Um anexo novo nasce 'P' no mapper; ao validar acompanha o estado do pedido.
        if (Estado.P.equals(doc.getEstado())) doc.setEstado(estadoFinal);
      }
      documentoEntityRepository.saveAll(sincronizados);
    }

    if (estadoFinal == Estado.A)
      ordemServicoWriteService.criar(funcionario, tipoRelAtual, dto.getTipoOrdemServico());

    // Atualizar pedido
    pedido.setEstado(estadoFinal.name());
    pedido.setEtapa("FINALIZADO");
    pedidoRepository.save(pedido);

    enviarNotificacaoJustificacaoFalta(pedido, funcionario);

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


  }

  /**
   * Resolve o tipo de justificação enviado pelo formulário.
   *
   * <p>O frontend envia {@code 0} para "nada seleccionado" — é a sentinela dele para
   * campos numéricos por preencher. Tratá-lo como um id real fazia a pesquisa rebentar
   * com um 404 enganador; aqui é lido como ausência de valor.
   *
   * @param obrigatorio quando alguma falta seleccionada vem com justificativo, o tipo
   *                    passa a ser exigido — sem ele não há como apurar desconto.
   */
  private ParamSituacaoEntity resolverTipoJustificacao(Long tipoJustificacao, boolean obrigatorio) {

    boolean preenchido = tipoJustificacao != null && tipoJustificacao > 0;

    if (!preenchido) {
      if (obrigatorio)
        throw IgrpResponseStatusException.badRequest(
            "Tipo de justificação é obrigatório quando a falta é marcada com justificativo");
      return null;
    }

    var paramSituacao = paramSituacaoEntityRepository.findById(tipoJustificacao)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Tipo de justificação inválido: " + tipoJustificacao));

    // tipoFalta não nulo indica que este paramSituacao serve para justificar faltas
    if (paramSituacao.getTipoFalta() == null)
      throw IgrpResponseStatusException.badRequest("Tipo justificativo não permitido para falta");

    return paramSituacao;
  }

  private void enviarNotificacaoJustificacaoFalta(PedidoEntity pedido, FuncionarioEntity funcionario) {
    var emailOpt = funcionario.getContactos().stream()
        .filter(c -> "EMAIL".equalsIgnoreCase(c.getTipoContacto()))
        .map(ContactoEntity::getContacto)
        .findFirst();
    if (emailOpt.isEmpty()) {
      LOGGER.warn("Funcionário {} sem email para notificação de justificação de falta", funcionario.getUuid());
      return;
    }
    var vars = Map.of(
        "nome", funcionario.getNome() != null ? funcionario.getNome() : "",
        "estado", pedido.getEstado() != null ? pedido.getEstado() : ""
    );
    notificacaoDispatchService.enviar(
        "JUSTIFICACAO_FALTA", emailOpt.get(), funcionario.getNome(),
        pedido.getId(), "RH_T_PEDIDO", pedido.getUuid(), funcionario, vars);
  }

}
