package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubstituicaoWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SubstituicaoEntityRepository substituicaoEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  @Transactional
  public SubstituicaoDTO registrar(RegistarSubstituicaoCommand command) {

    var dto = command.getSubstituicao();

    if (dto.getColaboradorSubstituto() == null)
      throw IgrpResponseStatusException.badRequest("É obrigatório indicar o colaborador substituto.");

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituto não encontrado.")
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituído não encontrado.")
    );


    var substitutoTiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid());
    var substituidoTiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid());

    var substituicao = new SubstituicaoEntity();
    substituicao.setSubstitutoTiprelId(substitutoTiprel);
    substituicao.setSubstituidoTiprelId(substituidoTiprel);
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setMotivo(ValidationUtil.trimToNull(dto.getMotivoSubstituicao()));
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setUuid(IdentificadorUnico.create().valor());
    substituicao.setEstado(Estado.P);
    substituicaoEntityRepository.save(substituicao);

    // Caso de teste / item 50-51: a substituição só segue para VALIDAÇÃO quando existe diferença
    // salarial a favor do substituto (salário do substituto < salário do substituído). Nesse caso
    // regista-se a diferença em RH_T_DEF_REMUNERACOES (Tipo Movimento parametrizado no vínculo com
    // TIPO='REM_SUBSTITUICAO', OBS='Substituição') + RH_T_TIPREL_REM_PAG.
    var salarioSubstituto = substitutoTiprel.getSalario();
    var salarioSubstituido = substituidoTiprel.getSalario();
    boolean temDiferencaSalarial = salarioSubstituto != null && salarioSubstituido != null
        && salarioSubstituto.compareTo(salarioSubstituido) < 0;

    if (temDiferencaSalarial) {
      var diferenca = salarioSubstituido.subtract(salarioSubstituto);

      var tm = tipoMovimentoSubstituicao(substitutoTiprel);
      if (tm != null) {
        var defRem = definicaoRemuneracaoMapper.createRenumeracao(
            diferenca, tm, substituicao.getDataInicio(), substituicao.getDataFim(),
            funcionarioSubstituto, substitutoTiprel.getMoeda());
        defRem.setObs("Substituição");
        defRem.setEstado(Estado.P);
        definicaoRemuneracaoEntityRepository.save(defRem);

        var link = new TipoRelRemPagEntity();
        link.setTiprelId(substitutoTiprel);
        link.setRemId(defRem);
        tipoRelRemPagEntityRepository.save(link);
      }

      // Existe diferença → segue para validação
      var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.SUBSTITUICAO.name(), Estado.P);
      validacao.setFunId(funcionarioSubstituido);
      validacao.setTiprelId(substituicao.getSubstitutoTiprelId());
      validacao.setReferenciaId(substituicao.getId());
      validacao.setReferenciaUuid(substituicao.getUuid());
      funcionarioSubstituido.getValidacoes().add(validacao);
    }

    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;

  }

  @Transactional
  public SubstituicaoDTO validar(ValidarSubstituicaoCommand command) {
    var dto = command.getSubstituicao();

    var idSusbtituicao = IdentificadorUnico.from(command.getSubstituicaoId()).valor();

    if (dto.getColaboradorSubstituto() == null)
      throw IgrpResponseStatusException.badRequest("É obrigatório indicar o colaborador substituto.");

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituto não encontrado.")
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituído não encontrado.")
    );

    var substituicao = substituicaoEntityRepository.findByUuid(idSusbtituicao).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Substituição não encontrada.")
    );
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(substituicao.getEstado());
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setSubstitutoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid()));
    substituicao.setSubstituidoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()));

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      if(estado.equals(Estado.A)){
        ordemServicoWriteService.criar(
            funcionarioSubstituido,
            funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()),
            dto.getTipoOrdemServico());
      }

      substituicao.setEstado(estado);

      // Diferença salarial: o DEF_REMUNERACOES da diferença acompanha a decisão — P->A na aprovação,
      // P->I na rejeição. Identifica-se pelo Tipo de Movimento REM_SUBSTITUICAO do vínculo do
      // substituto (determinístico), não por texto de OBS. Sem isto a diferença ficaria pendente.
      var tmSubstituicao = tipoMovimentoSubstituicao(substituicao.getSubstitutoTiprelId());
      if (tmSubstituicao != null) {
        definicaoRemuneracaoEntityRepository
            .findByFunIdAndTmIdAndEstado(funcionarioSubstituto, tmSubstituicao, Estado.P)
            .forEach(r -> r.setEstado(estado));
      }

      funcionarioRules.getValidacaoPendente(funcionarioSubstituido.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO)
          .ifPresent(v -> v.setEstado(estado));

    }


    substituicaoEntityRepository.save(substituicao);
    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;
  }

  /**
   * Tipo de Movimento parametrizado no vínculo do substituto para a diferença salarial de
   * substituição (RH_T_PARAM_VINCULO_MOV, TIPO='REM_SUBSTITUICAO', estado A). Devolve null se o
   * tiprel/vínculo não estiver resolvido ou o vínculo não tiver esse movimento parametrizado.
   */
  private TipoMovimentoEntity tipoMovimentoSubstituicao(TiposRelacionamentoEntity substitutoTiprel) {
    Long vinculoId = (substitutoTiprel != null && substitutoTiprel.getContrVinculoId() != null
        && substitutoTiprel.getContrVinculoId().getVinculoId() != null)
        ? substitutoTiprel.getContrVinculoId().getVinculoId().getId() : null;
    if (vinculoId == null) return null;

    return paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, "REM_SUBSTITUICAO", Estado.A)
        .stream()
        .map(ParamVinculoMovimentoEntity::getTmId)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
