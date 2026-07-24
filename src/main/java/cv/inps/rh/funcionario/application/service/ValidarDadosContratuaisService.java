package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ValidarDadosContratuaisService {

  private final EntityManager entityManager;

  public void validar(DadosContratuaisReqDTO dc) {
    // Fluxos gerais (ex.: registo de colaborador): data de início não pode ser no passado.
    validar(dc, false);
  }

  /**
   * @param dataInicioAteHoje quando {@code true}, aplica a regra do <b>Novo Contrato</b> (DOSSIÊ,
   *     Gestão Contratual): "Data início não ser maior que sysdate" — i.e. a data de início não
   *     pode ser <b>futura</b> (aceita hoje ou passado). Quando {@code false}, mantém a regra dos
   *     restantes fluxos (não pode ser no passado).
   */
  public void validar(DadosContratuaisReqDTO dc, boolean dataInicioAteHoje) {
    // -----------------------------
    // OBRIGATÓRIOS BÁSICOS
    // -----------------------------
    if (dc.getTipoContratoId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato é obrigatório.");

    /*if (dc.getCargoPosicaoId() == null)
      throw IgrpResponseStatusException.badRequest("Cargo/posição é obrigatório.");*/

    if (dc.getDirecaoId() == null)
      throw IgrpResponseStatusException.badRequest("Direção é obrigatória.");

    /*if (dc.getSeccaoId() == null)
      throw IgrpResponseStatusException.badRequest("Seção é obrigatória.");*/

    if (dc.getLocalTrabalhoId() == null)
      throw IgrpResponseStatusException.badRequest("Local de trabalho é obrigatório.");


    // Moeda: se o utilizador nao indicar, assume CVE (Cabo Verde) — valor do dominio MOEDA.
    dc.setMoeda(ValidationUtil.trimToNull(dc.getMoeda()));
    if (dc.getMoeda() == null)
      dc.setMoeda("CVE");

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");

    if (dc.getTipoVinculoLaboralId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de vínculo laboral é obrigatório.");

    // -----------------------------
    // EXISTÊNCIA DAS REFERÊNCIAS (FK)
    // -----------------------------
    var paramContrato = entityManager.find(ParamContratoEntity.class, dc.getTipoContratoId());
    if (paramContrato == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato inválido: o valor indicado não existe.");

    if (entityManager.find(DirecaoEntity.class, dc.getDirecaoId()) == null)
      throw IgrpResponseStatusException.badRequest("Direção inválida: o valor indicado não existe.");

    if (entityManager.find(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()) == null)
      throw IgrpResponseStatusException.badRequest("Local de trabalho inválido: o valor indicado não existe.");

    if (dc.getSeccaoId() != null && entityManager.find(SecaoEntity.class, dc.getSeccaoId()) == null)
      throw IgrpResponseStatusException.badRequest("Seção inválida: o valor indicado não existe.");

    if (dc.getCargoPosicaoId() != null && entityManager.find(ParamCargoEntity.class, dc.getCargoPosicaoId()) == null)
      throw IgrpResponseStatusException.badRequest("Cargo/posição inválido: o valor indicado não existe.");

    if (dc.getSituacaoLaboralId() != null && entityManager.find(ParamSituacaoEntity.class, dc.getSituacaoLaboralId()) == null)
      throw IgrpResponseStatusException.badRequest("Situação laboral inválida: o valor indicado não existe.");

    // -----------------------------
    // REGRAS DE DATAS
    // -----------------------------
    var hoje = LocalDate.now(ZoneId.systemDefault());

    if (dataInicioAteHoje) {
      // Novo Contrato: início não pode ser futuro (DOSSIÊ: "Data início não ser maior que sysdate").
      if (dc.getDataInicio().isAfter(hoje))
        throw IgrpResponseStatusException.badRequest("A data de início não pode ser uma data no futuro.");
    } else if (dc.getDataInicio().isBefore(hoje)) {
      throw IgrpResponseStatusException.badRequest("A data de início não pode ser uma data no passado.");
    }

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("A Data de Início não pode ser posterior à Data de Fim.");

    // Contrato a termo (RH_T_PARAM_CONTRATO.PRAZO_OBRIGATORIO = 1, ex.: Contrato
    // Determinado/Projeto/Estágio) exige Data de Fim. Indeterminado / sem prazo
    // obrigatório (=0) aceita Data de Fim nula.
    if (Integer.valueOf(1).equals(paramContrato.getPrazoObrigatorio())
        && dc.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest(
          "Este tipo de contrato é a termo: a Data de Fim é obrigatória.");

    // Deriva a duração (meses) a partir das datas quando o frontend não a envia.
    // A Data de Fim é a fonte de verdade do prazo; a duração fica coerente com ela.
    if (dc.getDataFim() != null && dc.getDuracaoMeses() == null)
      dc.setDuracaoMeses((int) ChronoUnit.MONTHS.between(dc.getDataInicio(), dc.getDataFim()));

    // -----------------------------
    // OBRIGATÓRIOS POR TIPO DE VÍNCULO
    // -----------------------------
    var vinculo = entityManager.find(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId());
    if (vinculo == null)
      throw IgrpResponseStatusException.badRequest("Tipo de vínculo inválido.");

    // flgCarreira = 1 → carreira, categoria, escalão obrigatórios e salário automático
    if (vinculo.getFlgCarreira() != null && vinculo.getFlgCarreira() == 1) {

      if (dc.getCarreiraId() == null)
        throw IgrpResponseStatusException.badRequest("Carreira é obrigatória para este tipo de vínculo.");

      /*if (dc.getCategoriaId() == null)
        throw IgrpResponseStatusException.badRequest("Categoria é obrigatória para este tipo de vínculo.");*/

      if (dc.getEscalaoReferenciaId() == null)
        throw IgrpResponseStatusException.badRequest("Escalão é obrigatório para este tipo de vínculo.");

      // Salário automático
      var escalao = entityManager.find(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId());
      if (escalao == null)
        throw IgrpResponseStatusException.badRequest("Escalão inválido.");
      dc.setSalario(escalao.getValor());
    }

    // flgSalario = 1 → salário é obrigatório
    if (vinculo.getFlgSalario() != null && vinculo.getFlgSalario() == 1) {
      if (dc.getSalario() == null)
        throw IgrpResponseStatusException.badRequest("valor do salário é obrigatório para este tipo de vínculo.");
    }

    // -----------------------------
    // SUBSÍDIOS E ENCARGOS/DESCONTOS
    // -----------------------------
    if (dc.getEncargosDescontos() != null) {
      for (var encargo : dc.getEncargosDescontos()) {
        // Se o utilizador nao indicar datas no encargo, assume as datas do contrato.
        if (encargo.getDataInicio() == null) encargo.setDataInicio(dc.getDataInicio());
        if (encargo.getDataFim() == null) encargo.setDataFim(dc.getDataFim());
        if (encargo.getDataFim() != null && encargo.getDataInicio() != null
            && encargo.getDataInicio().isAfter(encargo.getDataFim())) {
          throw IgrpResponseStatusException.badRequest(
              "A Data de Início não pode ser posterior à Data de Fim no encargo/desconto.");
        }
      }
    }
  }

}
