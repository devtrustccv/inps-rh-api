package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubstituicaoProvider implements OrdemServicoProvider {

  private final OrdemServicoDados dados;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository substituicaoEntityRepository;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository domainEntityRepository;

  @Override
  public OrdemServico getTipo() {
    return OrdemServico.SUBSTITUICAO;
  }

  /**
   * B15 (QA 26/11): motivo e período vêm da substituição do colaborador (o SUBSTITUTO, dono do
   * dossiê) — antes eram fixos ("férias", hoje → hoje+15). Sem substituição registada, mantém o
   * comportamento anterior.
   */
  @Override
  public Map<String, Object> buildVariables(String funcionarioId) {

    var fun = dados.relacaoLaboral(funcionarioId);

    String motivo = "férias";
    LocalDate inicio = LocalDate.now();
    LocalDate fim = LocalDate.now().plusDays(15);
    try {
      var sub = substituicaoEntityRepository.findFirstBySubstitutoTiprelId_FunId_UuidAndEstadoInOrderByIdDesc(
          java.util.UUID.fromString(funcionarioId),
          java.util.List.of(cv.inps.rh.shared.application.constants.Estado.A, cv.inps.rh.shared.application.constants.Estado.P));
      if (sub.isPresent()) {
        var s = sub.get();
        if (s.getMotivo() != null) {
          var desc = domainEntityRepository.getActiveDomainByCode("MOTIVO_SUBSTITUICAO_MOB").get(s.getMotivo());
          motivo = (desc != null ? desc : s.getMotivo()).toLowerCase();
        }
        if (s.getDataInicio() != null) inicio = s.getDataInicio();
        if (s.getDataFim() != null) fim = s.getDataFim();
      }
    } catch (IllegalArgumentException ignored) {
      // funcionarioId não é um UUID: fica o texto por defeito
    }

    return OrdemServicoDados.vars(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "motivo", motivo,
        "dataInicio", DateFormatter.EXTENDED_DATE_PT.format(inicio),
        "dataFim", DateFormatter.EXTENDED_DATE_PT.format(fim)
    );
  }
}
