package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.funcionario.application.dto.ContactoReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContactoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioRules {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final ContactoEntityRepository contactoEntityRepository;


  public boolean temValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository.existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(),
        referenciaName.name());
  }

  /**
   * Guard de escrita: impede alterar/validar um registo que esteja Inactivo (I) ou Eliminado (E).
   * Lança 400 com mensagem clara para o cliente. Chamar depois de carregar o registo alvo, antes de mutar.
   */
  public void garantirEditavel(Estado estado) {
    if (estado == Estado.I || estado == Estado.E) {
      throw IgrpResponseStatusException.badRequest(
          "Este registo está %s e não pode ser alterado."
              .formatted(estado == Estado.E ? "eliminado" : "inactivo"));
    }
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoAtual(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid)
        .orElseThrow(()-> IgrpResponseStatusException.badRequest("Funcionario sem tipo de relacionamento atual"));
  }


  public ContratoEntity getContratoComMaiorVersao(UUID funUuid) {
    return contratoEntityRepository.findTopByFunId_UuidOrderByVersaoDesc(funUuid);
  }


  public ContratoEntity getPrimeiroContrato(UUID funUuid) {
    return contratoEntityRepository.findPrimeiroContratoFuncionario(funUuid);
  }


  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(UUID funId, UUID contratoId) {
    if (funId == null || contratoId == null) return null;
    return tiposRelacionamentoEntityRepository.findByFunUuidAndContratoUuid(funId, contratoId);
  }


  public List<TipoRelRemPagEntity> getRemuneracoesPagamentosAssociados(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelId_Id(tipoRelacionamentoId);


  }
    public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociados(Long tipoRelacionamentoId) {

      return tipoRelRemPagEntityRepository
          .findByTiprelId_Id(tipoRelacionamentoId)
          .stream()
          .map(TipoRelRemPagEntity::getRemId)
          .filter(Objects::nonNull)
          //.distinct()
          .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociados(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelId_Id(tipoRelacionamentoId)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }


  public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosAtivos(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
        .stream()
        .map(TipoRelRemPagEntity::getRemId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }

  public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosPendentes(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.P)
        .stream()
        .map(TipoRelRemPagEntity::getRemId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosAtivos(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosPendentes(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.P)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

  }



  public Optional<ValidacaoEntity> getValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {

    return validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(), referenciaName.name());
  }

  public Optional<ValidacaoEntity> getValidacaoPendenteByReferenciaUuid(UUID referenciaUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository
        .findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(referenciaUuid, Estado.P, tipoAccao.name(), referenciaName.name());
  }

  public List<String> validarContactosDuplicados(List<ContactoReqDTO> contactos, UUID funUuid) {
    if (contactos == null || contactos.isEmpty()) return List.of();
    List<String> alertas = new ArrayList<>();
    for (var c : contactos) {
      if (c.getContacto() == null || c.getContacto().isBlank()) continue;
      boolean duplicado = (funUuid == null)
          ? contactoEntityRepository.existsByContactoAndEstadoNot(c.getContacto(), Estado.E)
          : contactoEntityRepository.existsByContactoAndFunId_UuidNotAndEstadoNot(c.getContacto(), funUuid, Estado.E);
      if (duplicado) {
        alertas.add("O contacto informado já está associado a outro colaborador: " + c.getContacto());
      }
    }
    return alertas;
  }


}
