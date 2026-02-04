package cv.inps.rh.funcionario.application.service.documento;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.AnexoReqDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.OrdemServicoPdfService;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentoWriteService {

  private final DocumentoEntityRepository documentoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DocumentoMapper documentoMapper;
  private final OrdemServicoPdfService ordemServicoPdfService;

  public String saveOrdemServico(String funcionarioId, AnexoReqDTO anexo) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    Map<String, Object> model = new HashMap<>();
    model.put("numeroOrdem", "015");
    model.put("ano", 2025);
    model.put("assunto", "Pedido de Licença sem Vencimento");
    model.put("periodoMeses", 2);
    model.put("periodoExtenso", "dois");
    model.put("cargo", "Coordenadora");
    model.put("nome", funcionario.getNome());
    model.put("categoria", "14 E");
    model.put("dataEfeito", "20 de maio de 2025");
    model.put("dataEmissao", "30 de abril de 2025");
    model.put("nomePresidente", "Carlos Lopes");

    var fileId = ordemServicoPdfService.generate(model);

    var dto = new AnexoReqDTO();
    dto.setDocumento(fileId);
    dto.setTipoDocumentoId(1L);

    var documentoEntity = documentoMapper.toEntity(
        dto,
        Estado.A,
        Referencia.ORDEM_SERVICO.name(),
        funcionario.getId(),
        funcionario.getUuid(),
        1L,
        funcionario);
    documentoEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
    documentoEntity.setFunId(funcionario);
    documentoEntityRepository.save(documentoEntity);
    return fileId;
  }

}
