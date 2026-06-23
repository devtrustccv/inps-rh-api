package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisReqDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.HabilitacaoLiterariaReqDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoMovimentoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ColaboradorValidationRules {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final EntityManager entityManager;

  public void validarDadosPessoais(DadosPessoaisReqDTO dp, UUID uuidExistente) {
    validarDocumentoUnico(dp, uuidExistente);
    validarNif(dp.getNif(), uuidExistente);
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

  private static final int NIF_LENGTH = 9;

  private void validarNif(Long nif, UUID uuidExistente) {
    if (nif == null || nif <= 0) {
      throw IgrpResponseStatusException.badRequest("O NIF é obrigatório e deve ser um número positivo.");
    }
    if (String.valueOf(nif).length() != NIF_LENGTH) {
      throw IgrpResponseStatusException.badRequest("O NIF deve ter exactamente " + NIF_LENGTH + " dígitos.");
    }

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

    if (dp.getTipoDocumentoId() != null
        && entityManager.find(TipoDocumentoEntity.class, dp.getTipoDocumentoId()) == null) {
      throw IgrpResponseStatusException.badRequest("Tipo de documento inválido: o valor indicado não existe.");
    }
    if (entityManager.find(GeografiaEntity.class, dp.getNaturalidadeId()) == null) {
      throw IgrpResponseStatusException.badRequest("Naturalidade inválida: o valor indicado não existe.");
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

  public void validarEncargosDescontosDuplicados(List<EncargosDescontosReqDTO> encargos) {
    if (CollectionUtils.isEmpty(encargos)) return;
    var seen = new HashSet<Long>();
    for (var e : encargos) {
      if (e.getTipoEncargoId() != null && !seen.add(e.getTipoEncargoId())) {
        var descricao = tipoMovimentoEntityRepository.findById(e.getTipoEncargoId())
            .map(TipoMovimentoEntity::getDescricao).orElse("Desconhecido");
        throw IgrpResponseStatusException.conflict(
            "O encargo/desconto '" + descricao + "' foi adicionado mais do que uma vez.");
      }
    }
  }

  public void validarSubsidiosDuplicados(List<SubsidioReqDTO> subsidios) {
    if (CollectionUtils.isEmpty(subsidios)) return;
    var seen = new HashSet<Long>();
    for (var s : subsidios) {
      if (s.getTipoSubsidioId() != null && !seen.add(s.getTipoSubsidioId())) {
        var descricao = tipoMovimentoEntityRepository.findById(s.getTipoSubsidioId())
            .map(TipoMovimentoEntity::getDescricao).orElse("Desconhecido");
        throw IgrpResponseStatusException.conflict(
            "O subsídio/remuneração '" + descricao + "' foi adicionado mais do que uma vez.");
      }
    }
  }

  public Set<Long> getTipoMovimentoIdsDePagamentos(List<DefPagamentoEntity> pagamentos) {
    if (CollectionUtils.isEmpty(pagamentos)) return Set.of();
    return pagamentos.stream()
        .filter(p -> p.getTmId() != null)
        .map(p -> p.getTmId().getId())
        .collect(Collectors.toSet());
  }

  public Set<Long> getTipoMovimentoIdsDeRemuneracoes(List<DefinicaoRemuneracaoEntity> remuneracoes) {
    if (CollectionUtils.isEmpty(remuneracoes)) return Set.of();
    return remuneracoes.stream()
        .filter(r -> r.getTmId() != null)
        .map(r -> r.getTmId().getId())
        .collect(Collectors.toSet());
  }
}