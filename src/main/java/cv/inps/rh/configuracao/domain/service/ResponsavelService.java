package cv.inps.rh.configuracao.domain.service;

import cv.inps.rh.configuracao.application.dto.AssociarResponsaveisRequestDTO;
import cv.inps.rh.configuracao.application.dto.ResponsaveisDirecaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.ResponsavelResponseDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Transactional
@Service
public class ResponsavelService {

  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SecaoEntityRepository secaoEntityRepository;
  private final InstituicaoEntityRepository instituicaoEntityRepository;

  public ResponsavelService(ResponsavelEntityRepository responsavelEntityRepository, FuncionarioEntityRepository funcionarioEntityRepository, SecaoEntityRepository secaoEntityRepository, InstituicaoEntityRepository instituicaoEntityRepository) {
    this.responsavelEntityRepository = responsavelEntityRepository;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.secaoEntityRepository = secaoEntityRepository;
    this.instituicaoEntityRepository = instituicaoEntityRepository;
  }

  public void saveResponsaveis(AssociarResponsaveisRequestDTO request) {
    request.getResponsaveis().forEach(row -> {
      var responsavel = ValidationUtil.isValidNumberId(row.getIdResponsavel()) ? responsavelEntityRepository.findByIdOrThrow(row.getIdResponsavel()) : new ResponsavelEntity();
      responsavel.setInstitId(instituicaoEntityRepository.findByIdOrThrow(row.getIdDirecao()));
      responsavel.setFunId(funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdFuncionario())));
      responsavel.setSecaoId(StringUtils.hasText(row.getIdSeccao()) ? secaoEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdSeccao())) : null);
      responsavel.setEmail(row.getEmail());
      responsavelEntityRepository.save(responsavel);
    });
  }

  public ResponsaveisDirecaoResponseDTO getResponsavelData(Long institutoId) {

    var savedData = responsavelEntityRepository.findAllByInstitId_id(institutoId);

    var arraySavedData = new ArrayList<ResponsavelResponseDTO>();

    var sectionIdsAlreadySaved = new HashSet<Long>();

    savedData.forEach(e -> {
      var obj = new ResponsavelResponseDTO();
      obj.setEmail(e.getEmail());
      obj.setIdResponsavel(e.getId());

      var section = e.getSecaoId();
      obj.setNomeSeccao(section.getNome());
      obj.setIdSeccao(section.getId().toString());
      sectionIdsAlreadySaved.add(section.getId());

      var direction = e.getInstitId();
      obj.setIdDirecao(direction.getId());
      obj.setNomeDirecao(direction.getNome());

      var funId = e.getFunId();
      obj.setIdFuncionario(funId.getId().toString());
      obj.setNomeFuncionario(funId.getNome());
      arraySavedData.add(obj);
    });

    var allDirectionSections = secaoEntityRepository.findAllByEstadoAndInstId_Id(Estado.A, institutoId);
    allDirectionSections.forEach(s -> {
      if (!sectionIdsAlreadySaved.contains(s.getId())) {
        sectionIdsAlreadySaved.add(s.getId());
        var obj = new ResponsavelResponseDTO();
        obj.setIdSeccao(s.getId().toString());
        obj.setNomeSeccao(s.getNome());
        arraySavedData.add(obj);
      }
    });

    return new ResponsaveisDirecaoResponseDTO(arraySavedData);
  }

}
