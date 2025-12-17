package cv.inps.rh.funcionario.application.service.documento;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.AnexoReqDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentoWriteService {

  private final DocumentoEntityRepository documentoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DocumentoMapper documentoMapper;

  public void saveOrdemServico(String funcionarioId, AnexoReqDTO anexo, MultipartFile ordemServico) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    // TODO 27/11/2025 20:08 not implemented

    var documentoEntity = documentoMapper.toEntity(anexo, Estado.A);
    documentoEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
    documentoEntity.setFunId(funcionario);
    documentoEntityRepository.save(documentoEntity);
  }

}
