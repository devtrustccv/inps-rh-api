package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class GetContratoByIdQueryHandler implements QueryHandler<GetContratoByIdQuery, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetContratoByIdQueryHandler.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final RhVContratoEntityRepository rhVContratoEntityRepository;

  public GetContratoByIdQueryHandler(FuncionarioEntityRepository funcionarioEntityRepository, DadosContratuaisMapper dadosContratuaisMapper, FuncionarioRules funcionarioRules,
                                     TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository,
                                     RhVContratoEntityRepository rhVContratoEntityRepository) {

    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.dadosContratuaisMapper = dadosContratuaisMapper;
    this.funcionarioRules = funcionarioRules;
    this.tiposRelacionamentoEntityRepository = tiposRelacionamentoEntityRepository;
    this.rhVContratoEntityRepository = rhVContratoEntityRepository;
  }


  @Transactional(readOnly = true)
  @IgrpQueryHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(GetContratoByIdQuery query) {
    LOGGER.info("Handling GetContratoByIdQuery: {}", query);

    var contratoId = IdentificadorUnico.from(query.getContratoId()).valor();

    var idFunc = IdentificadorUnico.from(query.getId());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    // Só há 2 vistas com botão: INICIAL e ATUAL. O frontend manda a `versao` (opcional). Filtramos a
    // RH_V_CONTRATO por uuid+versão; se essa versão for a INICIAL (tipo_situacao='INICIO') mostramos
    // o tiprel INICIO + datas da versão inicial. Qualquer outro caso — sem versão, versão do meio
    // (sem botão) ou inexistente — faz FALLBACK para a ATUAL (est_act_adm=1).
    var versao = query.getVersao();
    TiposRelacionamentoEntity tiposRelacionamento = null;
    RhVContratoEntity versaoInicial = null;

    if (versao != null) {
      var vista = rhVContratoEntityRepository.findByContratoUuidAndVersao(contratoId, versao).orElse(null);
      if (vista != null && "INICIO".equalsIgnoreCase(vista.getTipoSituacao())) {
        var inicioTiprel = tiposRelacionamentoEntityRepository
            .findByFunUuidAndContratoUuidAndTipoSituacao(funcionario.getUuid(), contratoId, "INICIO")
            .stream().findFirst().orElse(null);
        if (inicioTiprel != null) {
          tiposRelacionamento = inicioTiprel;
          versaoInicial = vista;
        }
      }
    }
    if (tiposRelacionamento == null) {
      // Default/fallback: ATUAL (est_act_adm=1).
      tiposRelacionamento = funcionarioRules.getTipoRelacionamentoByContratoId(funcionario.getUuid(), contratoId);
    }

    if (tiposRelacionamento == null)
      throw IgrpResponseStatusException.notFound("Contrato com id '%s' não encontrado".formatted(contratoId));

    // Def SÓ deste contrato: pela associação do tiprel. Regra do analista (progressão 4.2.2): os def
    // relevantes são os que estão 'A' — o vencimento superseded fecha-se por DATA_FIM mas MANTÉM 'A'.
    // Um tiprel superseded pela progressão fica 'I' enquanto os seus def ficam 'A', logo NÃO se pode
    // filtrar por "def.estado == tiprel.estado" (daria vazio). Filtra-se por 'A', EXCETO quando o
    // tiprel está PENDENTE (P): aí mostram-se os def P (preview). Isto também exclui os E/I
    // (eliminados/inactivos — ex.: manuais substituídos pelo sync), que não são 'A'.
    var estadoAlvo = tiposRelacionamento.getEstado() == Estado.P ? Estado.P : Estado.A;
    // Vista ATUAL (est_act_adm=1): mostra só os def EM VIGOR — o MESMO predicado "não-terminado" da
    // cópia da renovação: data_fim NULL ou >= hoje. Exclui os expirados (ex.: diferenças de meses já
    // passados) e mantém os futuros. A vista INICIAL não filtra período (mostra o estado inicial).
    boolean atual = Integer.valueOf(1).equals(tiposRelacionamento.getEstActAdm());
    var hoje = LocalDate.now();

    var remuneracoes = funcionarioRules.getRemuneracoesAssociados(tiposRelacionamento.getId())
        .stream().filter(r -> r.getEstado() == estadoAlvo)
        .filter(r -> !atual || emVigor(r.getDataFim(), hoje))
        .toList();
    var pagamentos = funcionarioRules.getPagamentosDescontosAssociados(tiposRelacionamento.getId())
        .stream().filter(p -> p.getEstado() == estadoAlvo)
        .filter(p -> !atual || emVigor(p.getDataFim(), hoje))
        .toList();

    var dadosContratuaisResp = dadosContratuaisMapper
        .dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes);

    // Mesmos 2 flags da lista (ContratoMapper.toDTO): `inicial` = versão 1 (a vista INICIAL só é
    // resolvida quando o tiprel INICIO/versão 1 existe), `atual` = est_act_adm=1 (o tiprel em vigor).
    dadosContratuaisResp.setInicial(versaoInicial != null && Integer.valueOf(1).equals(versaoInicial.getVersao()));
    dadosContratuaisResp.setAtual(atual);

    // Só na vista INICIAL: as datas da versão vêm da VISTA (o tiprel INICIO tem data_fim = data do
    // fecho, não a da versão). No fallback/atual, versaoInicial fica null → usa as datas do tiprel atual.
    if (versaoInicial != null) {
      dadosContratuaisResp.setDataInicio(versaoInicial.getDataInicio());
      dadosContratuaisResp.setDataFim(versaoInicial.getDataFim());
    }

    return ResponseEntity.ok(dadosContratuaisResp);
  }

  /** Em vigor = não terminado (mesmo predicado da cópia da renovação): DATA_FIM nula ou >= hoje. */
  private static boolean emVigor(LocalDate dataFim, LocalDate hoje) {
    return dataFim == null || !dataFim.isBefore(hoje);
  }
}
