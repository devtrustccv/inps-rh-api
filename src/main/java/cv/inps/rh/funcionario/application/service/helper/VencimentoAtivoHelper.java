package cv.inps.rh.funcionario.application.service.helper;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Salário que o colaborador recebe hoje: o vencimento ativo (SAL) do contrato em RH_T_DEF_REMUNERACOES,
 * e não o RH_T_TIPOS_RELACIONAMENTO.SALARIO gravado na contratação — esse fica desatualizado depois de um
 * aumento, e não reflete um vencimento fechado (pedido do analista para o cálculo da substituição).
 */
@Service
@RequiredArgsConstructor
public class VencimentoAtivoHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(VencimentoAtivoHelper.class);

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;

  /**
   * Vencimento ativo hoje do contrato. Sem vencimento ativo dá 400 (antes contava como sem salário e a
   * operação seguia sem aviso). Com mais do que um, usa o mais recente e deixa aviso no log — é dado a
   * corrigir (ex.: vencimento do contrato anterior que não foi fechado).
   *
   * @param papel quem é o colaborador na operação, para a mensagem (ex.: "substituto")
   */
  public BigDecimal vencimentoAtual(TiposRelacionamentoEntity tiprel, String papel) {
    var valores = definicaoRemuneracaoEntityRepository.vencimentosAtivosHoje(tiprel.getId());
    var nome = tiprel.getFunId() != null ? tiprel.getFunId().getNome() : ("contrato " + tiprel.getId());
    if (valores.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "O colaborador %s (%s) não tem vencimento ativo.".formatted(papel, nome));
    if (valores.size() > 1)
      LOGGER.warn("Contrato {} ({}) tem {} vencimentos ativos {}; a usar o mais recente ({}).",
          tiprel.getId(), nome, valores.size(), valores, valores.getFirst());
    return valores.getFirst();
  }
}
