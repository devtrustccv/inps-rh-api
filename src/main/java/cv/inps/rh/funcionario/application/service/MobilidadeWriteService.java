package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.EditarMobilidadeCommand;
import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.commands.ValidarMobilidadeCommand;
import cv.inps.rh.funcionario.application.constants.TipoMobilidade;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MobilidadeWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MobilidadeWriteService.class);

  private final FuncionarioRules funcionarioRules;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final EntityManager entityManager;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper tipoRelRemPagHelper;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;

  /**
   * "Mobilidade processada" = igual à coluna PROCESSAMENTO da vista RH_V_MOBILIDADE: existe um tiprel
   * desta mobilidade com registo em RH_T_PROC_FUNCIONARIOS (já entrou em folha). Doc: o botão Editar
   * só fica visível se a mobilidade não tiver processamento — aqui garantimos o mesmo no servidor.
   */
  private boolean mobilidadeProcessada(MobilidadeEntity mobilidade) {
    return mobilidade != null && mobilidade.getId() != null
        && processamentoFuncionarioRepository.existsByTiprel_MobId_Id(mobilidade.getId());
  }

  @Transactional
  public SuccessResponseDTO save(SaveMobilidadeCommand command) {

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)){
      throw IgrpResponseStatusException.badRequest("Funcionário tem uma mobilidade pendente por validar");
    }

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // A mobilidade em vigor no momento do registo é o "antes" desta. Guardamo-la já aqui, em
    // RH_T_MOBILIDADE.MOB_ID, porque no registo ainda não existe tipo_relacionamento novo — sem isto
    // o ecrã de validação não teria como mostrar direção/secção/local "antes".
    var novaMobilidade = createMobilidade(mobilidadeDto, funcionario, tipoRelacionamentoAtual.getMobId());

    // Novo padrão: o REGISTO não cria nem altera tipo_relacionamento. O vínculo atual mantém-se
    // intacto e continua a ser o atual até a mobilidade ser validada. Aqui só se grava a mobilidade
    // (estado P) e a validação pendente; a criação/troca de tiprel acontece no validarMobilidade (SIM).
    entityManager.persist(novaMobilidade);
    entityManager.flush();

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.MOBILIDADE.name(), Estado.P);
    valid.setFunId(funcionario);
    // tiprelId = vínculo atual, apenas para contexto/leitura — NÃO é alterado no registo.
    valid.setTiprelId(tipoRelacionamentoAtual);
    valid.setReferenciaId(novaMobilidade.getId());
    valid.setReferenciaUuid(novaMobilidade.getUuid());
    entityManager.persist(valid);
    entityManager.flush();

    // Baseline JaVers do REGISTO: grava o snapshot inicial da mobilidade recém-criada. Sem isto, o
    // JaVers só passa a conhecer a entidade na 1ª EDIÇÃO — e essa edição sairia como snapshot inicial
    // (sem "antes"), deixando a grelha vazia. Com o baseline aqui, qualquer edição futura (incluindo a
    // primeira) já produz um diff antes→depois. Carimbado com a validação de INSERT; a grelha desta
    // valida­ção fica vazia de propósito (um REGISTO é criação, não tem "antes").
    try {
      ValidacaoAuditContext.set(valid.getId(), valid.getUuid(), "RH_T_MOBILIDADE");
      mobilidadeEntityRepository.save(novaMobilidade);
    } finally {
      ValidacaoAuditContext.clear();
    }

    return new SuccessResponseDTO(true, novaMobilidade.getUuid().toString(), "Mobilidade registada.", java.util.List.of());

  }


  private MobilidadeEntity createMobilidade(MobilidadeDTO mobilidadeDTO,
                                            cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity funcionario,
                                            MobilidadeEntity mobilidadeAnterior){
     if (mobilidadeDTO == null) return null;

    // Tipo de mobilidade = multi-select (códigos do domínio separados por vírgula, ex. "DIRECAO,SECAO").
    // O ecrã só mostra os campos "(depois)" dos tipos escolhidos; tem de existir pelo menos um.
    var selecionados = tiposSelecionados(mobilidadeDTO.getTipoMobilidade());
    if (selecionados.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "Indique o tipo de mobilidade (Direcção, Unidade e/ou Local de trabalho).");
    }

    var me = new MobilidadeEntity();
    me.setTipoSituacao(ValidationUtil.trimToNull(mobilidadeDTO.getTipoMobilidade()));
    me.setObs("MOBILIDADE");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setFunId(funcionario);
    me.setDataInicio(mobilidadeDTO.getDataInicio());
    me.setDataFim(mobilidadeDTO.getDataFim());
    me.setEstado(Estado.P);
    me.setMobId(mobilidadeAnterior);
    // Por cada tipo: se foi escolhido, o "(depois)" é obrigatório; se não, o campo não muda e herda o
    // valor "antes" (mobilidade anterior), evitando exigir ao frontend reenviar o que não alterou.
    aplicarCamposMobilidade(me, mobilidadeDTO, selecionados, mobilidadeAnterior);
    return me;
  }

  /**
   * Interpreta o {@code tipoMobilidade} (códigos do domínio separados por vírgula) no conjunto de
   * {@link TipoMobilidade}. Rejeita códigos desconhecidos — só DIRECAO/SECAO/LOCAL_TRABALHO são
   * selecionáveis no ecrã de mobilidade.
   */
  private java.util.EnumSet<TipoMobilidade> tiposSelecionados(String tipoMobilidade) {
    var selecionados = java.util.EnumSet.noneOf(TipoMobilidade.class);
    if (tipoMobilidade == null) {
      return selecionados;
    }
    for (var token : tipoMobilidade.split(",")) {
      var codigo = ValidationUtil.trimToNull(token);
      if (codigo == null) {
        continue;
      }
      var tipo = TipoMobilidade.fromCodigo(codigo).orElseThrow(() ->
          IgrpResponseStatusException.badRequest("Tipo de mobilidade inválido: '" + token.trim() + "'."));
      selecionados.add(tipo);
    }
    return selecionados;
  }

  /**
   * Preenche direção/unidade/local do novo registo: para cada tipo ESCOLHIDO exige o "(depois)" e
   * resolve-o; para os NÃO escolhidos copia o valor "antes" (mobilidade anterior) — a mobilidade
   * grava sempre os três campos, só mudando os que o utilizador selecionou.
   */
  private void aplicarCamposMobilidade(MobilidadeEntity me, MobilidadeDTO dto,
      java.util.EnumSet<TipoMobilidade> selecionados, MobilidadeEntity anterior) {
    for (var tipo : TipoMobilidade.values()) {
      if (selecionados.contains(tipo)) {
        var id = tipo.depoisId(dto);
        if (id == null) {
          throw IgrpResponseStatusException.badRequest(
              "Escolheu mobilidade de " + tipo.getLabel() + ": indique o campo \"" + tipo.getLabel() + " (depois)\".");
        }
        tipo.set(me, ValidationUtil.ref(entityManager, tipo.getEntityType(), id));
      } else {
        tipo.set(me, tipo.antes(anterior));
      }
    }
  }

  private MobilidadeEntity updateMobilidade(MobilidadeEntity me ,MobilidadeDTO mobilidadeDTO){
    if (mobilidadeDTO == null) return null;
    var tipoRef = ValidationUtil.trimToNull(mobilidadeDTO.getTipoMobilidade());
    if (tipoRef != null) me.setTipoSituacao(tipoRef);

    var localTrabRef = ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, mobilidadeDTO.getLocalTrabalhoDestino());
    if (localTrabRef != null) me.setLocalTrabId(localTrabRef);

    var secaoRef = ValidationUtil.ref(entityManager, SecaoEntity.class, mobilidadeDTO.getSeccaoDestino());
    if (secaoRef != null) me.setSecaoId(secaoRef);

    var instidRef = ValidationUtil.ref(entityManager, DirecaoEntity.class, mobilidadeDTO.getDirecaoDestino());
    if (instidRef != null) me.setInstidId(instidRef);

    // Null-safe como os restantes campos: um payload parcial não deve apagar as datas do registo.
    if (mobilidadeDTO.getDataInicio() != null) me.setDataInicio(mobilidadeDTO.getDataInicio());
    if (mobilidadeDTO.getDataFim() != null) me.setDataFim(mobilidadeDTO.getDataFim());
    return me;
  }

  @Transactional
  public SuccessResponseDTO validarMobilidade(ValidarMobilidadeCommand command){

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    // A mobilidade a validar é identificada pelo uuid (path {mobilidadeId}) — já NÃO se descobre via
    // tiprel.getMobId(), porque no registo não se cria/troca tiprel nenhum.
    var mobilidade = mobilidadeEntityRepository.findByUuid(
        IdentificadorUnico.from(command.getMobilidadeId()).valor())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Mobilidade não encontrada."));
    var mobUuid = mobilidade.getUuid();

    // Endpoint de VALIDAÇÃO (checker) — a decisão é obrigatória (SIM, NAO ou CORRIGIR). A edição/
    // reenvio do maker vive no endpoint editar().
    if (mobilidadeDto.getValidar() == null)
      throw IgrpResponseStatusException.badRequest("A decisão de validação (SIM, NAO ou CORRIGIR) é obrigatória.");

    // Validação DESTA mobilidade (INSERT tem precedência sobre UPDATE) — filtra pelo referenciaUuid =
    // uuid da mobilidade a validar, não por qualquer validação pendente do funcionário. Distingue
    // REGISTO (INSERT) de EDIÇÃO (UPDATE) para o passo de consolidação de tiprel mais abaixo.
    var validacaoInsert = funcionarioRules.getValidacaoPendenteByReferenciaUuid(mobUuid, TipoAcao.INSERT, Referencia.MOBILIDADE);
    var validacao = validacaoInsert
        .or(() -> funcionarioRules.getValidacaoPendenteByReferenciaUuid(mobUuid, TipoAcao.UPDATE, Referencia.MOBILIDADE))
        .orElse(null);

    // CORRIGIR (checker devolve ao maker): P -> C, SEM aplicar payload, validar ou tocar no vínculo.
    // Espelha o ciclo do registo de colaborador (ValidarRegistoColaboradorService): o maker corrige e
    // reenvia depois via editar() (C -> P). Guard: só uma mobilidade pendente (P) com validação pendente.
    if (EstadoValidacao.CORRIGIR.equals(mobilidadeDto.getValidar())) {
      if (mobilidade.getEstado() != Estado.P || validacao == null) {
        throw IgrpResponseStatusException.badRequest(
            "Só é possível devolver para correção uma mobilidade pendente de validação.");
      }
      mobilidade.setEstado(Estado.C);
      validacao.setEstado(Estado.C);
      validacaoEntityRepository.save(validacao);
      mobilidadeEntityRepository.save(mobilidade);
      LOGGER.info("[CORRIGIR] MOBILIDADE devolvida para correção (mobilidade={}).", mobUuid);
      return new SuccessResponseDTO(true, mobUuid.toString(),
          "Mobilidade devolvida para correção.", java.util.List.of());
    }

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    // SIM / NAO — só sobre uma mobilidade PENDENTE (P). Uma em correção (C) tem de ser reenviada pelo
    // maker (editar) antes de poder voltar à validação.
    if (mobilidade.getEstado() != Estado.P) {
      throw IgrpResponseStatusException.badRequest("Esta mobilidade não está pendente de validação.");
    }

    // Decisão SIM / NAO sobre a mobilidade pendente.
    {
      // Aplica eventuais edições do formulário antes de decidir a validação.
      updateMobilidade(mobilidade, mobilidadeDto);
      var estado = mobilidadeDto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      if (validacao != null) validacao.setEstado(estado);
      // Só o REGISTO (INSERT) muda o estado da mobilidade segundo a decisão e cria/troca
      // tipos_relacionamento. A validação de uma EDIÇÃO (UPDATE) NÃO mexe no tiprel — a alteração já
      // está in place e o tiprel atual referencia-a via MOB_ID.
      if (validacaoInsert.isPresent()) {
        // REGISTO: A (aprovado) ou I (rejeitado).
        mobilidade.setEstado(estado);
      } else if (validacao != null) {
        // EDIÇÃO: a mobilidade estava PENDENTE (P) com os dados já editados in place; validar tira-a
        // do pendente → volta a ACTIVA (A), quer aprovada quer rejeitada (o update foi feito "na
        // mesma"; sem snapshot não se revertem valores).
        mobilidade.setEstado(Estado.A);
      }

      if (estado.equals(Estado.A) && validacaoInsert.isPresent()) {
        // Consolidação: é AQUI (e só aqui) que o tipo_relacionamento é criado/trocado — a mesma
        // mecânica que antes estava no save(), agora executada apenas quando a mobilidade é aprovada.
        var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
        var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);

        // Backfill para registos gravados antes de MOB_ID existir: o "antes" é a mobilidade do
        // vínculo que ainda é o atual. A guarda evita auto-referência em registos do padrão antigo,
        // onde o tiprel atual já apontava para esta própria mobilidade.
        if (mobilidade.getMobId() == null
            && tipoRelacionamentoAtual.getMobId() != null
            && !java.util.Objects.equals(tipoRelacionamentoAtual.getMobId().getId(), mobilidade.getId())) {
          mobilidade.setMobId(tipoRelacionamentoAtual.getMobId());
        }

        // Opção A: data efetiva = data do pedido (mobilidade.data_inicio), não a data da validação,
        // para o processamento refletir quando a mobilidade realmente aconteceu. O vínculo antigo
        // fecha com data_fim = data efetiva - 1 (regra do analista: o relacionamento fechado termina
        // em inicio-1, contíguo com o novo). (A CK_TIPREL_PERIODO está DISABLED na BD, por isso não
        // impomos data_fim >= data_inicio aqui.)
        var dataEfetiva = mobilidade.getDataInicio();

        // Fecho do vínculo antigo: est_act_adm=0 + data_fim. O ESTADO mantém-se 'A' de propósito
        // (convenção do sistema: o "atual" é definido por est_act_adm=1; um vínculo fechado é
        // histórico e as vistas já o mostram como I quando data_fim < sysdate). Não passar a I aqui
        // sem alinhar a convenção de forma transversal (mobilidade, carreira, etc.).
        tipoRelacionamentoAtual.setEstActAdm(0);
        tipoRelacionamentoAtual.setDataFim(dataEfetiva.minusDays(1));

        // Doc: "um colaborador deve ter um único registo de mobilidade ativo" — a mobilidade anterior
        // passa a I com data_fim = data efetiva. Percorre TODAS as activas (não só a do vínculo atual)
        // para o invariante ficar garantido mesmo em dados herdados com mais do que uma activa.
        for (var ativa : mobilidadeEntityRepository.findAllByFunIdAndEstado(funcionario, Estado.A)) {
          if (java.util.Objects.equals(ativa.getId(), mobilidade.getId())) continue;
          ativa.setDataFim(dataEfetiva.minusDays(1));
          ativa.setEstado(Estado.I);
        }

        novoTipoRelacionamento.setEstActAdm(1);
        novoTipoRelacionamento.setMobId(mobilidade);
        novoTipoRelacionamento.setEstado(Estado.A);
        novoTipoRelacionamento.setDataInicio(dataEfetiva);
        novoTipoRelacionamento.setDataFim(null);
        // Spec: TIPO_SITUACAO = referente ao tipo de mobilidade. Lê-se da mobilidade já actualizada
        // (não do DTO) para o payload de validação não precisar de reenviar o tipo.
        var tipoMobilidade = mobilidade.getTipoSituacao();
        novoTipoRelacionamento.setTipoSituacao(tipoMobilidade);
        novoTipoRelacionamento.setReferente(Referencia.MOBILIDADE.name());
        novoTipoRelacionamento.setObs("MOBILIDADE-" + tipoMobilidade);
        entityManager.persist(novoTipoRelacionamento);
        entityManager.flush();

        // rem/pag passam do vínculo antigo para o novo — só na aprovação.
        tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tipoRelacionamentoAtual, novoTipoRelacionamento, java.util.List.of(), java.util.List.of());

        // Spec: REFERENTE='MOBILIDADE', DESCRICAO='Mobilidade do colaborador - '||nome, VALIDACAO_ID preenchido
        var nome = funcionario.getNome() != null ? funcionario.getNome() : "";
        ordemServicoWriteService.criar(funcionario, novoTipoRelacionamento, Referencia.MOBILIDADE.name(),
            validacao, "Mobilidade do colaborador - " + nome);
      }
      // Rejeição (NAO): a mobilidade e a validação seguem o estado definido acima; o vínculo atual
      // não é tocado. (Numa validação de EDIÇÃO/UPDATE nunca se cria tiprel.)

    }

    funcionarioEntityRepository.save(funcionario);
    var mensagem = EstadoValidacao.SIM.equals(mobilidadeDto.getValidar())
        ? "Mobilidade validada."
        : "Mobilidade rejeitada.";
    return new SuccessResponseDTO(true, mobilidade.getUuid().toString(), mensagem, java.util.List.of());
  }

  /**
   * EDITAR mobilidade (endpoint dedicado PUT .../mobilidades/{id}). Doc: "Editar uma mobilidade —
   * update registo (in place); o botão só fica visível se não tiver processamento" + "registo e
   * alteração passa por validação". Faz UPDATE in place na RH_T_MOBILIDADE + cria validação pendente
   * UPDATE + marca a mobilidade PENDENTE (P). NÃO toca no tipos_relacionamento — a alteração propaga
   * via MOB_ID; a mobilidade volta a A quando validada.
   */
  @Transactional
  public SuccessResponseDTO editar(EditarMobilidadeCommand command) {

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var mobilidadeDto = command.getMobilidade();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var mobilidade = mobilidadeEntityRepository.findByUuid(
        IdentificadorUnico.from(command.getMobilidadeId()).valor())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Mobilidade não encontrada."));

    // Registos inactivos/eliminados são histórico — não se editam (guard I/E comum a todo o dossiê).
    funcionarioRules.garantirEditavel(mobilidade.getEstado());
    // Guard de processamento: se já entrou em folha, editar alteraria dados já processados.
    if (mobilidadeProcessada(mobilidade)) {
      throw IgrpResponseStatusException.badRequest("Não é possível editar uma mobilidade que já tem processamento salarial.");
    }

    // 1) UPDATE in place — direção/secção/local/datas na RH_T_MOBILIDADE.
    updateMobilidade(mobilidade, mobilidadeDto);

    var mobUuid = mobilidade.getUuid();

    // 2a) Maker reenvia a correção: a mobilidade estava EM CORREÇÃO (C) porque o checker a devolveu.
    //     Aplicado o payload acima, volta à fila de validação (C -> P) REACTIVANDO a mesma validação
    //     que o checker deixou em C (INSERT tem precedência sobre UPDATE) — não cria uma nova. Espelha
    //     o C -> P do registo de colaborador. A correção é auditada (JaVers) contra essa validação.
    if (Estado.C.equals(mobilidade.getEstado())) {
      var validacao = funcionarioRules.getValidacaoByReferenciaUuid(mobUuid, Estado.C, TipoAcao.INSERT, Referencia.MOBILIDADE)
          .or(() -> funcionarioRules.getValidacaoByReferenciaUuid(mobUuid, Estado.C, TipoAcao.UPDATE, Referencia.MOBILIDADE))
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Esta mobilidade não está por corrigir."));
      validacao.setEstado(Estado.P);
      mobilidade.setEstado(Estado.P);
      try {
        ValidacaoAuditContext.set(validacao.getId(), validacao.getUuid(), "RH_T_MOBILIDADE");
        mobilidadeEntityRepository.save(mobilidade);
      } finally {
        ValidacaoAuditContext.clear();
      }
      funcionarioEntityRepository.save(funcionario);
      return new SuccessResponseDTO(true, mobUuid.toString(), "Correção reenviada para validação.", java.util.List.of());
    }

    // 2b) Vai para validação — cria validação pendente UPDATE + marca a mobilidade P (aparece como
    //    "por validar" na própria lista). Só para mobilidade já validada (A); se ainda P, a validação
    //    inicial (INSERT) cobre-a. Não duplica se já houver UPDATE pendente. NÃO cria/troca tiprel.
    if (Estado.A.equals(mobilidade.getEstado())
        && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.MOBILIDADE)) {
      var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.MOBILIDADE.name(), Estado.P);
      valid.setFunId(funcionario);
      valid.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid()));
      valid.setReferenciaId(mobilidade.getId());
      valid.setReferenciaUuid(mobilidade.getUuid());
      entityManager.persist(valid);
      mobilidade.setEstado(Estado.P);

      // Auditoria de alterações (JaVers): marca a validação corrente para o CommitPropertiesProvider
      // e força o commit auto-audit da mobilidade — o save() no repositório dispara o aspecto. O
      // JaVers calcula o diff antes/depois contra o seu próprio snapshot, pelo que é imune ao
      // auto-flush do Hibernate que apagava o loadedState. O holder é sempre limpo no finally.
      try {
        ValidacaoAuditContext.set(valid.getId(), valid.getUuid(), "RH_T_MOBILIDADE");
        mobilidadeEntityRepository.save(mobilidade);
      } finally {
        ValidacaoAuditContext.clear();
      }
    }

    funcionarioEntityRepository.save(funcionario);
    return new SuccessResponseDTO(true, mobilidade.getUuid().toString(), "Mobilidade actualizada.", java.util.List.of());
  }

}
