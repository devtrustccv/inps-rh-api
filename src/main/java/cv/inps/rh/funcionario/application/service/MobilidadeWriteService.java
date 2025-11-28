package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.commands.ValidarMobilidadeCommand;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
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

  @Transactional
  public MobilidadeDTO save(SaveMobilidadeCommand command) {

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

    if (funcionarioRules.temValidacaoPendente(funcionario, "INSERT", "MOBILIDADE")){
      throw IgrpResponseStatusException.badRequest("Funcionário tem uma mobilidade pendente por validar");
    }

    var novaMobilidade = createMobilidade(mobilidadeDto);

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);

    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());
    tipoRelacionamentoAtual.getMobId().setDataFim(mobilidadeDto.getDataInicio().minusDays(1));

    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setMobId(novaMobilidade);
    novoTipoRelacionamento.setEstado(Estado.P);
    novoTipoRelacionamento.setTipoSituacao(mobilidadeDto.getTipoMobilidade());

    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);
    funcionario.getMobilidades().add(novaMobilidade);


    // Cria validação agora que os IDs existem
    var valid = dadosContratuaisMapper.toValidacaoInsert("INSERT","MOBILIDADE", Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    funcionario.getValidacoes().add(valid);

    // Salva validação
    funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(novaMobilidade.getId());
          validacaoEntityRepository.save(e);
        });


    return mobilidadeDto;

  }


  private MobilidadeEntity createMobilidade(MobilidadeDTO mobilidadeDTO){
     if (mobilidadeDTO == null) return null;

     if(mobilidadeDTO.getLocalTrabalhoDepois() == null || mobilidadeDTO.getSeccaoDepois() == null || mobilidadeDTO.getDirecaoDepois() == null){
       throw IgrpResponseStatusException.badRequest("Local de trabalho, seção e direção são obrigatórios");
     }

    var me = new MobilidadeEntity();
    me.setTipoSituacao(mobilidadeDTO.getTipoMobilidade());
    me.setObs("MOBILIDADE");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setLocalTrabId(entityManager.getReference(ParamLocalTrabEntity.class, mobilidadeDTO.getLocalTrabalhoDepois()));
    me.setSecaoId(entityManager.getReference(SecaoEntity.class, mobilidadeDTO.getSeccaoDepois()));
    me.setInstidId(entityManager.getReference(InstituicaoEntity.class, mobilidadeDTO.getDirecaoDepois()));
    me.setDataInicio(mobilidadeDTO.getDataInicio());
    me.setDataFim(mobilidadeDTO.getDataFim());
    me.setEstado(Estado.P);
    return me;
  }

  private MobilidadeEntity updateMobilidade(MobilidadeEntity me ,MobilidadeDTO mobilidadeDTO){
    if (mobilidadeDTO == null) return null;
    me.setTipoSituacao(mobilidadeDTO.getTipoMobilidade());
    me.setObs(me.getObs());
    me.setUuid(me.getUuid());
    me.setLocalTrabId(mobilidadeDTO.getLocalTrabalhoDepois()!=null ?
        entityManager.getReference(ParamLocalTrabEntity.class, mobilidadeDTO.getLocalTrabalhoDepois()): me.getLocalTrabId());

    me.setSecaoId(mobilidadeDTO.getSeccaoDepois()!=null ?
        entityManager.getReference(SecaoEntity.class, mobilidadeDTO.getSeccaoDepois()): me.getSecaoId());

    me.setInstidId(mobilidadeDTO.getDirecaoDepois()!=null ?
        entityManager.getReference(InstituicaoEntity.class, mobilidadeDTO.getDirecaoDepois()): me.getInstidId());

    me.setDataInicio(mobilidadeDTO.getDataInicio());
    me.setDataFim(mobilidadeDTO.getDataFim()!=null ? mobilidadeDTO.getDataFim() : me.getDataFim());
    me.setEstado(me.getEstado());
    return me;
  }



  public MobilidadeDTO validarMobilidade(ValidarMobilidadeCommand command){

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var mobilidadeDto = command.getMobilidade();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);

    var mobilidade = updateMobilidade(tipoRelacionamentoAtual.getMobId(),mobilidadeDto);

    if(mobilidadeDto.getValidar()!=null) {
      var estado = mobilidadeDto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

       mobilidade.setEstado(estado);
       tipoRelacionamentoAtual.setEstado(estado);

      funcionario.getValidacoes().stream()
          .filter(v -> v.getEstado() == Estado.P)
          .filter(v -> "MOBILIDADE".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.setEstado(estado));

      if(estado.equals(Estado.I)){
        var remuneracoes = funcionario.getDefinicoesRenumeracoes();
        if (remuneracoes != null) remuneracoes.forEach(r -> { if (r != null) r.setEstado(estado); });

        var descontos = funcionario.getDefinicoesPagamentos();
        if (descontos != null) descontos.forEach(d -> { if (d != null) d.setEstado(estado); });
      }

      if(estado.equals(Estado.A)){
        OrdemServicoEntity ordemServicoEntity = new OrdemServicoEntity();
        ordemServicoEntity.setFunId(funcionario);
        ordemServicoEntity.setTiprelId(tipoRelacionamentoAtual);
        ordemServicoEntity.setReferente("MOBILIDADE");
        ordemServicoEntity.setDescricao("Mobilidae do colaborador - "+funcionario.getNome());
        ordemServicoEntity.setNuOrdem("1"); // todo fix later
        ordemServicoEntity.setEstado(Estado.A);
        funcionario.getOrdemServicos().add(ordemServicoEntity);
      }

    }

    funcionarioEntityRepository.save(funcionario);


    return mobilidadeDto;
  }

}
