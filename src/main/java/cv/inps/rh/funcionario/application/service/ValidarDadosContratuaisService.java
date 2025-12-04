package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
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
    else
      dc.setMoeda(dc.getMoeda().trim());

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");

    if (dc.getTipoVinculoLaboralId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de vínculo laboral é obrigatório.");

    // -----------------------------
    // REGRAS DE DATAS
    // -----------------------------
    var hoje = LocalDate.now(ZoneId.systemDefault());

    if (dc.getDataInicio().isAfter(hoje))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser maior que a data atual.");

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser superior à data fim.");

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

      if (dc.getCategoriaId() == null)
        throw IgrpResponseStatusException.badRequest("Categoria é obrigatória para este tipo de vínculo.");

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
        throw IgrpResponseStatusException.badRequest("Salário é obrigatório para este tipo de vínculo.");
    }
  }

}
