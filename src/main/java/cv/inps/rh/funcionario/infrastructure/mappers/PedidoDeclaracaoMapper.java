package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;
import cv.inps.rh.shared.application.dto.PedidoDeclaracaoRowDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import org.springframework.stereotype.Component;

@Component
public class PedidoDeclaracaoMapper {

    public PedidoDeclaracaoRowDTO toDto(DeclaracaoEntity entity) {
        if (entity == null) {
            return null;
        }

        PedidoDeclaracaoRowDTO dto = new PedidoDeclaracaoRowDTO();
        dto.setId(entity.getId());
        dto.setTipoDeclaracao(entity.getTipoDeclaracao());
        dto.setEfeito(entity.getPedidoId().getTipoPedido()); // Assumindo que este campo existe
        dto.setEstadoPedido(entity.getEstado());

        if (entity.getPedidoId() != null) {
            dto.setDataPedido(entity.getDataPedido());
            dto.setEtapa(entity.getPedidoId().getEtapa());

            if (entity.getPedidoId().getFunId() != null) {
                dto.setNomeColaborador(entity.getPedidoId().getFunId().getNome());

                // Para Direção, Secção e Vínculo, precisamos da relação ativa do funcionário
                // Esta lógica pode ser complexa e pode precisar de uma busca adicional
                // ou de um DTO de projeção mais completo da query.
                // Por agora, vamos deixar como placeholder.

                // Exemplo simplificado (pode não funcionar diretamente):
                TiposRelacionamentoEntity relacao = entity.getPedidoId().getFunId().getTiposrelacionamentos().stream()
                        .filter(tr -> "1".equals(tr.getEstActAdm())) // Assumindo que '1' é ativo
                        .findFirst().orElse(null);

                if (relacao != null) {
                    if (relacao.getMobId() != null && relacao.getMobId().getInstidId() != null) {
                        dto.setDirecao(relacao.getMobId().getInstidId().getNome());
                    }
                    if (relacao.getMobId() != null && relacao.getMobId().getSecaoId() != null) {
                        dto.setSeccao(relacao.getMobId().getSecaoId().getNome());
                    }
                    if (relacao.getContrVinculoId() != null && relacao.getContrVinculoId().getVinculoId() != null) {
                        dto.setVinculo(relacao.getContrVinculoId().getVinculoId().getNome());
                    }
                }
            }
        }

        return dto;
    }

    public PedidoDeclaracaoResponseDTO toResponseDto(DeclaracaoEntity entity) {
        if (entity == null) {
            return null;
        }

        PedidoDeclaracaoResponseDTO dto = new PedidoDeclaracaoResponseDTO();

        // Dados da Declaração
        dto.setId(entity.getId());
        dto.setTipoDeclaracao(entity.getTipoDeclaracao());
        dto.setEfeito(entity.getPedidoId().getTipoPedido());
        dto.setEstadoPedido(entity.getEstado());
        dto.setFinalidade(entity.getFinalidade());
        dto.setEntidadeDestinado(entity.getEntidadeDestinado());
        dto.setObs(entity.getObs());

        // Dados da Análise
        dto.setDecisaoAnalise(entity.getDecisaoAnalise());
        dto.setObsAnalise(entity.getObsAnalise());

        // Dados da Validação
        dto.setValidar(entity.getDecisaoRh());
        dto.setEntregaPorEmail(entity.getEntrega());

        // Dados do Pedido e Funcionário
        if (entity.getPedidoId() != null) {
            dto.setDataPedido(entity.getDataPedido());
            dto.setEtapa(entity.getPedidoId().getEtapa());

            if (entity.getPedidoId().getFunId() != null) {
                FuncionarioEntity funcionario = entity.getPedidoId().getFunId();
                dto.setFunId(funcionario.getId());
                dto.setNomeColaborador(funcionario.getNome());

                // Lógica para buscar a relação ativa
                TiposRelacionamentoEntity relacao = funcionario.getTiposrelacionamentos().stream()
                        .filter(tr -> "1".equals(tr.getEstActAdm()))
                        .findFirst().orElse(null);

                if (relacao != null) {
                    if (relacao.getMobId() != null && relacao.getMobId().getInstidId() != null) {
                        dto.setDirecao(relacao.getMobId().getInstidId().getNome());
                    }
                    if (relacao.getMobId() != null && relacao.getMobId().getSecaoId() != null) {
                        dto.setSeccao(relacao.getMobId().getSecaoId().getNome());
                    }
                    if (relacao.getContrVinculoId() != null && relacao.getContrVinculoId().getVinculoId() != null) {
                        dto.setVinculo(relacao.getContrVinculoId().getVinculoId().getNome());
                    }
                }
            }
        }

        // Mapear Anexos (se o DocumentoMapper estiver disponível e injetado)
        // dto.setAnexos(documentoMapper.toAnexoRespDTOList(entity.getAnexos()));

        return dto;
    }
}
