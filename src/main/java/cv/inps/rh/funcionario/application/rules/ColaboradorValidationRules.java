package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ColaboradorValidationRules {

  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public void validarDadosPessoais(DadosPessoaisReqDTO dp, UUID uuidExistente) {
    validarDocumentoUnico(dp, uuidExistente);
    validarNifUnico(dp.getNif(), uuidExistente);
    validarCamposObrigatorios(dp);
  }

  private void validarDocumentoUnico(DadosPessoaisReqDTO dp, UUID uuidExistente) {
    boolean duplicado = (uuidExistente == null)
        ? funcionarioEntityRepository.existsByTipoDocumentoId_idAndNumDocumento(
            dp.getTipoDocumentoId(), dp.getNumDocumento())
        : funcionarioEntityRepository.existsByTipoDocumentoId_IdAndNumDocumentoAndUuidNot(
            dp.getTipoDocumentoId(), dp.getNumDocumento(), uuidExistente);

    if (duplicado) {
      throw IgrpResponseStatusException.conflict(
          "Já existe um colaborador registado com este número de documento de identificação. Verifique os dados introduzidos.");
    }
  }

  private void validarNifUnico(Long nif, UUID uuidExistente) {
    if (nif == null) return;

    boolean duplicado = (uuidExistente == null)
        ? funcionarioEntityRepository.existsByNifAndEstadoNot(nif, Estado.E)
        : funcionarioEntityRepository.existsByNifAndUuidNotAndEstadoNot(nif, uuidExistente, Estado.E);

    if (duplicado) {
      throw IgrpResponseStatusException.conflict(
          "Já existe um colaborador registado com o NIF informado. Verifique os dados introduzidos.");
    }
  }

  private void validarCamposObrigatorios(DadosPessoaisReqDTO dp) {
    if (!StringUtils.hasText(dp.getNomePai())) {
      throw IgrpResponseStatusException.badRequest("Nome do Pai é obrigatório.");
    }
    if (!StringUtils.hasText(dp.getNacionalidade())) {
      throw IgrpResponseStatusException.badRequest("Nacionalidade é obrigatória.");
    }
    if (dp.getNaturalidadeId() == null) {
      throw IgrpResponseStatusException.badRequest("Naturalidade é obrigatória.");
    }
  }

  public void validarHabilitacoesLiterarias(List<HabilitacaoLiterariaReqDTO> habilitacoes) {
    if (habilitacoes == null) return;
    for (var h : habilitacoes) {
      if (h.getPaisId() == null) {
        throw IgrpResponseStatusException.badRequest("O campo País é obrigatório para cada habilitação literária.");
      }
      if (!StringUtils.hasText(h.getGrauAcademico())) {
        throw IgrpResponseStatusException.badRequest("O campo Grau Académico é obrigatório para cada habilitação literária.");
      }
    }
  }

  public void verificarDuplicidadeFamiliares(List<AgregadoDependenteReqDTO> novos,
                                              List<FamiliarEntity> existentes) {
    if (novos == null) return;
    var docsExistentes = new HashSet<String>();
    if (existentes != null) {
      for (var f : existentes) {
        if (f.getEstado() != Estado.E && StringUtils.hasText(f.getNumDocumento())) {
          docsExistentes.add(f.getNumDocumento().trim().toUpperCase());
        }
      }
    }
    var docsNovos = new HashSet<String>();
    for (var dto : novos) {
      if (dto.getId() != null) continue;
      if (!StringUtils.hasText(dto.getNumDocumento())) continue;
      String doc = dto.getNumDocumento().trim().toUpperCase();
      if (docsExistentes.contains(doc) || !docsNovos.add(doc)) {
        throw IgrpResponseStatusException.conflict(
            "O familiar com documento '" + dto.getNumDocumento() + "' já se encontra registado no agregado deste colaborador.");
      }
    }
  }
}