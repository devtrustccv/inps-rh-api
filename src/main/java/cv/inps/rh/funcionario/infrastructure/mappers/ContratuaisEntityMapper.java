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

  public ContratoEntity toContrato(DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    var c = new ContratoEntity();
    c.setEstado(Estado.P);
    c.setDataInicio(dc.getDataInicio());
    c.setDataFim(dc.getDataFim());
    c.setDuracao(dc.getDuracaoMeses());
    c.setTpContrato("NOVO_CONTRATO");
    c.setSituacaoLaboral("INICIO");
    c.setObs("NOVO_CONTRATO");
    c.setTpContratoId(em.getReference(ParamContratoEntity.class, dc.getTipoContratoId()));
    c.setVinculoId(em.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId()));
    return c;
  }

  public TiposRelacionamentoEntity toRelacionamento(DadosContratuaisReqDTO dc) {
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
    return tr;
  }

  public CarreiraEntity toCarreira(DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    var ce = new CarreiraEntity();
    ce.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    ce.setEscalaoId(em.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId()));
    ce.setCategoriaId(em.getReference(ParamCategoriaEntity.class, dc.getCategoriaId()));
    ce.setCarrPccsId(em.getReference(ParamCarreiraEntity.class, dc.getCarreiraId()));
    ce.setSalario(dc.getSalario());
    ce.setFlgProcessa(1);
    ce.setTipoSituacao("NOVO_CONTRATO");
    ce.setEstado(Estado.P);
    ce.setObs("NOVO_CONTRATO");
    ce.setUuid(UuidCreator.getTimeOrderedEpoch());
    return ce;
  }

  public RegimeTrabalhoEntity toRegime(DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    var re = new RegimeTrabalhoEntity();
    re.setTipoRegime(dc.getRegimeTrabalho());
    re.setTipoSituacao("NOVO_CONTRATO");
    re.setDataFim(dc.getDataFim());
    re.setObs(null);
    re.setEstado(Estado.P);
    re.setUuid(UuidCreator.getTimeOrderedEpoch());
    return re;
  }

  public MobilidadeEntity toMobilidade(DadosContratuaisReqDTO dc) {
    if (dc == null) return null;
    var me = new MobilidadeEntity();
    me.setTipoSituacao("NOVO_CONTRATO");
    me.setEstado(Estado.P);
    me.setObs("NOVO_CONTRATO");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setLocalTrabId(em.getReference(ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    me.setSecaoId(em.getReference(SecaoEntity.class, dc.getSeccaoId()));
    me.setInstidId(em.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
    me.setDataInicio(dc.getDataInicio());
    me.setDataFim(dc.getDataFim());
    return me;
  }

  public DefinicaoRemuneracaoEntity toDefinicaoRemuneracao(SubsidioReqDTO s, FuncionarioEntity fun) {
    if (s == null) return null;
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(s.getPercentagem());
    de.setValor(s.getValor());
    de.setEstado(Estado.P);
    de.setObs(s.getObservacoes());
    de.setDataInicio(LocalDate.now());
    de.setDataFim(LocalDate.now());
    if (s.getTipoSubsidioId() != null) {
      de.setTmId(em.getReference(TipoMovimentoEntity.class, s.getTipoSubsidioId()));
    }
    de.setFunId(fun);
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }

  public DefPagamentoEntity toDefPagamento(EncargosDescontosReqDTO e, FuncionarioEntity fun) {
    if (e == null) return null;
    var dp = new DefPagamentoEntity();
    if (e.getTipoEncargoId() != null) {
      dp.setTmId(em.getReference(TipoMovimentoEntity.class, e.getTipoEncargoId()));
    }
    dp.setValor(e.getValor());
    dp.setDataInicio(e.getDataInicio());
    dp.setDataFim(e.getDataFim());
    dp.setObs(e.getObservacoes());
    dp.setEstado(Estado.P);
    dp.setUuid(UuidCreator.getTimeOrderedEpoch());
    dp.setFunId(fun);
    return dp;
  }

  public ValidacaoEntity toValidacaoInsert(String referenciaName, Long funId) {
    var v = new ValidacaoEntity();
    v.setTipoAccao("INSERT");
    v.setReferenciaName(referenciaName);
    v.setReferenciaId(funId);
    v.setEstado(Estado.P);
    v.setObs(null);
    v.setUuid(UuidCreator.getTimeOrderedEpoch());
    return v;
  }

  public SituacaoLaboralEntity toSituacaoLaboralInicial(DadosContratuaisReqDTO dc, ParamSitLaboralEntity param) {
    var sl = new SituacaoLaboralEntity();
    sl.setSituacaoLaboralId(param);
    sl.setMotivoSitLab(null);
    sl.setDataInicio(dc.getDataInicio());
    sl.setDataFim(null);
    sl.setEstado(Estado.P);
    sl.setObs("NOVO_CONTRATO");
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    return sl;
  }
}
