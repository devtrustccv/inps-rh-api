package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosRespDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioRespDTO;
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
    tr.setSalario(dc.getSalario());
    tr.setMoeda(dc.getMoeda());
    tr.setTipoSituacao("NOVO_CONTRATO");
    tr.setFlgProcessa(1);
    tr.setObs("NOVO_CONTRATO");
    tr.setDataInicio(dc.getDataInicio());
    tr.setDataFim(dc.getDataFim());
    tr.setReferente("REGISTO_COLABORADOR");
    tr.setUuid(UuidCreator.getTimeOrderedEpoch());
    tr.setEstado(estado);
    return tr;
  }

  public void toUpdateRelacionamento(TiposRelacionamentoEntity tr, DadosContratuaisReqDTO dc) {
    if (dc == null) return ;
    tr.setCargoId(em.getReference(ParamCargoEntity.class, dc.getCargoPosicaoId()));
    tr.setSalario(dc.getSalario());
    tr.setMoeda(dc.getMoeda());
    tr.setTipoSituacao("NOVO_CONTRATO");
    tr.setObs("NOVO_CONTRATO");
    tr.setDataInicio(dc.getDataInicio());
    tr.setDataFim(dc.getDataInicio());
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

  public SituacaoLaboralEntity toSituacaoLaboral(DadosContratuaisReqDTO dc, ParamSituacaoEntity param, Estado estado,
                                                 String motivoSituacaoLaboral, String observacao) {
    var sl = new SituacaoLaboralEntity();
    sl.setSituacaoLaboralId(param);
    sl.setTipoSituacao(motivoSituacaoLaboral);
    sl.setDataInicio(dc.getDataInicio());
    sl.setDataFim(dc.getDataFim());
    sl.setEstado(estado);
    sl.setObs(observacao);
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    return sl;
  }

  public void toUpdateSituacaoLaboral(SituacaoLaboralEntity entity, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    entity.setDataInicio(dc.getDataInicio());
    entity.setDataFim(dc.getDataFim());
    entity.setSituacaoLaboralId(em.getReference(ParamSituacaoEntity.class,dc.getSituacaoLaboralId()));

  }


  public DadosContratuaisRespDTO dadosContratuaisRespDTO(TiposRelacionamentoEntity tiposRelacionamento) {
    if (tiposRelacionamento == null) return null;

    DadosContratuaisRespDTO dcr = new DadosContratuaisRespDTO();

    dcr.setTipoContratoId(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getTpContratoId().getId() : null);
    dcr.setTipoContratoDesc(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getTpContratoId().getNome() : null);

    dcr.setCargoPosicaoId(tiposRelacionamento.getCargoId() != null ? tiposRelacionamento.getCargoId().getId() : null);
    dcr.setCargoPosicaoDesc(tiposRelacionamento.getCargoId() != null ? tiposRelacionamento.getCargoId().getNome() : null);

    if(tiposRelacionamento.getMobId()!=null){
      dcr.setDirecaoId(tiposRelacionamento.getMobId().getInstidId().getId());
      dcr.setDirecaoDesc(tiposRelacionamento.getMobId().getInstidId().getNome());
      dcr.setSeccaoId(tiposRelacionamento.getMobId().getSecaoId().getId());
      dcr.setSeccaoDesc(tiposRelacionamento.getMobId().getSecaoId().getNome());
    }

    dcr.setCarreiraId(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getCarrPccsId().getId() : null);

    dcr.setCarreiraDesc(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getCarrPccsId().getNome(): null);

    dcr.setCategoriaId(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getCategoriaId().getId() : null);

    dcr.setCategoriaDesc(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getCategoriaId().getNome() : null);

    dcr.setEscalaoReferenciaId(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getEscalaoId().getId() : null);

    dcr.setEscalaoReferenciaDesc(tiposRelacionamento.getCarreiraId()!=null ?
        tiposRelacionamento.getCarreiraId().getEscalaoId().getEscalao() : null);

    if(tiposRelacionamento.getMobId()!=null){
      dcr.setLocalTrabalhoId(tiposRelacionamento.getMobId().getLocalTrabId().getId());
      dcr.setLocalTrabalhoDesc(tiposRelacionamento.getMobId().getLocalTrabId().getNome());
    }

    dcr.setTipoVinculoLaboralId(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getVinculoId().getId() : null);
    dcr.setTipoVinculoLaboralDesc(tiposRelacionamento.getContrVinculoId() != null ? tiposRelacionamento.getContrVinculoId().getVinculoId().getNome() : null);

    dcr.setSalario(tiposRelacionamento.getSalario());
    dcr.setMoeda(tiposRelacionamento.getMoeda());
    dcr.setDataInicio(tiposRelacionamento.getDataInicio());
    dcr.setDataFim(tiposRelacionamento.getDataFim());
    dcr.setRegimeTrabalho(tiposRelacionamento.getRegimeId()!=null ? tiposRelacionamento.getRegimeId().getTipoRegime() : null);

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
    clone.setSalario(original.getSalario());
    clone.setMoeda(original.getMoeda());
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
    clone.setSituacLaboralId(original.getSituacLaboralId());
    clone.setReferente(original.getReferente());
    clone.setUltProc(original.getUltProc());
    clone.setMotivoSitLab(original.getMotivoSitLab());
    clone.setFlgProcessa(original.getFlgProcessa());

    return clone;
  }


}
