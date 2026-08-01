package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.dto.RegularizacaoContaRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegularizacaoSdoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AbonosBeneficiosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegularizacaoSdoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegularizacaoService {

  private final RegularizacaoSdoEntityRepository regularizacaoRepository;
  private final ProcessamentoFuncionarioRepository processamentoSalarialRepository;
  private final AbonosBeneficiosEntityRepository abonosBeneficiosRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  @Transactional
  public List<RegularizacaoContaRequestDTO> getByFunId(String funUuid) {
    return regularizacaoRepository.findRegularizacoesByFunId(
        UUID.fromString(funUuid),
        Estado.P.name()
    );
  }

  @Transactional
  public List<RegularizacaoContaRequestDTO> create(List<RegularizacaoContaRequestDTO> request) {

    var savedList = new ArrayList<RegularizacaoSdoEntity>();

    for (var dto : request) {

      var procFun = processamentoSalarialRepository.findByIdOrThrow(dto.getProcessamentoFuncionarioId());
      var tiprel = procFun.getTiprel();
      var fun = tiprel.getFunId();
      var regularizacaoUUID = UuidCreator.getTimeOrderedEpoch();

      var entity = new RegularizacaoSdoEntity();
      entity.setMesReferente(dto.getMesReferencia());
      entity.setSdoRecebido(dto.getSdoRecebido());
      entity.setValorRetroativoSalario(dto.getRetroativoSalario());
      entity.setValorRetroativoSdo(dto.getRetroativoSdo());
      entity.setProcFun(procFun);
      entity.setAbonoBeneficio(abonosBeneficiosRepository.findByIdOrThrow(dto.getAbonoBeneficioId()));
      entity.setEstado(Estado.P.name());
      entity.setUuid(regularizacaoUUID.toString());
      entity = regularizacaoRepository.save(entity);
      savedList.add(entity);

      var validation = new ValidacaoEntity();
      validation.setTipoAccao(TipoAcao.INSERT.name());
      validation.setReferenciaName(Referencia.REGULARIZACAO.name());
      validation.setReferenciaId(entity.getId());
      validation.setReferenciaUuid(regularizacaoUUID);
      validation.setFunId(fun);
      validation.setTiprelId(tiprel);
      validation.setEstado(Estado.P);
      validation.setUuid(UuidCreator.getTimeOrderedEpoch());
      validacaoEntityRepository.save(validation);
    }

    return savedList
        .stream()
        .map(entity -> {
          var dto = new RegularizacaoContaRequestDTO();
          dto.setMesReferencia(entity.getMesReferente());
          dto.setSdoRecebido(entity.getSdoRecebido());
          dto.setRetroativoSalario(entity.getValorRetroativoSalario());
          dto.setRetroativoSdo(entity.getValorRetroativoSdo());
          dto.setUuidRegularizacao(entity.getUuid());
          return dto;
        })
        .toList();
  }

  @Transactional
  public List<RegularizacaoContaRequestDTO> update(List<RegularizacaoContaRequestDTO> request) {

    var savedList = new ArrayList<RegularizacaoSdoEntity>();

    for (var dto : request) {
      var entity = regularizacaoRepository.findByUuid(dto.getUuidRegularizacao()).orElseThrow();
      entity.setMesReferente(dto.getMesReferencia());
      entity.setSdoRecebido(dto.getSdoRecebido());
      entity.setValorRetroativoSalario(dto.getRetroativoSalario());
      entity.setValorRetroativoSdo(dto.getRetroativoSdo());
      entity.setAbonoBeneficio(abonosBeneficiosRepository.findByIdOrThrow(dto.getAbonoBeneficioId()));
      savedList.add(regularizacaoRepository.save(entity));
    }

    return mapToRegularizacaoContaRequestDTO(savedList);
  }

  @Transactional
  public List<RegularizacaoContaRequestDTO> validate(String validation, List<RegularizacaoContaRequestDTO> request) {

    if (!Arrays.stream(EstadoValidacao.values()).map(EstadoValidacao::name).toList().contains(validation))
      throw IgrpResponseStatusException.badRequest("Invalid validation flag: %s".formatted(validation));

    var status = validation.equals(EstadoValidacao.SIM.name()) ? Estado.A : Estado.I;

    var savedList = new ArrayList<RegularizacaoSdoEntity>();
    var regularizationUuids = new ArrayList<String>();

    for (var dto : request) {
      regularizationUuids.add(dto.getUuidRegularizacao());
      var entity = regularizacaoRepository.findByUuid(dto.getUuidRegularizacao()).orElseThrow();
      entity.setEstado(status.name());
      entity.setMesReferente(dto.getMesReferencia());
      entity.setSdoRecebido(dto.getSdoRecebido());
      entity.setValorRetroativoSalario(dto.getRetroativoSalario());
      entity.setAbonoBeneficio(abonosBeneficiosRepository.findByIdOrThrow(dto.getAbonoBeneficioId()));
      entity.setValorRetroativoSdo(dto.getRetroativoSdo());
      savedList.add(regularizacaoRepository.save(entity));
    }

    validacaoEntityRepository.updateValidationsForRegularization(
        status,
        TipoAcao.INSERT.name(),
        Referencia.REGULARIZACAO.name(),
        regularizationUuids,
        Estado.P
    );

    // TODO 01/08/2026 18:26 update other tables here

    return mapToRegularizacaoContaRequestDTO(savedList);
  }

  private List<RegularizacaoContaRequestDTO> mapToRegularizacaoContaRequestDTO(List<RegularizacaoSdoEntity> data) {
    return data.stream()
        .map(entity -> {
          var dto = new RegularizacaoContaRequestDTO();
          dto.setValorLiquido(entity.getProcFun().getTotLiquido());
          dto.setSubsidiofiscalRecebido(entity.getProcFun().getTotRemunCollect());
          dto.setProcessamentoFuncionarioId(entity.getProcFun().getId());
          dto.setAbonoBeneficioId(entity.getAbonoBeneficio().getId());
          dto.setEstado(entity.getEstado());
          dto.setMesReferencia(entity.getMesReferente());
          dto.setSdoRecebido(entity.getSdoRecebido());
          dto.setRetroativoSalario(entity.getValorRetroativoSalario());
          dto.setRetroativoSdo(entity.getValorRetroativoSdo());
          dto.setUuidRegularizacao(entity.getUuid());
          return dto;
        })
        .toList();
  }

}
