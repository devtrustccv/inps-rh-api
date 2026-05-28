package cv.inps.rh.processamento.domain.service.baixamedica;

import cv.inps.rh.processamento.application.dto.BaixaMedicaDetalheDTO;
import cv.inps.rh.processamento.application.queries.GetBaixaMedicaQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AbonosBeneficiosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaixaMedicaReadService {

    private final PedidoEntityRepository pedidoRepository;
    private final AbonosBeneficiosEntityRepository abonosRepository;
    private final FaltaEntityRepository faltaRepository;
    private final BaixaMedicaServiceWrite baixaMedicaServiceWrite;

    @Transactional(readOnly = true)
    public BaixaMedicaDetalheDTO getBaixaMedica(GetBaixaMedicaQuery query) {
        var pedido = pedidoRepository.findByUuid(UUID.fromString(query.getPedidoId()))
                .orElseThrow(() -> IgrpResponseStatusException.badRequest("Pedido não encontrado"));

        // Buscar abono activo associado ao colaborador deste pedido
        var faltas = faltaRepository.findAllByPedidoId(pedido);
        var tiprel = faltas.isEmpty() ? null : faltas.getFirst().getTiprelId();
        var funcionario = tiprel != null ? tiprel.getFunId() : null;

        // Buscar abono pelo funcionário e estado (match por fun_id)
        var abono = funcionario != null
                ? abonosRepository.findAll().stream()
                        .filter(a -> funcionario.getId().equals(a.getFunId() != null ? a.getFunId().getId() : null))
                        .findFirst().orElse(null)
                : null;

        var dto = new BaixaMedicaDetalheDTO();
        dto.setPedidoId(pedido.getId());
        dto.setPedidoUuid(pedido.getUuid() != null ? pedido.getUuid().toString() : null);
        dto.setEstado(pedido.getEstado());

        if (abono != null) {
            dto.setDataInicio(abono.getDataInicio());
            dto.setDataFim(abono.getDataFim());
            dto.setObservacao(abono.getObs());
            if (abono.getParamSitId() != null) {
                dto.setTipoLicencaId(abono.getParamSitId().getId());
                dto.setTipoLicencaNome(abono.getParamSitId().getNome());
            }
            if (abono.getParamSitDetId() != null) {
                dto.setMotivoId(abono.getParamSitDetId().getId());
                dto.setMotivoNome(abono.getParamSitDetId().getMotivo());
            }

            // Recalcular via procedure para mostrar os dados actualizados
            if (tiprel != null && abono.getParamSitId() != null
                    && abono.getDataInicio() != null && abono.getDataFim() != null) {
                var calculo = baixaMedicaServiceWrite.chamarProcedure(
                        tiprel.getId(),
                        abono.getDataInicio(),
                        abono.getDataFim(),
                        abono.getParamSitId().getId(),
                        null);
                dto.setCalculo(calculo);
            }
        }

        return dto;
    }
}
