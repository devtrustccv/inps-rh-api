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
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

  @Transactional
  public MobilidadeDTO save(SaveMobilidadeCommand command) {

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)){
      throw IgrpResponseStatusException.badRequest("Funcionário tem uma mobilidade pendente por validar");
    }

    var novaMobilidade = createMobilidade(mobilidadeDto, funcionario);

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // TODO(guard I/E temporariamente desativado):
    // if (tipoRelacionamentoAtual.getMobId() != null)
    //   funcionarioRules.garantirEditavel(tipoRelacionamentoAtual.getMobId().getEstado());
    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);

    // Caso de uso: no update do vínculo anterior, DATA_FIM = Data do registo (não dataInicio-1, que
    // fecharia o vínculo antes de começar e viola CK_TIPREL_PERIODO quando registo e mobilidade são
    // no mesmo dia).
    var dataRegisto = LocalDate.now();
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(dataRegisto);
    tipoRelacionamentoAtual.getMobId().setDataFim(dataRegisto);

    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setMobId(novaMobilidade);
    novoTipoRelacionamento.setEstado(Estado.P);
    // Caso de uso: novo vínculo DATA_INICIO = Data do registo, DATA_FIM = nulo.
    novoTipoRelacionamento.setDataInicio(dataRegisto);
    novoTipoRelacionamento.setDataFim(null);
    novoTipoRelacionamento.setTipoSituacao(ValidationUtil.trimToNull(mobilidadeDto.getTipoMobilidade()));
    // Spec: REFERENTE = 'MOBILIDADE', OBS = 'MOBILIDADE-'||tipo (não herdar do vínculo clonado)
    novoTipoRelacionamento.setReferente(Referencia.MOBILIDADE.name());
    novoTipoRelacionamento.setObs("MOBILIDADE-" + ValidationUtil.trimToNull(mobilidadeDto.getTipoMobilidade()));

    // Persist new entities directly so their IDs are assigned on the same references.
    // saveAndFlush(funcionario) uses em.merge(), which for transient children creates
    // a managed copy — the original reference keeps getId() == null.
    entityManager.persist(novaMobilidade);
    entityManager.persist(novoTipoRelacionamento);
    entityManager.flush();

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.MOBILIDADE.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    valid.setReferenciaId(novaMobilidade.getId());
    valid.setReferenciaUuid(novaMobilidade.getUuid());
    entityManager.persist(valid);

    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tipoRelacionamentoAtual, novoTipoRelacionamento, java.util.List.of(), java.util.List.of());

    return mobilidadeDto;

  }


  private MobilidadeEntity createMobilidade(MobilidadeDTO mobilidadeDTO, cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity funcionario){
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

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // TODO(guard I/E temporariamente desativado):
    // if (tipoRelacionamentoAtual.getMobId() != null)
    //   funcionarioRules.garantirEditavel(tipoRelacionamentoAtual.getMobId().getEstado());

    var mobilidade = updateMobilidade(tipoRelacionamentoAtual.getMobId(),mobilidadeDto);

    if(mobilidadeDto.getValidar()!=null) {
      var estado = mobilidadeDto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

       mobilidade.setEstado(estado);
       tipoRelacionamentoAtual.setEstado(estado);

      var validacao = funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.MOBILIDADE)
          .orElse(null);
      if (validacao != null) validacao.setEstado(estado);

      // Propagar o estado APENAS às remunerações/descontos associados a ESTE vínculo
      // (não a todas as definições do funcionário).
      var remuneracoes = funcionarioRules.getRemuneracoesAssociadosAtivos(tipoRelacionamentoAtual.getId());
      if (remuneracoes != null) remuneracoes.forEach(r -> { if (r != null) r.setEstado(estado); });
      var descontos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(tipoRelacionamentoAtual.getId());
      if (descontos != null) descontos.forEach(d -> { if (d != null) d.setEstado(estado); });

      if(estado.equals(Estado.A)){
        // Spec: REFERENTE='MOBILIDADE', DESCRICAO='Mobilidade do colaborador - '||nome, VALIDACAO_ID preenchido
        var nome = funcionario.getNome() != null ? funcionario.getNome() : "";
        ordemServicoWriteService.criar(funcionario, tipoRelacionamentoAtual, Referencia.MOBILIDADE.name(),
            validacao, "Mobilidade do colaborador - " + nome);
      }

    }

    funcionarioEntityRepository.save(funcionario);


    return mobilidadeDto;
  }

}
