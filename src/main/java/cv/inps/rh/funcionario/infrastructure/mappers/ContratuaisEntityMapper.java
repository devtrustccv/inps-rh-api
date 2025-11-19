package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ContratuaisEntityMapper {

  @PersistenceContext
  private EntityManager em;



  public TiposRelacionamentoEntity toRelacionamento(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var tr = new TiposRelacionamentoEntity();
    tr.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    tr.setInstitId(em.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
    tr.setVinculoId(em.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    tr.setSeccaoId(em.getReference(SecaoEntity.class, dc.getSeccaoId()));
    tr.setCategoriaId(em.getReference(ParamCategoriaEntity.class, dc.getCategoriaId()));
    tr.setEscalaoId(em.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    tr.setCarrPccId(em.getReference(ParamCarreiraEntity.class, dc.getCarreiraId()));
    tr.setSalario(dc.getSalario());
    tr.setMoeda(dc.getMoeda());
    tr.setRegime(dc.getRegimeTrabalho());
    tr.setTipoSituacao("NOVO_CONTRATO");
    tr.setFlgProcessa("S");
    tr.setObs("NOVO_CONTRATO");
    tr.setDataInicio(dc.getDataInicio());
    tr.setDataFim(null);
    tr.setDataInicioContrato(dc.getDataInicio());
    tr.setDataFimContrato(dc.getDataFim());
    tr.setLocTrabId(em.getReference(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    tr.setTipoContratoId(em.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    tr.setReferente("REGISTO_COLABORADOR");
    tr.setTpContrato("INICIAL");
    tr.setUuid(UuidCreator.getTimeOrderedEpoch());
    tr.setEstado(estado);
    return tr;
  }

  public void toUpdateRelacionamento(TiposRelacionamentoEntity tr, DadosContratuaisReqDTO dc) {
    if (dc == null) return ;
    tr.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    tr.setInstitId(em.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
    tr.setVinculoId(em.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    tr.setSeccaoId(em.getReference(SecaoEntity.class, dc.getSeccaoId()));
    tr.setCategoriaId(em.getReference(ParamCategoriaEntity.class, dc.getCategoriaId()));
    tr.setEscalaoId(em.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    tr.setCarrPccId(em.getReference(ParamCarreiraEntity.class, dc.getCarreiraId()));
    tr.setSalario(dc.getSalario());
    tr.setMoeda(dc.getMoeda());
    tr.setRegime(dc.getRegimeTrabalho());
    tr.setTipoSituacao("NOVO_CONTRATO");
    tr.setObs("NOVO_CONTRATO");
    tr.setDataInicio(dc.getDataInicio());
    tr.setDataFim(null);
    tr.setDataInicioContrato(dc.getDataInicio());
    tr.setDataFimContrato(dc.getDataFim());
    tr.setLocTrabId(em.getReference(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    tr.setTipoContratoId(em.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    tr.setReferente("REGISTO_COLABORADOR");
    tr.setTpContrato("INICIAL");

  }


  public ValidacaoEntity toValidacaoInsert(String referenciaName, Long funId, Estado estado) {
    var v = new ValidacaoEntity();
    v.setTipoAccao("INSERT");
    v.setReferenciaName(referenciaName);
    v.setReferenciaId(funId);
    v.setEstado(estado);
    v.setObs(null);
    v.setUuid(UuidCreator.getTimeOrderedEpoch());
    return v;
  }

  public SituacaoLaboralEntity toSituacaoLaboralInicial(DadosContratuaisReqDTO dc, ParamSitLaboralEntity param, Estado estado) {
    var sl = new SituacaoLaboralEntity();
    sl.setSituacaoLaboralId(param);
    sl.setMotivoSitLab(null);
    sl.setDataInicio(dc.getDataInicio());
    sl.setDataFim(null);
    sl.setEstado(estado);
    sl.setObs("NOVO_CONTRATO");
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    return sl;
  }





}
