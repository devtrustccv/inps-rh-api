package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.RenovacaoDetalheDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoRespDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoHistoricoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RenovacaoContratoReadService {

  private final ContratoEntityRepository contratoEntityRepository;
  private final ContratoHistoricoEntityRepository contratoHistoricoEntityRepository;
  private final ContratoMapper contratoMapper;

  /**
   * Devolve os dados de renovação de um contrato: o estado ATUAL (datas em vigor) e, se existir,
   * a RENOVAÇÃO pendente (o que foi enviado para validação — histórico estado P, versão &gt; 1).
   */
  @Transactional(readOnly = true)
  public RenovacaoDetalheDTO getDetalhe(String contratoId) {
    var uuid = IdentificadorUnico.from(contratoId).valor();
    var contrato = contratoEntityRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Contrato não encontrado: " + contratoId));

    var dto = new RenovacaoDetalheDTO();

    // O "atual" deve refletir o registo de histórico atualmente ativo (EST_ACT_ADM = 1), não as colunas
    // da entidade base RH_T_CONTRATO_VINCULO — essas só são gravadas na criação do contrato e nunca são
    // atualizadas quando uma renovação é validada.
    dto.setAtual(contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstActAdmOrderByVersaoDesc(contrato.getId(), 1)
        .map(h -> {
          var atual = new RenovarContratoRespDTO();
          preencherTipoEVinculo(atual, contrato);
          atual.setDataInicio(h.getDataInicio());
          atual.setDataFim(h.getDataFim());
          atual.setDuracaoMeses(h.getDuracao());
          return atual;
        })
        .orElseGet(() -> contratoMapper.toRenovacaoContratoRespDTO(contrato)));
    dto.setTemRenovacaoPendente(false);

    // A renovação pendente é o histórico em estado P com versão > 1 (a versão 1 é o contrato inicial,
    // não uma renovação).
    contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), Estado.P)
        .filter(h -> h.getVersao() != null && h.getVersao() > 1)
        .ifPresent(h -> {
          var renovacao = new RenovarContratoRespDTO();
          preencherTipoEVinculo(renovacao, contrato);
          renovacao.setDataInicio(h.getDataInicio());
          renovacao.setDataFim(h.getDataFim());
          renovacao.setDuracaoMeses(h.getDuracao());
          dto.setRenovacao(renovacao);
          dto.setTemRenovacaoPendente(true);
        });

    return dto;
  }

  private void preencherTipoEVinculo(RenovarContratoRespDTO resp, ContratoEntity contrato) {
    if (contrato.getTpContratoId() != null) {
      resp.setTipoContratoId(contrato.getTpContratoId().getId());
      resp.setTipoContratoDesc(contrato.getTpContratoId().getNome());
    }
    if (contrato.getVinculoId() != null) {
      resp.setTipoVinculoId(contrato.getVinculoId().getId());
      resp.setTipoVinculoDesc(contrato.getVinculoId().getNome());
    }
  }
}
