package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarreiraMapper {


  private final EntityManager entityManager;


  public  CarreiraListDTO toDTO(CarreiraList projection) {
    if (projection == null) return null;

    var dto = new CarreiraListDTO();
    dto.setId(projection.getId());
    dto.setUuid(projection.getUuid());
    dto.setIdFuncionario(projection.getIdFuncionario());
    dto.setUuidFuncionario(projection.getUuidFuncionario());
    dto.setTipoCarreira(projection.getTipoCarreira());
    dto.setVinculo(projection.getVinculo());
    dto.setCarreira(projection.getCarreira());
    dto.setCargo(projection.getCargo());
    dto.setEscalao(projection.getEscalao());
    dto.setSalario(projection.getSalario());
    dto.setSituacaoLaboral(projection.getSituacaoLaboral());
    dto.setDataInicio(projection.getDataInicio());
    dto.setDataFim(projection.getDataFim());
    dto.setProcessamento(projection.getProcessamento());
    dto.setEstado(projection.getEstado() != null ? projection.getEstado() : null);
    dto.setEstadoDesc(projection.getEstado() != null ? Estado.fromCodeOrThrow(projection.getEstado()).getDescription() : null);

    return dto;
  }


  /*public  CarreiraListDTO toDTO(Carreira carreira) {
    if (carreira == null) return null;

    var dto = new CarreiraListDTO();
    dto.setId(carreira.getId());
    dto.setUuid(carreira.getUuid().toString());
    dto.setIdFuncionario(projection.getIdFuncionario());
    dto.setUuidFuncionario(projection.getUuidFuncionario());
    dto.setTipoCarreira(carreira.getTipoSituacao());
    dto.setVinculo(carreira.ge);
    dto.setCarreira(carreira.getCarrPccs() != null ? carreira.getCarrPccs().getNome() : null);
    dto.setCargo(carreira.getCargo()!=null ? carreira.getCargo().getNome() : null);
    dto.setEscalao(carreira.getEscalao()!=null ? carreira.getEscalao().getEscalao() : null);
    dto.setSalario(projection.getSalario());
    dto.setSituacaoLaboral(projection.getSituacaoLaboral());
    dto.setDataInicio(projection.getDataInicio());
    dto.setDataFim(projection.getDataFim());
    dto.setProcessamento(projection.getProcessamento());
    dto.setEstado(projection.getEstado() != null ? projection.getEstado() : null);
    dto.setEstadoDesc(projection.getEstado() != null ? Estado.fromCodeOrThrow(projection.getEstado()).getDescription() : null);

    return dto;
  }*/

  public CarreiraEntity toCarreira(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var ce = new CarreiraEntity();

    ce.setCargoId(dc.getCargoPosicaoId() != null
            ? entityManager.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId())
            : null);
    ce.setEscalaoId(dc.getEscalaoReferenciaId() != null
            ? entityManager.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId())
            : null);
    ce.setCategoriaId(dc.getCategoriaId() != null
            ? entityManager.getReference(ParamCategoriaEntity.class, dc.getCategoriaId())
            : null);
    ce.setCarrPccsId(dc.getCarreiraId() != null
            ? entityManager.getReference(ParamCarreiraEntity.class, dc.getCarreiraId())
            : null);

    ce.setSalario(dc.getSalario());
    ce.setFlgProcessa(1);
    ce.setTipoSituacao("NOVO_CONTRATO");
    ce.setObs("NOVO_CONTRATO");
    ce.setUuid(UuidCreator.getTimeOrderedEpoch());
    ce.setEstado(estado);
    return ce;
  }


  public void toUpdateEntity(CarreiraEntity carreira, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    carreira.setCargoId(entityManager.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    carreira.setEscalaoId(entityManager.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    carreira.setCategoriaId(entityManager.getReference(ParamCategoriaEntity.class, dc.getCategoriaId()));
    carreira.setCarrPccsId(entityManager.getReference(ParamCarreiraEntity.class, dc.getCarreiraId()));
    carreira.setSalario(dc.getSalario());
    carreira.setFlgProcessa(1);
    carreira.setTipoSituacao("NOVO_CONTRATO");
    carreira.setObs("NOVO_CONTRATO");
  }
}
