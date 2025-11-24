package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
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
    tipoRelacionamentoAtual.setEstado(Estado.P);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());
    tipoRelacionamentoAtual.getMobId().setDataFim(mobilidadeDto.getDataInicio().minusDays(1));

    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setMobId(novaMobilidade);

    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);
    funcionario.getMobilidades().add(novaMobilidade);



    var valid = dadosContratuaisMapper.toValidacaoInsert("MOBILIDADE", 1L, Estado.P); //todo resolve id later
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    funcionario.getValidacoes().add(valid);

    funcionarioEntityRepository.save(funcionario);


    return mobilidadeDto;

  }


  private MobilidadeEntity createMobilidade(MobilidadeDTO mobilidadeDTO){
     if (mobilidadeDTO == null) return null;
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


  public MobilidadeDTO validar(){
    return null;
  }

}
