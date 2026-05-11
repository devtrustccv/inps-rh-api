package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.RegimeListDTO;
import cv.inps.rh.funcionario.application.rules.RegimeRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegimeTrabalhoMapper {

  private final RegimeRules regimeRules;


  public RegimeListDTO toDTO(RegimeTrabalhoEntity regime) {
    if (regime == null) return null;

    RegimeListDTO dto = new RegimeListDTO();

    dto.setId(regime.getId());
    dto.setUuid(regime.getUuid().toString());

    dto.setIdFuncionario(regime.getFunId().getId());
    dto.setUuidFuncionario(regime.getFunId()!= null ? regime.getFunId().getUuid().toString() : null);

    dto.setTipoRegime(regime.getTipoRegime());
    dto.setDataInicio(DateFormatter.localDateToString(regime.getDataInicio()));
    dto.setDataFim(regime.getDataFim() != null ? regime.getDataFim().toString() : null);
    dto.setModalidade(regimeRules.getDiasSemanaAgrupados(regime));
    dto.setNumHoras(String.valueOf(regimeRules.getTotalHoras(regime)));

    if (regime.getEstado() != null) {
      dto.setEstado(regime.getEstado().getCode());
      dto.setEstadoDesc(regime.getEstado().getDescription());
    }

    return dto;
  }


  public RegimeTrabalhoEntity toRegime(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var re = new RegimeTrabalhoEntity();
    re.setTipoRegime(dc.getRegimeTrabalho());
    re.setTipoSituacao("NOVO_CONTRATO");
    re.setDataInicio(dc.getDataInicio());
    re.setDataFim(dc.getDataFim());
    re.setObs(null);
    re.setEstado(Estado.P);
    re.setUuid(UuidCreator.getTimeOrderedEpoch());
    re.setEstado(estado);
    return re;
  }

  public void toUpdateEntity(RegimeTrabalhoEntity regime, DadosContratuaisReqDTO dc) {
    if (dc == null) return ;
    regime.setTipoRegime(dc.getRegimeTrabalho());
    regime.setTipoSituacao("NOVO_CONTRATO");
    regime.setDataFim(dc.getDataFim());
    regime.setObs(null);
  }

}
