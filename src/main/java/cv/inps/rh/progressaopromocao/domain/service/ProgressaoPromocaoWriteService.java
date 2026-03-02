package cv.inps.rh.progressaopromocao.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@AllArgsConstructor
@Service
public class ProgressaoPromocaoWriteService {

  private final EvolucaoCarreiraEntityRepository evolucaoCarreiraEntityRepository;
  private final OrdemServicoEntityRepository ordemServicoEntityRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  public void anexarOrdemServico(AnexarOrdemServicoRequestDTO request) {

    var ev = evolucaoCarreiraEntityRepository.findByUuidOrThrow(request.getEvolucaoCarreiraUuid());
    var funId = ev.getTiprel().getFunId();

    documentoEntityRepository.findByReferenciaNameAndReferenciaUuidAndReferenciaIdAndFunIdAndEstado(
        TableName.RH_T_EVOLUCAO_CARREIRA.name(),
        UUID.fromString(ev.getUuid()),
        ev.getId().toString(),
        funId,
        Estado.A
    ).ifPresentOrElse(obj -> {
          obj.setUrl(request.getOrdemServicoUrl());
          documentoEntityRepository.save(obj);
        },
        () -> {
          var document = new DocumentoEntity();
          document.setUuid(UuidCreator.getTimeOrderedEpoch());
          document.setReferenciaName(TableName.RH_T_EVOLUCAO_CARREIRA.name());
          document.setReferenciaId(ev.getId().toString());
          document.setReferenciaUuid(UUID.fromString(ev.getUuid()));
          document.setEstado(Estado.A);
          document.setFunId(funId);
          document.setUrl(request.getOrdemServicoUrl());
          documentoEntityRepository.save(document);
        });

    var os = new OrdemServicoEntity();
    os.setNuOrdem(request.getNumero());
    os.setDescricao(request.getDescricao());
    os.setReferente(request.getRequerente());
    os.setEstado(Estado.A);
    os.setUuid(UuidCreator.getTimeOrderedEpoch());
    os.setFunId(funId);
    var osUuid = ordemServicoEntityRepository.save(os).getUuid().toString();

    ev.setOrdemServicoId(osUuid);
    evolucaoCarreiraEntityRepository.save(ev);
  }
}
