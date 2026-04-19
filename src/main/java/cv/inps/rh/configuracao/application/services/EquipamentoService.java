package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.EquipamentoListRequestDTO;
import cv.inps.rh.configuracao.application.dto.EquipamentoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EquipamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EquipamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamLocalTrabEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.UpsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class EquipamentoService {

  private final ParamLocalTrabEntityRepository localRepository;
  private final EquipamentoEntityRepository equipamentoEntityRepository;
  private final UpsEntityRepository upsEntityRepository;

  public void save(String localId, EquipamentoListRequestDTO dto) {

    var local = localRepository.findByUuidOrThrow(UUID.fromString(localId));

    var ups = local.getUpsId();

    var data = new ArrayList<EquipamentoEntity>();

    for (var obj : dto.getEquipamentos()) {

      final EquipamentoEntity equipment;
      if (StringUtils.hasText(obj.getId())) {
        equipment = equipamentoEntityRepository.findByUuidOrThrow(UUID.fromString(obj.getId()));
      } else {
        equipment = new EquipamentoEntity();
        equipment.setUuid(UuidCreator.getTimeOrderedEpoch());
        equipment.setEstado(Estado.A);
      }
      equipment.setIdEquipamento(obj.getIdEquipamento());
      equipment.setLocal(obj.getDescricaoLocal());
      equipment.setIpAddress(obj.getIpAddress());
      equipment.setTipo(obj.getTipo());
      equipment.setPicagem(obj.getPicagem());
      equipment.setIdUps(local.getUpsId());
      equipment.setTpMovimento(obj.getDescricaoTipoMovimento());
      equipment.setTpMovimentoDesc(obj.getTipoMovimento());
      data.add(equipment);
    }

    equipamentoEntityRepository.saveAll(data);
  }

  public EquipamentoListRequestDTO getEquipmentsByLocalId(String localId) {

    var local = localRepository.findByUuidOrThrow(UUID.fromString(localId));

    var data = equipamentoEntityRepository.findAllByIdUps_idAndEstado(local.getUpsId().getId(), Estado.A);

    var response = new ArrayList<EquipamentoRequestDTO>();

    for (var obj : data) {
      var equipment = new EquipamentoRequestDTO();
      equipment.setId(obj.getUuid().toString());
      equipment.setIdEquipamento(obj.getIdEquipamento());
      equipment.setDescricaoLocal(obj.getLocal());
      equipment.setIpAddress(obj.getIpAddress());
      equipment.setTipo(obj.getTipo());
      equipment.setPicagem(obj.getPicagem());
      equipment.setTipoMovimento(obj.getTpMovimento());
      equipment.setDescricaoTipoMovimento(obj.getTpMovimentoDesc());
      response.add(equipment);
    }

    return new EquipamentoListRequestDTO(response);
  }
}

