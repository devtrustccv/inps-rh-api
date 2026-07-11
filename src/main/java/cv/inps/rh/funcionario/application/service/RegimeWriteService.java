package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.AdicionarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeModalidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeModalidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegimeWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final RegimeModalidadeEntityRepository regimeModalidadeEntityRepository;
  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;


  @Transactional
  public RegimeTrabalhoDTO alterarRegimeTrabalho(AdicionarRegimeTrabalhoCommand command) {

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

    return dto;
  }

  @Transactional
  public RegimeTrabalhoDTO validar(ValidarRegimeTrabalhoCommand command) {

    var dto = command.getRegimetrabalho();
    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).valor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var regime = regimeTrabalhoEntityRepository.findByUuid(UUID.fromString(command.getRegimeId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Regime não encontrado"));

    if (regime.getFunId() == null || !regime.getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Regime não pertence a este funcionário");

    funcionarioRules.garantirEditavel(regime.getEstado());

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

    // Validação opcional: o mesmo PUT edita e, se vier o campo "validar", transiciona o estado.
    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      regime.setEstado(estado);
    }

    regimeTrabalhoEntityRepository.save(regime);

    return dto;
  }

}
