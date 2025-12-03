package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.AdicionarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegimeWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final EntityManager entityManager;
  private final ValidacaoEntityRepository validacaoEntityRepository;


  @Transactional
  public RegimeTrabalhoDTO adicionarRegimeTrabalho(AdicionarRegimeTrabalhoCommand command) {

    // Carregar dados do comando
    var dto = command.getRegimetrabalho();
    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    // Buscar funcionário
    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    // Desativar relacionamento atual
    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    tipoRelacionamentoAtual.setEstActAdm(0);

    // Criar entidade RegimeTrabalho
    var regimeTrabalho = new RegimeTrabalhoEntity();
    regimeTrabalho.setFunId(funcionario);
    regimeTrabalho.setTipoRegime(dto.getTipoRegime());
    regimeTrabalho.setDataInicio(dto.getDataInicio());
    regimeTrabalho.setDataFim(dto.getDataFim());
    regimeTrabalho.setEstado(Estado.P);
    regimeTrabalho.setUuid(IdentificadorUnico.create().valor());

    // Criar novo relacionamento
    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setRegimeId(regimeTrabalho);
    novoTipoRelacionamento.setDataInicio(dto.getDataInicio());
    novoTipoRelacionamento.setTipoSituacao("MUDANCA_REGIME");
    novoTipoRelacionamento.setReferente("REGIME");
    novoTipoRelacionamento.setObs("REGIME - " + dto.getTipoRegime());
    novoTipoRelacionamento.setEstado(Estado.P);

    // Associar ao funcionário
    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);
    funcionario.getRegimesTrabalhos().add(regimeTrabalho);

    // Criar modalidades
    if (dto.getRegimeModalidade() != null) {
      for (var mod : dto.getRegimeModalidade()) {
        var modalidade = new RegimeModalidadeEntity();
        modalidade.setModalidade(mod.getModalidade());
        modalidade.setDiasSemana(mod.getDiasSemana());
        modalidade.setNumHoras(mod.getNumeroHoras());
        modalidade.setUuid(IdentificadorUnico.create().valor());
        modalidade.setRegimeId(regimeTrabalho);
        regimeTrabalho.getModalidades().add(modalidade);
      }
    }


    // Criar validação
    var validacao = dadosContratuaisMapper.toValidacaoInsert("INSERT", "REGIME", Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(novoTipoRelacionamento);
    funcionario.getValidacoes().add(validacao);

    // Salvar tudo (cascade)
    funcionarioEntityRepository.saveAndFlush(funcionario);


    validacaoEntityRepository.findById(validacao.getId())
        .ifPresent(e -> {
          e.setReferenciaId(regimeTrabalho.getId());
          validacaoEntityRepository.save(e);
        });

    return dto;
  }

  @Transactional
  public RegimeTrabalhoDTO validar(ValidarRegimeTrabalhoCommand command) {

    var dto = command.getRegimetrabalho();
    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    var regime = tipoRelacionamentoAtual.getRegimeId();

    if (regime == null) {
      throw IgrpResponseStatusException.badRequest("Funcionário não possui regime associado");
    }

    // Atualização simples do regime
    regime.setTipoRegime(dto.getTipoRegime());
    regime.setDataInicio(dto.getDataInicio());
    regime.setDataFim(dto.getDataFim());

    /********************* SINCRONIZAÇÃO DAS MODALIDADES ************************/

    var existentes = regime.getModalidades();
    var recebidos = dto.getRegimeModalidade();

    var mapExistentes = existentes.stream()
        .collect(Collectors.toMap(RegimeModalidadeEntity::getId, e -> e));

    if (recebidos != null) {
      for (var modDto : recebidos) {

        if (modDto.getId() != null) {

          var existente = mapExistentes.get(modDto.getId());
          if (existente != null) {
            existente.setModalidade(modDto.getModalidade());
            existente.setDiasSemana(modDto.getDiasSemana());
            existente.setNumHoras(modDto.getNumeroHoras());
            existente.setEstado(Estado.A); // garante ativo
            mapExistentes.remove(modDto.getId());
          }

        } else {

          var novo = new RegimeModalidadeEntity();
          novo.setUuid(IdentificadorUnico.create().valor());
          novo.setModalidade(modDto.getModalidade());
          novo.setDiasSemana(modDto.getDiasSemana());
          novo.setNumHoras(modDto.getNumeroHoras());
          novo.setRegimeId(regime);
          novo.setEstado(Estado.A);

          existentes.add(novo);
        }
      }
    }

    // SOFT DELETE
    for (var rem : mapExistentes.values()) {
      rem.setEstado(Estado.I);
    }

    /********************* VALIDAÇÃO ************************/

    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      tipoRelacionamentoAtual.setEstado(estado);
      regime.setEstado(estado);

      funcionario.getValidacoes().stream()
          .filter(v -> v.getEstado() == Estado.P)
          .filter(v -> "REGIME".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.setEstado(estado));
    }

    funcionarioEntityRepository.save(funcionario);

    return dto;
  }

}
