package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.EditarPedidoJustificacaoCommand;
import cv.inps.rh.assiduidade.application.commands.EliminarPedidoJustificacaoCommand;
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
import cv.inps.rh.shared.domain.service.SaldoLockService;
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

  /** Valor de "Deduzir Falta Em" que remove a dedução no editar — ver resolverDeducaoEditada. */
  private static final String DEDUCAO_NENHUMA = "NENHUM";

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
  private final SaldoLockService saldoLockService;

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

    // Antes de qualquer leitura de saldo: daqui para a frente lê-se quanto resta de férias e
    // de dispensa e grava-se o consumo, e duas justificações simultâneas do mesmo colaborador
    // concederiam ambas o mesmo saldo.
    saldoLockService.lockColaborador(funcionarioUuid);

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

    // Regra: só vai a validação se forem mais de 3 dias no MÊS E o tipo de justificação
    // descontar no salário. Caso contrário fica logo activo.
    // A data vem da síntese e não do DTO: o item traz `data` como texto e pode nem vir
    // preenchido — o que o formulário garante é o id da síntese.
    var mesReferencia = entityManager
        .getReference(AssiduidadeSinteseDiarioEntity.class, selecionados.getFirst().getId())
        .getData();
    boolean requerValidacao = faltaDescontoService.requerValidacaoNoMes(
        funcionario.getUuid(), mesReferencia, selecionados.size(), paramSituacao);
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
      // Campo hidden derivado do tipo de falta — ver FaltaServiceWrite.
      falta.setFlgDescontoSal(
          paramSituacao != null && Integer.valueOf(1).equals(paramSituacao.getFlgFaltaDecontoSal()) ? 1 : 0);
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

    // É no despacho que os descontos são aplicados — é a escrita de saldo mais pesada de todo
    // o fluxo, e a que não pode correr em paralelo com outra do mesmo colaborador.
    saldoLockService.lockColaborador(funcionario.getUuid());

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

      // Despacho do RH (SIM/NAO) em RH_T_FALTA.DESPACHO_RH — ver FaltaServiceWrite.
      if (dto.getValidar() != null)
        falta.setDespachoRh(dto.getValidar().getCode());

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

    // Atualizar validação pendente. Pela referência (uuid do pedido), não pelo funcionário: a
    // validação foi gravada com referenciaUuid = pedido.uuid e o mesmo colaborador pode ter mais
    // do que um pedido pendente — procurar por funUuid apanhava o errado (ou rebentava).
    funcionarioRules.getValidacaoPendenteByReferenciaUuid(
        pedido.getUuid(),
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


  /**
   * Guarda partilhado pelo Editar e pelo Eliminar: um pedido cujo desconto já foi processado em
   * folha não pode ser mexido — o dinheiro já saiu no vencimento, e alterá-lo aqui deixaria a
   * folha e a assiduidade a dizer coisas diferentes.
   *
   * <p>Critério dado pelo utilizador (10/09): a falta tem {@code DEF_REM_ID} e essa definição já
   * foi apanhada por uma {@code RH_T_REMUNERACOES} (via {@code REM_1_ID}). Basta **uma** falta
   * do pedido nessas condições para bloquear o pedido inteiro.
   */
  private void garantirNaoProcessado(PedidoEntity pedido, String accao) {
    long processadas = faltaRepository.countFaltasProcessadasEmFolha(pedido.getId());
    if (processadas > 0)
      throw IgrpResponseStatusException.badRequest(
          "Não é possível " + accao + " este pedido: "
              + processadas + " falta(s) já foram processadas em folha.");
  }

  /**
   * Só um pedido <b>activo</b> se edita ou elimina (decisão de negócio, 10/09).
   *
   * <p>Cada estado tem a sua razão, e por isso cada um tem a sua mensagem — dizer só "não pode"
   * obrigava o utilizador a adivinhar porquê:
   *
   * <ul>
   *   <li>{@code P} — está em despacho, pertence ao checker. Deixar o maker editá-lo por baixo
   *       significaria o checker aprovar coisa diferente da que lhe foi apresentada. Quem se
   *       arrependeu pede a rejeição e volta a justificar.</li>
   *   <li>{@code I} — foi rejeitado, e a decisão é do checker. Editar um pedido recusado seria
   *       contornar a recusa sem passar por ninguém.</li>
   *   <li>{@code E} — já não existe.</li>
   * </ul>
   */
  private void garantirPedidoActivo(PedidoEntity pedido, String accao) {

    var estado = pedido.getEstado();

    if (Estado.P.name().equals(estado))
      throw IgrpResponseStatusException.badRequest(
          "Não é possível " + accao + " este pedido: está em validação, à espera de despacho do RH. "
              + "Só é possível " + accao + " depois de o pedido ser validado ou rejeitado.");

    if (Estado.I.name().equals(estado))
      throw IgrpResponseStatusException.badRequest(
          "Não é possível " + accao + " este pedido: foi rejeitado no despacho. "
              + "Para corrigir, registe uma nova justificação para os dias em causa.");

    if (Estado.E.name().equals(estado))
      throw IgrpResponseStatusException.badRequest(
          "Não é possível " + accao + " este pedido: já foi eliminado.");
  }

  /**
   * Editar um pedido de justificação (acção "Editar" do resumo de faltas, spec 09/09 :655).
   * Age no pedido inteiro, agrupado por {@code RH_T_FALTA.PEDIDO_ID}.
   *
   * <p>Não é um update de campos: os efeitos financeiros são <b>revertidos e reaplicados</b> a
   * partir do estado novo. Sem isso, trocar "Deduzir em" de FERIAS para DISPENSA deixava as
   * férias gozadas onde estavam e criava a dispensa por cima — dois descontos pelo mesmo dia.
   *
   * <p><b>Volta a despacho quando a edição mexe em dinheiro</b> — muda o tipo de justificação ou
   * a dedução — e a regra do registo se verifica (mais de 3 dias no mês e tipo que desconta
   * salário). Sem isto, registar 4 dias com um tipo que não desconta (fica activo de imediato) e
   * editar para um que desconta deixava 4 dias descontados sem nunca terem passado por despacho:
   * o maker-checker contornado por uma edição.
   *
   * <p>Alterações que não mexem em dinheiro — motivo, parecer, observação, responsável, anexos —
   * gravam direto, como antes. Reabrir despacho por causa de uma gralha corrigida trancaria o
   * pedido (ver {@link #garantirPedidoActivo}) até alguém o despachar de novo.
   */
  @Transactional
  public Map<String, ?> editarPedidoJustificacao(EditarPedidoJustificacaoCommand command) {

    var pedido = pedidoRepository.findByUuid(UUID.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Pedido de justificação de falta não encontrado"));

    var dto = command.getJustificarfalta();
    if (dto == null)
      throw IgrpResponseStatusException.badRequest("Corpo do pedido em falta");

    // O estado do pedido decide-se antes de ir buscar as faltas: é mais barato e é a
    // mensagem certa para o utilizador.
    garantirPedidoActivo(pedido, "editar");
    var vivas = faltasVivas(pedido);
    garantirNaoProcessado(pedido, "editar");

    var funcionario = pedido.getFunId();

    // O editar reverte e reaplica: devolve saldo e volta a consumi-lo, portanto compete com
    // qualquer outra escrita de saldo do mesmo colaborador.
    saldoLockService.lockColaborador(funcionario.getUuid());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    boolean comJustificativo = "SIM".equalsIgnoreCase(dto.getComJustificativo());
    var paramSituacao = resolverTipoJustificacao(dto.getTipoJustificacao(), comJustificativo);
    var responsavel = resolverResponsavel(dto.getResponsavelId());

    // Estado ANTIGO, lido antes de o ciclo o sobrescrever — é com ele que se decide se a edição
    // mexeu em dinheiro. Conjunto e não getFirst(): se os dias divergirem por alguma razão
    // histórica, qualquer divergência face ao valor novo conta como mudança.
    var tiposAntigos = vivas.stream()
        .map(f -> f.getParamSitId() != null ? f.getParamSitId().getId() : null)
        .collect(Collectors.toSet());
    var deducoesAntigas = vivas.stream()
        .map(FaltaEntity::getFlgDescontoFalta)
        .collect(Collectors.toSet());

    var deducao = resolverDeducaoEditada(dto.getDeduzirFaltaEm(), deducoesAntigas);

    // "Material" = mexe em dinheiro. Só o tipo de justificação (que manda no desconto salarial) e
    // a dedução o são; motivo, parecer, observação, responsável e anexos não.
    boolean mudouTipo = paramSituacao != null
        && !tiposAntigos.equals(Collections.singleton(paramSituacao.getId()));
    boolean mudouDeducao = !deducoesAntigas.equals(Collections.singleton(deducao));
    boolean mudancaMaterial = mudouTipo || mudouDeducao;

    // Reavaliar o despacho pela MESMA regra do registo (>3 dias no mês E desconto salarial), e só
    // quando algo material mudou: sem isto, editar 4 dias de um tipo que não desconta para um que
    // desconta deixava-os activos sem nunca passarem por despacho — o maker-checker contornado
    // por uma edição. Corrigir uma gralha no motivo não reabre nada, nem sequer vai à BD.
    var tipoEfectivo = paramSituacao != null ? paramSituacao : vivas.getFirst().getParamSitId();
    boolean requerValidacao = mudancaMaterial
        && faltaDescontoService.requerValidacaoNoMes(
            funcionario.getUuid(),
            vivas.getFirst().getDataInicio().toLocalDate(),
            vivas.size(),
            tipoEfectivo,
            pedido.getId());
    var estadoAlvo = requerValidacao ? Estado.P : null;

    // O editar mexe no PEDIDO, não na composição dele: os dias que o compõem mantêm-se todos.
    // A spec (:658) só diz "permite editar a justificação de falta do grupo selecionado" e dá
    // ao Eliminar — e só a ele — o efeito de pôr faltas em 'E'. Quem se quiser livrar de um dia
    // elimina o pedido e volta a justificar os dias certos.
    //
    // Por isso o `itensFalta` é ignorado aqui, ao contrário da convenção de arrays dos outros
    // PUT: aplicá-la neste ecrã era perigoso de mais. O FaltaItemDTO tem um campo `selecionar`,
    // logo o formulário tem checkboxes, e um frontend que enviasse só as linhas marcadas —
    // a coisa mais natural de fazer — apagava silenciosamente as restantes faltas e revertia o
    // dinheiro delas, sem erro nenhum à vista.
    for (var falta : vivas) {

      // Reverter antes de reaplicar — é isto que impede o desconto duplo quando o tipo ou a
      // dedução mudam. Mas SÓ quando algo material mudou: reverter e reaplicar numa edição
      // cosmética matava as linhas de desconto e criava outras iguais, o que enchia a base de
      // linhas mortas, quebrava a ligação de qualquer remuneração já emitida à definição antiga
      // e fazia o histórico parecer que se mexeu em dinheiro para corrigir uma gralha.
      if (mudancaMaterial)
        faltaDescontoService.reverter(falta, pedido);

      if (StringUtils.hasText(dto.getMotivo()))
        falta.setDescricaoMotivo(dto.getMotivo());
      if (StringUtils.hasText(dto.getComJustificativo()))
        falta.setFlgJustificativo(dto.getComJustificativo());
      if (dto.getParecerResponsavel() != null)
        falta.setDecisaoResponsavel(dto.getParecerResponsavel());
      if (responsavel != null)
        falta.setResponsavelId(responsavel);
      if (dto.getObsResponsavel() != null)
        falta.setObsResponsavel(dto.getObsResponsavel());
      if (paramSituacao != null)
        falta.setParamSitId(paramSituacao);

      // Campos financeiros e efeitos: só se mexem quando a edição foi material. Numa edição
      // cosmética o desconto que lá está continua a ser o certo — foi calculado a partir do
      // mesmo tipo e da mesma dedução.
      if (mudancaMaterial) {

        falta.setFlgDescontoFalta(deducao);
        // Derivado do tipo EFECTIVO e não do que veio no payload: com o tipo omitido,
        // `paramSituacao` é null e a falta ficava a dizer "não desconta" apesar de descontar.
        falta.setFlgDescontoSal(
            tipoEfectivo != null && Integer.valueOf(1).equals(tipoEfectivo.getFlgFaltaDecontoSal())
                ? 1 : 0);

        if (estadoAlvo != null) {
          falta.setEstado(estadoAlvo);
          // O despacho anterior deixou de valer para esta falta: mantê-lo faria a falta aparecer
          // em despacho já com a decisão do ciclo passado.
          falta.setDespachoRh(null);
        }

        // Reaplica com o estado novo. Uma falta que volta a despacho não recebe efeitos agora — a
        // reserva (saldo a contar as faltas em P) garante que o saldo continua comprometido.
        if (!requerValidacao && Estado.A.equals(falta.getEstado()))
          faltaDescontoService.aplicar(falta, pedido, tipoRelAtual);
      }
    }

    faltaRepository.saveAll(vivas);

    if (requerValidacao)
      reabrirDespacho(pedido, funcionario, tipoRelAtual);

    // Anexos: semântica dos arrays da casa — null preserva, item sem id cria, ausente fica 'E'.
    if (dto.getDocumentos() != null) {
      var existentes = documentoEntityRepository
          .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_PEDIDO.name(), pedido.getUuid());
      var sincronizados = documentoMapper.syncDocumentos(
          new ArrayList<>(existentes), dto.getDocumentos(),
          TableName.RH_T_PEDIDO.name(), pedido.getId(), pedido.getUuid(), 1L, funcionario);
      for (var doc : sincronizados)
        if (doc.getUuid() == null) doc.setUuid(UuidCreator.getTimeOrderedEpoch());
      documentoEntityRepository.saveAll(sincronizados);
    }

    long ficaram = vivas.stream().filter(f -> !Estado.E.equals(f.getEstado())).count();
    LOGGER.info("[EDITAR] Pedido de justificação {} editado ({} dias; material={}, voltou a despacho={}).",
        pedido.getUuid(), ficaram, mudancaMaterial, requerValidacao);

    // O estado é lido DEPOIS de gravar: quem acabou de voltar a despacho tem de o ver na resposta.
    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado(),
        "etapa", Objects.requireNonNullElse(pedido.getEtapa(), ""),
        "requerValidacao", requerValidacao,
        "totalRegistos", ficaram);
  }

  /**
   * "Deduzir Falta Em" no editar: {@code null} ou ausente <b>preserva</b> o que está gravado,
   * {@code NENHUM} limpa, {@code FERIAS}/{@code DISPENSA} trocam.
   *
   * <p>Antes, o campo era atribuído incondicionalmente e um {@code PUT} que não o reenviasse
   * apagava a dedução: as férias deixavam de cobrir e o dia inteiro passava a desconto salarial.
   * Um campo ausente no payload não pode destruir estado financeiro — e, com a reavaliação do
   * despacho, ainda mandaria o pedido a validação sem ninguém perceber porquê. Passa a valer o
   * mesmo que o {@code validarFaltaJustificada} já fazia.
   */
  private String resolverDeducaoEditada(String deduzirFaltaEm, Set<String> deducoesAntigas) {

    if (!StringUtils.hasText(deduzirFaltaEm))
      return deducoesAntigas.size() == 1 ? deducoesAntigas.iterator().next() : null;

    if (DEDUCAO_NENHUMA.equalsIgnoreCase(deduzirFaltaEm))
      return null;

    return TipoDescontoFalta.fromCodeOrThrow(deduzirFaltaEm).getCode();
  }

  /**
   * Devolve o pedido ao despacho do RH depois de uma edição que mexeu em dinheiro.
   *
   * <p>A validação pendente é <b>reutilizada</b> quando existe. Duas linhas em {@code P} para o
   * mesmo pedido partiriam o {@code validar} e o {@code eliminar}, que procuram a pendente com um
   * {@code Optional} — e não há unique constraint em BD a impedi-lo. Já a validação de um despacho
   * anterior está fechada em {@code A} e não colide: fica como histórico dessa aprovação, e esta
   * ronda ganha a sua própria linha.
   *
   * <p>Sem notificação e sem ordem de serviço, por simetria com o registo: ambos pertencem ao
   * despacho, não ao acto de o pedir.
   */
  private void reabrirDespacho(
      PedidoEntity pedido, FuncionarioEntity funcionario, TiposRelacionamentoEntity tipoRelAtual) {

    var pendente = funcionarioRules.getValidacaoPendenteByReferenciaUuid(
        pedido.getUuid(), TipoAcao.INSERT, Referencia.JUSTIFICAR_FALTA);

    if (pendente.isEmpty()) {
      var validacao = dadosContratuaisMapper.toValidacaoInsert(
          TipoAcao.INSERT.name(), Referencia.JUSTIFICAR_FALTA.name(), Estado.P);
      validacao.setFunId(funcionario);
      validacao.setTiprelId(tipoRelAtual);
      validacao.setReferenciaId(pedido.getId());
      validacao.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(validacao);
    }

    pedido.setEstado(Estado.P.name());
    pedido.setEtapa("DESPACHO_RH");
    pedidoRepository.save(pedido);
  }

  /**
   * Eliminar um pedido de justificação (acção "Eliminar" do resumo de faltas, spec 09/09 :665).
   * Soft-delete: {@code RH_T_FALTA.ESTADO = 'E'}, tal como a spec manda.
   *
   * <p>Desfaz também os efeitos financeiros (decisão de negócio, 10/09): sem isso o colaborador
   * ficava descontado no vencimento — ou com férias e horas de dispensa gastas — por uma falta
   * que já não existe.
   */
  @Transactional
  public Map<String, ?> eliminarPedidoJustificacao(EliminarPedidoJustificacaoCommand command) {

    var pedido = pedidoRepository.findByUuid(UUID.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Pedido de justificação de falta não encontrado"));

    garantirPedidoActivo(pedido, "eliminar");
    var vivas = faltasVivas(pedido);
    garantirNaoProcessado(pedido, "eliminar");

    // Devolver saldo também é escrevê-lo: sem o lock, uma devolução a meio de um consumo
    // simultâneo pode perder-se.
    saldoLockService.lockColaborador(pedido.getFunId().getUuid());

    for (var falta : vivas) {
      faltaDescontoService.reverter(falta, pedido);
      falta.setEstado(Estado.E);
    }
    faltaRepository.saveAll(vivas);

    // Os anexos são do pedido e acompanham-no.
    var anexos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_PEDIDO.name(), pedido.getUuid());
    anexos.forEach(a -> a.setEstado(Estado.E));
    documentoEntityRepository.saveAll(anexos);

    // Uma validação por despachar deixa de fazer sentido: o pedido que lhe deu origem sumiu.
    funcionarioRules.getValidacaoPendenteByReferenciaUuid(
            pedido.getUuid(), TipoAcao.INSERT, Referencia.JUSTIFICAR_FALTA)
        .ifPresent(v -> {
          v.setEstado(Estado.E);
          validacaoEntityRepository.save(v);
        });

    pedido.setEstado(Estado.E.name());
    pedido.setEtapa("FINALIZADO");
    pedidoRepository.save(pedido);

    LOGGER.info("[ELIMINAR] Pedido de justificação {} eliminado ({} dias).",
        pedido.getUuid(), vivas.size());

    return Map.of(
        "pedidoId", pedido.getId(),
        "pedidoUuid", pedido.getUuid(),
        "estado", pedido.getEstado(),
        "totalRegistos", vivas.size());
  }

  /** Faltas do pedido que ainda contam — as eliminadas não voltam a ser mexidas. */
  private List<FaltaEntity> faltasVivas(PedidoEntity pedido) {
    var faltas = faltaRepository.findAllByPedidoIdOrderByDataInicioAsc(pedido);
    if (faltas.isEmpty())
      throw IgrpResponseStatusException.badRequest("Não existem faltas associadas a este pedido");
    var vivas = faltas.stream().filter(f -> !Estado.E.equals(f.getEstado())).toList();
    if (vivas.isEmpty())
      throw IgrpResponseStatusException.badRequest("Este pedido já foi eliminado");
    return vivas;
  }

}
