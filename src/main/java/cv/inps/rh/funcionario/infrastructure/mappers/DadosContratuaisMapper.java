package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.service.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.UUID;

@Component
public class DadosContratuaisMapper {

  @PersistenceContext
  private EntityManager em;

  private final FuncionarioRules funcionarioRules;

  public DadosContratuaisMapper(FuncionarioRules funcionarioRules) {
    this.funcionarioRules = funcionarioRules;
  }


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
    sl.setDataFim(dc.getDataFim());
    sl.setEstado(estado);
    sl.setObs("NOVO_CONTRATO");
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    return sl;
  }


  public DadosContratuaisRespDTO dadosContratuaisRespDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

    // Tem tipo relacionamento?
    if (entity.getTiposrelacionamentos() == null || entity.getTiposrelacionamentos().isEmpty()) {
      return null;
    }

    var tr = funcionarioRules.getTipoRelacionamentoAtual(entity);
    if (tr == null) return null;

    DadosContratuaisRespDTO dcr = new DadosContratuaisRespDTO();

    dcr.setTipoContratoId(tr.getTipoContratoId() != null ? tr.getTipoContratoId().getId() : null);
    dcr.setTipoContratoDesc(tr.getTipoContratoId() != null ? tr.getTipoContratoId().getNome() : null);

    dcr.setCargoPosicaoId(tr.getCargoId() != null ? tr.getCargoId().getId() : null);
    dcr.setCargoPosicaoDesc(tr.getCargoId() != null ? tr.getCargoId().getNome() : null);

    dcr.setDirecaoId(tr.getInstitId() != null ? tr.getInstitId().getId() : null);
    dcr.setDirecaoDesc(tr.getInstitId() != null ? tr.getInstitId().getNome() : null);

    dcr.setSeccaoId(tr.getSeccaoId() != null ? tr.getSeccaoId().getId() : null);
    dcr.setSeccaoDesc(tr.getSeccaoId() != null ? tr.getSeccaoId().getNome() : null);

    dcr.setCarreiraId(tr.getCarrPccId() != null ? tr.getCarrPccId().getId() : null);
    dcr.setCarreiraDesc(tr.getCarrPccId() != null ? tr.getCarrPccId().getNome() : null);

    dcr.setCategoriaId(tr.getCategoriaId() != null ? tr.getCategoriaId().getId() : null);
    dcr.setCategoriaDesc(tr.getCategoriaId() != null ? tr.getCategoriaId().getNome() : null);

    dcr.setEscalaoReferenciaId(tr.getEscalaoId() != null ? tr.getEscalaoId().getId() : null);
    dcr.setEscalaoReferenciaDesc(tr.getEscalaoId() != null ? tr.getEscalaoId().getEscalao() : null);

    dcr.setLocalTrabalhoId(tr.getLocTrabId() != null ? tr.getLocTrabId().getId() : null);
    dcr.setLocalTrabalhoDesc(tr.getLocTrabId() != null ? tr.getLocTrabId().getNome() : null);

    dcr.setTipoVinculoLaboralId(tr.getVinculoId() != null ? tr.getVinculoId().getId() : null);
    dcr.setTipoVinculoLaboralDesc(tr.getVinculoId() != null ? tr.getVinculoId().getNome() : null);

    dcr.setRegimeTrabalho(tr.getRegime());
    dcr.setSalario(tr.getSalario());
    dcr.setMoeda(tr.getMoeda());
    dcr.setDataInicio(tr.getDataInicioContrato());
    dcr.setDataFim(tr.getDataFimContrato());

    if (tr.getContratoId() != null)
      dcr.setDuracaoMeses(tr.getContratoId().getDuracao());

    // Subsídios
    if (entity.getDefinicoesRenumeracoes() != null) {
      var subs = entity.getDefinicoesRenumeracoes().stream().map(s -> {
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
    if (entity.getDefinicoesPagamentos() != null) {
      var encs = entity.getDefinicoesPagamentos().stream().map(e -> {
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
    clone.setUuid(IdentificadorUnico.create().getValor());
    clone.setCargoId(original.getCargoId());
    clone.setInstitId(original.getInstitId());
    clone.setVinculoId(original.getVinculoId());
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
    clone.setTipoContratoId(original.getTipoContratoId());
    clone.setTpContrato(original.getTpContrato());
    clone.setTiprelId(original); // se quiser referenciar o anterior
    clone.setContratoId(original.getContratoId());
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
