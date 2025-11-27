package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.commands.ValidarHistoricoLaboralCommand;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HistoricoLaboralWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final ContratoMapper contratoMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public ValidarNovoHistoricoLaboralDTO validar(ValidarHistoricoLaboralCommand command) {

    var dto = command.getValidarnovohistoricolaboral();

    var idFuncionario = IdentificadorUnico.from(command.getIdFuncionario()).getValor();

    var funcionario = funcionarioEntityRepository.findByUuid(idFuncionario)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Funcionário não encontrado"));

    var dc = dto.getDadosContratuais();

    validarDadosContratuais(dc);

    // se tipoAlteracao envolve mobilidade/novo vínculo que exige contrato: garantir contrato activo
    if (dto.getTipoAlteracao() != null && requiresActiveContract(dto.getTipoAlteracao())) {
      var trAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      if (trAtual == null || trAtual.getContratoId() == null || trAtual.getContratoId().getEstado() != Estado.A) {
        throw IgrpResponseStatusException.badRequest("Não existe contrato activo associado ao colaborador. Não é possível registar mobilidade.");
      }
    }

    // -------- Fechar (inactivar) relacionamento activo existente ----------
    var trAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
      trAtual.setDataFim(LocalDate.now());
      trAtual.setEstActAdm(0);
      trAtual.setEstado(Estado.I);


    var trNovo = dadosContratuaisMapper.clone(trAtual);
    trNovo.setEstado(Estado.P);
    trNovo.setDataInicio(dc.getDataInicio());
    trNovo.setEstActAdm(1);

    // inativa remunerações associadas (se estiverem ligadas ao funcionario e activas)
    funcionario.getDefinicoesRenumeracoes()
        .stream()
        .filter(r -> r != null && r.getEstado() == Estado.A)
        .forEach(r -> {
          r.setDataFim(LocalDate.now());
        });

    // inativa pagamentos/encargos associados activos
    funcionario.getDefinicoesPagamentos()
        .stream()
        .filter(p -> p != null && p.getEstado() == Estado.A)
        .forEach(p -> {
          p.setDataFim(LocalDate.now());
          p.setEstado(Estado.I);
        });


    var novoContrato = contratoMapper.toContrato(dc, Estado.P);
    if(novoContrato!=null) {
      novoContrato.setFunId(funcionario);
      funcionario.getContratos().add(novoContrato);
    }
     var novaCarreira = carreiraMapper.toCarreira(dc, Estado.P);
    if(novaCarreira!=null) {
      novaCarreira.setFunId(funcionario);
      funcionario.getCarreiras().add(novaCarreira);
    }

    var novoRegime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if(novoRegime!=null) {
      novoRegime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(novoRegime);
    }

     var novaMobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    if(novaMobilidade!=null) {
      novaMobilidade.setFunId(funcionario);
      funcionario.getMobilidades().add(novaMobilidade);
    }


    // todo finish later

    return dto;

  }

  /* ------------------ helpers ------------------ */

  private boolean requiresActiveContract(String tipoAlteracao) {
    if (tipoAlteracao == null) return false;
    String t = tipoAlteracao.toUpperCase();
    return t.contains("MOBIL") || t.contains("MUDAR_VINCULO") || t.contains("CONVERSAO_CONTRATO");
  }

  private void validarDadosContratuais(DadosContratuaisReqDTO dc) {

    if (dc.getTipoContratoId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato é obrigatório.");

    if (dc.getCargoPosicaoId() == null)
      throw IgrpResponseStatusException.badRequest("Cargo/posição é obrigatório.");

    if (dc.getDirecaoId() == null)
      throw IgrpResponseStatusException.badRequest("Direção é obrigatória.");

    if (dc.getSeccaoId() == null)
      throw IgrpResponseStatusException.badRequest("Seção é obrigatória.");

    if (dc.getLocalTrabalhoId() == null)
      throw IgrpResponseStatusException.badRequest("Local de trabalho é obrigatório.");

    if (dc.getPaisId() == null)
      throw IgrpResponseStatusException.badRequest("País é obrigatório.");

    if (dc.getIlhaId() == null)
      throw IgrpResponseStatusException.badRequest("Ilha é obrigatória.");

    if (dc.getMoeda() == null || dc.getMoeda().isBlank())
      throw IgrpResponseStatusException.badRequest("Moeda é obrigatória.");

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");

    var hoje = LocalDate.now();
    if (dc.getDataInicio().isAfter(hoje))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser maior que a data actual.");

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser superior à data fim.");

    if (dc.getTipoVinculoLaboralId() != null) {
      var vinculo = entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId());
      if (vinculo.getFlgCarreira() != null && vinculo.getFlgCarreira() == 1) {
        if (dc.getCarreiraId() == null)
          throw IgrpResponseStatusException.badRequest("Carreira é obrigatória para este tipo de vínculo.");
        if (dc.getCategoriaId() == null)
          throw IgrpResponseStatusException.badRequest("Categoria é obrigatória para este tipo de vínculo.");
        if (dc.getEscalaoReferenciaId() == null)
          throw IgrpResponseStatusException.badRequest("Escalão é obrigatório para este tipo de vínculo.");
        var escalao = entityManager.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId());
        if (escalao != null && escalao.getValor() != null) dc.setSalario(escalao.getValor());
      }
      if (vinculo.getFlgSalario() != null && vinculo.getFlgSalario() == 1) {
        if (dc.getSalario() == null)
          throw IgrpResponseStatusException.badRequest("Salário é obrigatório para este tipo de vínculo.");
      }
    }
  }


  }






