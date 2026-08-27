package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.AlterarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.commands.RegistarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeModalidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegimeWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegimeWriteService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final RegimeModalidadeEntityRepository regimeModalidadeEntityRepository;
  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;


  @Transactional
  public SuccessResponseDTO registar(RegistarRegimeTrabalhoCommand command) {

    var dto = command.getRegimetrabalho();
    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    // Spec (Dossiê "Alterar regime trabalho" + BASE DADOS): o regime é registado directamente
    // como Ativo (A), associado ao FUN_ID, sem validação e sem movimento no tipos_relacionamento.
    // Um colaborador pode ter mais do que um regime.
    var regime = new RegimeTrabalhoEntity();
    regime.setUuid(IdentificadorUnico.create().valor());
    regime.setFunId(funcionario);
    regime.setTipoRegime(ValidationUtil.trimToNull(dto.getTipoRegime()));
    regime.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoRegime()));
    regime.setDataInicio(dto.getDataInicio());
    regime.setDataFim(dto.getDataFim());
    regime.setEstado(Estado.A);
    regimeTrabalhoEntityRepository.save(regime);

    if (dto.getRegimeModalidade() != null) {
      var novasModalidades = dto.getRegimeModalidade().stream().map(mod -> {
        var modalidade = new RegimeModalidadeEntity();
        modalidade.setUuid(IdentificadorUnico.create().valor());
        modalidade.setModalidade(mod.getModalidade());
        modalidade.setDiasSemana(mod.getDiasSemana());
        modalidade.setNumHoras(mod.getNumeroHoras());
        modalidade.setEstado(Estado.A);
        modalidade.setRegimeId(regime);
        return modalidade;
      }).toList();
      regimeModalidadeEntityRepository.saveAll(novasModalidades);
    }

    return new SuccessResponseDTO(true, regime.getUuid().toString(), "Regime de trabalho registado.", List.of());
  }

  @Transactional
  public SuccessResponseDTO alterar(AlterarRegimeTrabalhoCommand command) {

    var dto = command.getRegimetrabalho();

    // Terceiro caminho da validação (SIM / NAO / CORRIGIR). O fluxo de correção ainda não está
    // implementado: por agora CORRIGIR é um NO-OP — regista no log e devolve 200 com mensagem, SEM
    // validar, actualizar ou mudar qualquer estado. Guard no topo para não tocar em nada.
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      LOGGER.info("[CORRIGIR] REGIME_TRABALHO (regime={}): opção 'Corrigir' ainda não implementada; nenhuma alteração aplicada.",
          command.getRegimeId());
      return new SuccessResponseDTO(false, null, ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO, List.of());
    }

    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var regime = regimeTrabalhoEntityRepository.findByUuid(UUID.fromString(command.getRegimeId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Regime não encontrado"));

    if (regime.getFunId() == null || !regime.getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Regime não pertence a este funcionário");

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(regime.getEstado());

    // Atualização do regime
    regime.setTipoRegime(ValidationUtil.trimToNull(dto.getTipoRegime()));
    regime.setTipoSituacao(ValidationUtil.trimToNull(dto.getTipoRegime()));
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

          regimeModalidadeEntityRepository.save(novo);
          existentes.add(novo);
        }
      }
    }

    // SOFT DELETE
    for (var rem : mapExistentes.values()) {
      rem.setEstado(Estado.I);
    }

    /********************* VALIDAÇÃO ************************/

    // Estado na edição (spec "Alterar regime trabalho"): dois caminhos que gravam RH_T_REGIME_TRAB.ESTADO:
    //  1) campo "validar" (radiolist SIM_NAO, oculto — só em modo validação): SIM→A, NAO→I;
    //  2) campo "estado" (SELECT, visível só na edição): grava directamente o código escolhido.
    // Se ambos vierem, "validar" tem precedência (modo validação). Sem nenhum, mantém o estado atual.
    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      regime.setEstado(estado);
    } else if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
      regime.setEstado(Estado.fromCodeOrThrow(dto.getEstado()));
    }

    regimeTrabalhoEntityRepository.save(regime);

    var mensagem = dto.getValidar() == null
        ? "Regime de trabalho alterado."
        : (EstadoValidacao.SIM.equals(dto.getValidar())
            ? "Regime de trabalho validado."
            : "Regime de trabalho rejeitado.");
    return new SuccessResponseDTO(true, regime.getUuid().toString(), mensagem, List.of());
  }

}
