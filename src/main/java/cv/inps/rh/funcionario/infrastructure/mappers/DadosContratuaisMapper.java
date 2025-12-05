package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosRespDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DadosContratuaisMapper {

  @PersistenceContext
  private EntityManager em;


  public TiposRelacionamentoEntity toRelacionamento(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var tr = new TiposRelacionamentoEntity();
    tr.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    tr.setInstitId(em.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
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
    tr.setReferente("REGISTO_COLABORADOR");
    tr.setUuid(UuidCreator.getTimeOrderedEpoch());
    tr.setEstado(estado);
    return tr;
  }

  public void toUpdateRelacionamento(TiposRelacionamentoEntity tr, DadosContratuaisReqDTO dc) {
    if (dc == null) return ;
    tr.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    tr.setInstitId(em.getReference(InstituicaoEntity.class, dc.getDirecaoId()));
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
    tr.setReferente("REGISTO_COLABORADOR");

  }


  public ValidacaoEntity toValidacaoInsert(String TipoAccao, String referenciaName, Estado estado) {
    var v = new ValidacaoEntity();
    v.setTipoAccao(TipoAccao);
    v.setReferenciaName(referenciaName);
    v.setEstado(estado);
    v.setUuid(UuidCreator.getTimeOrderedEpoch());
    return v;
  }

  public SituacaoLaboralEntity toSituacaoLaboral(DadosContratuaisReqDTO dc, ParamSitLaboralEntity param, Estado estado,
                                                 String motivoSituacaoLaboral, String observacao) {
    var sl = new SituacaoLaboralEntity();
    sl.setSituacaoLaboralId(param);
    sl.setMotivoSitLab(motivoSituacaoLaboral);
    sl.setDataInicio(dc.getDataInicio());
    sl.setDataFim(dc.getDataFim());
    sl.setEstado(estado);
    sl.setObs(observacao);
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    return sl;
  }

  public SituacaoLaboralEntity toUpdateSituacaoLaboral(SituacaoLaboralEntity entity, DadosContratuaisReqDTO dc) {
    if (dc == null) return entity;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    return entity;
  }


  public DadosContratuaisRespDTO dadosContratuaisRespDTO(TiposRelacionamentoEntity tiposRelacionamento) {
    if (tiposRelacionamento == null) return null;

    DadosContratuaisRespDTO dcr = new DadosContratuaisRespDTO();

    dcr.setTipoContratoId(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getTpContratoId().getId() : null);
    dcr.setTipoContratoDesc(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getTpContratoId().getNome() : null);

    dcr.setCargoPosicaoId(tiposRelacionamento.getCargoId() != null ? tiposRelacionamento.getCargoId().getId() : null);
    dcr.setCargoPosicaoDesc(tiposRelacionamento.getCargoId() != null ? tiposRelacionamento.getCargoId().getNome() : null);

    dcr.setDirecaoId(tiposRelacionamento.getInstitId() != null ? tiposRelacionamento.getInstitId().getId() : null);
    dcr.setDirecaoDesc(tiposRelacionamento.getInstitId() != null ? tiposRelacionamento.getInstitId().getNome() : null);

    dcr.setSeccaoId(tiposRelacionamento.getSeccaoId() != null ? tiposRelacionamento.getSeccaoId().getId() : null);
    dcr.setSeccaoDesc(tiposRelacionamento.getSeccaoId() != null ? tiposRelacionamento.getSeccaoId().getNome() : null);

    dcr.setCarreiraId(tiposRelacionamento.getCarrPccId() != null ? tiposRelacionamento.getCarrPccId().getId() : null);
    dcr.setCarreiraDesc(tiposRelacionamento.getCarrPccId() != null ? tiposRelacionamento.getCarrPccId().getNome() : null);

    dcr.setCategoriaId(tiposRelacionamento.getCategoriaId() != null ? tiposRelacionamento.getCategoriaId().getId() : null);
    dcr.setCategoriaDesc(tiposRelacionamento.getCategoriaId() != null ? tiposRelacionamento.getCategoriaId().getNome() : null);

    dcr.setEscalaoReferenciaId(tiposRelacionamento.getEscalaoId() != null ? tiposRelacionamento.getEscalaoId().getId() : null);
    dcr.setEscalaoReferenciaDesc(tiposRelacionamento.getEscalaoId() != null ? tiposRelacionamento.getEscalaoId().getEscalao() : null);

    dcr.setLocalTrabalhoId(tiposRelacionamento.getLocTrabId() != null ? tiposRelacionamento.getLocTrabId().getId() : null);
    dcr.setLocalTrabalhoDesc(tiposRelacionamento.getLocTrabId() != null ? tiposRelacionamento.getLocTrabId().getNome() : null);

    dcr.setTipoVinculoLaboralId(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getVinculoId().getId() : null);
    dcr.setTipoVinculoLaboralDesc(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getVinculoId().getNome() : null);

    dcr.setRegimeTrabalho(tiposRelacionamento.getRegime());
    dcr.setSalario(tiposRelacionamento.getSalario());
    dcr.setMoeda(tiposRelacionamento.getMoeda());
    dcr.setDataInicio(tiposRelacionamento.getDataInicioContrato());
    dcr.setDataFim(tiposRelacionamento.getDataFimContrato());

    if (tiposRelacionamento.getContrVinculoId() != null)
      dcr.setDuracaoMeses(tiposRelacionamento.getContrVinculoId().getDuracao());

    // Subsídios
    if (tiposRelacionamento.getFunId().getDefinicoesRenumeracoes() != null) {
      var subs = tiposRelacionamento.getFunId().getDefinicoesRenumeracoes().stream().map(s -> {
        SubsidioRespDTO sr = new SubsidioRespDTO();
        sr.setId(s.getId());
        sr.setTipoSubsidioId(s.getTmId() != null ? s.getTmId().getId() : null);
        sr.setPercentagem(s.getPercentagem());
        sr.setValor(s.getValor());
        return sr;
      }).toList();

      dcr.setSubsidios(subs);
    }

    // Encargos / descontos
    if (tiposRelacionamento.getFunId().getDefinicoesPagamentos() != null) {
      var encs = tiposRelacionamento.getFunId().getDefinicoesPagamentos().stream().map(e -> {
        EncargosDescontosRespDTO er = new EncargosDescontosRespDTO();
        er.setId(e.getId());
        er.setTipoEncargoId(e.getTmId() != null ? e.getTmId().getId() : null);
        er.setValor(e.getValor());
        er.setDataInicio(e.getDataInicio());
        er.setDataFim(e.getDataFim());
        return er;
      }).toList();

      dcr.setEncargosDescontos(encs);
    }

    return dcr;
  }


  public TiposRelacionamentoEntity clone(TiposRelacionamentoEntity original) {

    TiposRelacionamentoEntity clone = new TiposRelacionamentoEntity();
    clone.setUuid(IdentificadorUnico.create().valor());
    clone.setCargoId(original.getCargoId());
    clone.setInstitId(original.getInstitId());
    clone.setSeccaoId(original.getSeccaoId());
    clone.setCategoriaId(original.getCategoriaId());
    clone.setEscalaoId(original.getEscalaoId());
    clone.setCarrPccId(original.getCarrPccId());
    clone.setSalario(original.getSalario());
    clone.setMoeda(original.getMoeda());
    clone.setRegime(original.getRegime());
    clone.setTipoSituacao(original.getTipoSituacao());
    clone.setCarreiraId(original.getCarreiraId());
    clone.setMobId(original.getMobId());
    clone.setRegimeId(original.getRegimeId());
    clone.setTiprelId(original); // se quiser referenciar o anterior
    clone.setContrVinculoId(original.getContrVinculoId());
    clone.setEstado(original.getEstado());
    clone.setDataInicio(original.getDataInicio());
    clone.setEstActAdm(original.getEstActAdm());
    clone.setFunId(original.getFunId());
    clone.setObs(original.getObs());
    clone.setLocTrabId(original.getLocTrabId());
    clone.setSituacLaboralId(original.getSituacLaboralId());
    clone.setReferente(original.getReferente());
    clone.setUltProc(original.getUltProc());
    clone.setMotivoSitLab(original.getMotivoSitLab());
    clone.setFlgProcessa(original.getFlgProcessa());

    return clone;
  }


}
