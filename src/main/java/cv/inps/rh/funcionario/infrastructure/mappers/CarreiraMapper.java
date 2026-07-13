package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.CarreiraListDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCategoriaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
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

    ce.setCargoId(ValidationUtil.ref(entityManager, ParamCargoEntity.class, dc.getCargoPosicaoId()));
    ce.setEscalaoId(ValidationUtil.ref(entityManager, ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    ce.setCarrPccsId(ValidationUtil.ref(entityManager, ParamCarreiraEntity.class, dc.getCarreiraId()));

    ce.setSalario(dc.getSalario());
    ce.setFlgProcessa(dc.getFlgProcessa() != null ? dc.getFlgProcessa() : 1);
    ce.setTipoSituacao(dc.getTipoCarreira() != null ? dc.getTipoCarreira() : "NOVO_CONTRATO");
    ce.setObs("CARREIRA");
    ce.setDataInicio(dc.getDataInicio());
    // Item 71: Data_Fim da carreira preenchida com a data fim do contrato
    ce.setDataFim(dc.getDataFim());
    ce.setUuid(UuidCreator.getTimeOrderedEpoch());
    ce.setEstado(estado);
    return ce;
  }


  public void toUpdateEntity(CarreiraEntity carreira, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    carreira.setCargoId(ValidationUtil.ref(entityManager, ParamCargoEntity.class, dc.getCargoPosicaoId()));
    carreira.setEscalaoId(ValidationUtil.ref(entityManager, ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    carreira.setCarrPccsId(ValidationUtil.ref(entityManager, ParamCarreiraEntity.class, dc.getCarreiraId()));
    carreira.setCategoriaId(ValidationUtil.ref(entityManager, ParamCategoriaEntity.class, dc.getCategoriaId()));
    carreira.setSalario(dc.getSalario());
    carreira.setFlgProcessa(dc.getFlgProcessa() != null ? dc.getFlgProcessa() : carreira.getFlgProcessa());
    if (dc.getTipoCarreira() != null) carreira.setTipoSituacao(dc.getTipoCarreira());
  }
}
