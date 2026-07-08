package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.RenovacaoDetalheDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoRespDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
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
    dto.setAtual(contratoMapper.toRenovacaoContratoRespDTO(contrato));
    dto.setTemRenovacaoPendente(false);

    // A renovação pendente é o histórico em estado P com versão > 1 (a versão 1 é o contrato inicial,
    // não uma renovação).
    contratoHistoricoEntityRepository
        .findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(contrato.getId(), Estado.P)
        .filter(h -> h.getVersao() != null && h.getVersao() > 1)
        .ifPresent(h -> {
          var renovacao = new RenovarContratoRespDTO();
          if (contrato.getTpContratoId() != null) {
            renovacao.setTipoContratoId(contrato.getTpContratoId().getId());
            renovacao.setTipoContratoDesc(contrato.getTpContratoId().getNome());
          }
          if (contrato.getVinculoId() != null) {
            renovacao.setTipoVinculoId(contrato.getVinculoId().getId());
            renovacao.setTipoVinculoDesc(contrato.getVinculoId().getNome());
          }
          renovacao.setDataInicio(h.getDataInicio());
          renovacao.setDataFim(h.getDataFim());
          renovacao.setDuracaoMeses(h.getDuracao());
          dto.setRenovacao(renovacao);
          dto.setTemRenovacaoPendente(true);
        });

    return dto;
  }
}
