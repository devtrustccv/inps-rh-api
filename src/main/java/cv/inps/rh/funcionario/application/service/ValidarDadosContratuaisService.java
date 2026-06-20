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

@Service
@RequiredArgsConstructor
public class ValidarDadosContratuaisService {

  private final EntityManager entityManager;

  public void validar(DadosContratuaisReqDTO dc) {
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


    dc.setMoeda(ValidationUtil.trimToNull(dc.getMoeda()));
    if (dc.getMoeda() == null)
      throw IgrpResponseStatusException.badRequest("Moeda é obrigatória.");

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");

    if (dc.getTipoVinculoLaboralId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de vínculo laboral é obrigatório.");

    // -----------------------------
    // EXISTÊNCIA DAS REFERÊNCIAS (FK)
    // -----------------------------
    if (entityManager.find(ParamContratoEntity.class, dc.getTipoContratoId()) == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato inválido: o valor indicado não existe.");

    if (entityManager.find(InstituicaoEntity.class, dc.getDirecaoId()) == null)
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

    if (dc.getDataInicio().isAfter(hoje))
      throw IgrpResponseStatusException.badRequest("A data de início não pode ser uma data futura.");

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("A Data de Início não pode ser posterior à Data de Fim.");

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
        if (encargo.getDataInicio() == null) {
          throw IgrpResponseStatusException.badRequest(
              "A Data de Início é obrigatória para cada encargo/desconto.");
        }
        if (encargo.getDataFim() != null && encargo.getDataInicio().isAfter(encargo.getDataFim())) {
          throw IgrpResponseStatusException.badRequest(
              "A Data de Início não pode ser posterior à Data de Fim no encargo/desconto.");
        }
      }
    }
  }

}
