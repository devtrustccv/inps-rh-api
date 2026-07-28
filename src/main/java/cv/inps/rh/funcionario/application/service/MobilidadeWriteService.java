package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.commands.ValidarMobilidadeCommand;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
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

  private final FuncionarioRules funcionarioRules;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final EntityManager entityManager;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper tipoRelRemPagHelper;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;

  @Transactional
  public MobilidadeDTO save(SaveMobilidadeCommand command) {

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

    return mobilidadeDto;

  }


  private MobilidadeEntity createMobilidade(MobilidadeDTO mobilidadeDTO,
                                            cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity funcionario,
                                            MobilidadeEntity mobilidadeAnterior){
     if (mobilidadeDTO == null) return null;

     if(mobilidadeDTO.getLocalTrabalhoDepois() == null && mobilidadeDTO.getSeccaoDepois() == null && mobilidadeDTO.getDirecaoDepois() == null){
       throw IgrpResponseStatusException.badRequest("Local de trabalho, seção e direção são obrigatórios");
     }

    var me = new MobilidadeEntity();
    me.setTipoSituacao(ValidationUtil.trimToNull(mobilidadeDTO.getTipoMobilidade()));
    me.setObs("MOBILIDADE");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setFunId(funcionario);
    me.setLocalTrabId(ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, mobilidadeDTO.getLocalTrabalhoDepois()));
    me.setSecaoId(ValidationUtil.ref(entityManager, SecaoEntity.class, mobilidadeDTO.getSeccaoDepois()));
    me.setInstidId(ValidationUtil.ref(entityManager, DirecaoEntity.class, mobilidadeDTO.getDirecaoDepois()));
    me.setDataInicio(mobilidadeDTO.getDataInicio());
    me.setDataFim(mobilidadeDTO.getDataFim());
    me.setEstado(Estado.P);
    me.setMobId(mobilidadeAnterior);
    return me;
  }

  private MobilidadeEntity updateMobilidade(MobilidadeEntity me ,MobilidadeDTO mobilidadeDTO){
    if (mobilidadeDTO == null) return null;
    me.setTipoSituacao(ValidationUtil.trimToNull(mobilidadeDTO.getTipoMobilidade()));
    me.setObs(me.getObs());
    me.setUuid(me.getUuid());
    var localTrabRef = ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, mobilidadeDTO.getLocalTrabalhoDepois());
    if (localTrabRef != null) me.setLocalTrabId(localTrabRef);

    var secaoRef = ValidationUtil.ref(entityManager, SecaoEntity.class, mobilidadeDTO.getSeccaoDepois());
    if (secaoRef != null) me.setSecaoId(secaoRef);

    var instidRef = ValidationUtil.ref(entityManager, DirecaoEntity.class, mobilidadeDTO.getDirecaoDepois());
    if (instidRef != null) me.setInstidId(instidRef);

    me.setDataInicio(mobilidadeDTO.getDataInicio());
    me.setDataFim(mobilidadeDTO.getDataFim()!=null ? mobilidadeDTO.getDataFim() : me.getDataFim());
    me.setEstado(me.getEstado());
    return me;
  }

  @Transactional
  public MobilidadeDTO validarMobilidade(ValidarMobilidadeCommand command){

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    // A mobilidade a validar é identificada pelo uuid (path {mobilidadeId}) — já NÃO se descobre via
    // tiprel.getMobId(), porque no registo não se cria/troca tiprel nenhum.
    var mobilidade = mobilidadeEntityRepository.findByUuid(
        IdentificadorUnico.from(command.getMobilidadeId()).valor())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Mobilidade não encontrada."));

    // Aplica eventuais edições do formulário à mobilidade pendente.
    updateMobilidade(mobilidade, mobilidadeDto);

    if (mobilidadeDto.getValidar() != null) {
      var estado = mobilidadeDto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      // A validação pendente pode ser INSERT (nova mobilidade) ou UPDATE (edição). Trata ambos,
      // senão a validação de uma edição ficava presa em P mesmo depois de aprovada.
      var validacao = funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)
          .or(() -> funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.MOBILIDADE))
          .orElse(null);
      if (validacao != null) validacao.setEstado(estado);
      mobilidade.setEstado(estado);

      if (estado.equals(Estado.A)) {
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
        // fecha com data_fim = mesma data. (A CK_TIPREL_PERIODO está DISABLED na BD, por isso não
        // impomos data_fim >= data_inicio aqui.)
        var dataEfetiva = mobilidade.getDataInicio();

        // Fecho do vínculo antigo: est_act_adm=0 + data_fim. O ESTADO mantém-se 'A' de propósito
        // (convenção do sistema: o "atual" é definido por est_act_adm=1; um vínculo fechado é
        // histórico e as vistas já o mostram como I quando data_fim < sysdate). Não passar a I aqui
        // sem alinhar a convenção de forma transversal (mobilidade, carreira, etc.).
        tipoRelacionamentoAtual.setEstActAdm(0);
        tipoRelacionamentoAtual.setDataFim(dataEfetiva);
        if (tipoRelacionamentoAtual.getMobId() != null) {
          tipoRelacionamentoAtual.getMobId().setDataFim(dataEfetiva);
          tipoRelacionamentoAtual.getMobId().setEstado(Estado.I);
        }

        novoTipoRelacionamento.setEstActAdm(1);
        novoTipoRelacionamento.setMobId(mobilidade);
        novoTipoRelacionamento.setEstado(Estado.A);
        novoTipoRelacionamento.setDataInicio(dataEfetiva);
        novoTipoRelacionamento.setDataFim(null);
        novoTipoRelacionamento.setTipoSituacao(ValidationUtil.trimToNull(mobilidadeDto.getTipoMobilidade()));
        novoTipoRelacionamento.setReferente(Referencia.MOBILIDADE.name());
        novoTipoRelacionamento.setObs("MOBILIDADE-" + ValidationUtil.trimToNull(mobilidadeDto.getTipoMobilidade()));
        entityManager.persist(novoTipoRelacionamento);
        entityManager.flush();

        // rem/pag passam do vínculo antigo para o novo — só na aprovação.
        tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tipoRelacionamentoAtual, novoTipoRelacionamento, java.util.List.of(), java.util.List.of());

        // Spec: REFERENTE='MOBILIDADE', DESCRICAO='Mobilidade do colaborador - '||nome, VALIDACAO_ID preenchido
        var nome = funcionario.getNome() != null ? funcionario.getNome() : "";
        ordemServicoWriteService.criar(funcionario, novoTipoRelacionamento, Referencia.MOBILIDADE.name(),
            validacao, "Mobilidade do colaborador - " + nome);
      }
      // Rejeição (NAO): mobilidade e validação ficam I; o vínculo atual NÃO foi tocado no registo,
      // logo continua atual — nada a reverter.

    } else {
      // Edição sem decisão de validação: atualiza a mobilidade pendente, mantém-na P e garante uma
      // validação UPDATE/MOBILIDADE pendente. NÃO toca no tipo_relacionamento (só o validar SIM o faz).
      mobilidade.setEstado(Estado.P);
      boolean jaPendente =
          funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.MOBILIDADE)
              || funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE);
      if (!jaPendente) {
        var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
        var valid = dadosContratuaisMapper.toValidacaoInsert(
            TipoAcao.UPDATE.name(), Referencia.MOBILIDADE.name(), Estado.P);
        valid.setFunId(funcionario);
        valid.setTiprelId(tipoRelacionamentoAtual);
        valid.setReferenciaId(mobilidade.getId());
        valid.setReferenciaUuid(mobilidade.getUuid());
        funcionario.getValidacoes().add(valid);
      }
    }

    funcionarioEntityRepository.save(funcionario);


    return mobilidadeDto;
  }

}
