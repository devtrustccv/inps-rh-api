package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AlertaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SituacaoLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertaWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertaWriteService.class);

  private static final String DOMINIO_PRAZO = "CONFIGURACAO_PRAZO";
  private static final int DEFAULT_PRAZO_DIAS = 30;

  private static final String TIPO_RENOVACAO = "RENOVACAO_CONTRATO";
  private static final String TIPO_CONVERSAO = "CONVERSAO_CONTRATO";
  private static final String TIPO_LICENCA_SV = "LICENCA_S_VENCIMENTO";

  private static final String PRIORIDADE_ALTA = "ALTA";
  private static final String PRIORIDADE_MEDIA = "MEDIA";
  private static final String ESTADO_PENDENTE = "P";

  private final AlertaEntityRepository alertaRepository;
  private final ContratoEntityRepository contratoRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralRepository;
  private final DomainEntityRepository domainRepository;

  // TODO(JOB->notificação): o JOB apenas CRIA o alerta (flg_notificacao='N'); NÃO gera notificação nem
  // envia email. A infra existe e funciona (NotificacaoDispatchService + NotificacaoDestinatarioResolver
  // + OracleEmailService — usada só pelo ecrã manual em NotificacaoController). Para ligar aqui:
  //   1) injetar NotificacaoDispatchService (+ resolver) neste service;
  //   2) por cada alerta criado, resolver destinatários e enviar; marcar alerta.setFlgNotificacao("S").
  // BLOQUEIO DE NEGÓCIO (spec silente): a spec TRANSVERSAL não define QUEM recebe a notificação do JOB
  // (só define destinatários para o envio MANUAL). Decisão pendente — provável: um ADMIN configurável +
  // talvez COLABORADOR e/ou RESPONSAVEL_COLABORADOR. Implementar de forma CONFIGURÁVEL (não hardcodar):
  // admin via env/RH_T_DOMINIO; destinatários por tipo de alerta via RH_T_DOMINIO; sem config -> não
  // envia (flg_notificacao fica 'N'). Ver handoff dossier_melhorias.md (secção TODO).
  @Scheduled(cron = "${alerta.job.cron:0 0 6 * * *}")
  @Transactional
  public void executarJobAlertas() {
    LOGGER.info("Iniciando JOB Alertas");
    processarRenovacaoContrato();
    processarConversaoContrato();
    processarLicencaSemVencimento();
    LOGGER.info("JOB Alertas concluído");
  }

  private void processarRenovacaoContrato() {
    int prazo = getPrazo("RENOVACAO");
    LocalDate hoje = LocalDate.now();

    var contratos = contratoRepository.findRenovaveisProximosAoFim(Estado.A, hoje, hoje.plusDays(prazo));
    LOGGER.info("Renovação contrato: {} candidatos", contratos.size());

    for (var contrato : contratos) {
      if (alertaRepository.existsByReferenciaIdAndTipoAlerta(contrato.getId(), TIPO_RENOVACAO)) continue;

      var alerta = novoAlerta();
      alerta.setFunId(contrato.getFunId());
      alerta.setReferencia("CONTRATO");
      alerta.setReferenciaName("RH_T_CONTRATO_VINCULO");
      alerta.setReferenciaId(contrato.getId());
      // uuid do contrato — o frontend faz lookup pelos endpoints por UUID (get contrato by id),
      // igual à lista de validações; evita traduzir id->uuid.
      alerta.setReferenciaUuid(contrato.getUuid());
      alerta.setTipoAlerta(TIPO_RENOVACAO);
      alerta.setPrioridade(PRIORIDADE_ALTA);
      alerta.setDescricao(descricaoRenovacao(contrato));
      alertaRepository.save(alerta);
    }
  }

  private void processarConversaoContrato() {
    int prazo = getPrazo("CONVERSAO");
    LocalDate hoje = LocalDate.now();
    // Alerta quando a marca de 3 anos está dentro da janela de prazo
    // dataInicio entre (hoje - 3 anos - prazo) e (hoje - 3 anos)
    LocalDate dataInicioMax = hoje.minusYears(3);
    LocalDate dataInicioMin = dataInicioMax.minusDays(prazo);

    var contratos = contratoRepository.findParaConversao(Estado.A, dataInicioMin, dataInicioMax);
    LOGGER.info("Conversão contrato: {} candidatos", contratos.size());

    for (var contrato : contratos) {
      if (alertaRepository.existsByReferenciaIdAndTipoAlerta(contrato.getId(), TIPO_CONVERSAO)) continue;

      var alerta = novoAlerta();
      alerta.setFunId(contrato.getFunId());
      alerta.setReferencia("CONTRATO");
      alerta.setReferenciaName("RH_T_CONTRATO_VINCULO");
      alerta.setReferenciaId(contrato.getId());
      alerta.setReferenciaUuid(contrato.getUuid());
      alerta.setTipoAlerta(TIPO_CONVERSAO);
      alerta.setPrioridade(PRIORIDADE_ALTA);
      alerta.setDescricao(descricaoConversao(contrato));
      alertaRepository.save(alerta);
    }
  }

  private void processarLicencaSemVencimento() {
    int prazo = getPrazo("LICENCA_S_VENCIMENTO");
    LocalDate hoje = LocalDate.now();

    // Licenças a expirar dentro do prazo
    var aExpirar = situacaoLaboralRepository.findLicencasAExpirar(
        TIPO_LICENCA_SV, Estado.A, hoje, hoje.plusDays(prazo));
    LOGGER.info("Licença s/ vencimento a expirar: {} candidatos", aExpirar.size());

    for (var licenca : aExpirar) {
      if (semContrato(licenca)) continue;
      if (alertaRepository.existsByReferenciaIdAndTipoAlerta(licenca.getId(), TIPO_LICENCA_SV)) continue;

      var alerta = novoAlertaLicenca(licenca);
      alerta.setDescricao("O Colaborador " + licenca.getContrVinculoId().getFunId().getNome()
          + ", tem uma licença sem vencimento próxima de expirar"
          + " (Data início: " + licenca.getDataInicio()
          + " e Data fim: " + licenca.getDataFim() + ")");
      alertaRepository.save(alerta);
    }

    // Licenças já expiradas
    var expiradas = situacaoLaboralRepository.findLicencasExpiradas(TIPO_LICENCA_SV, Estado.A, hoje);
    LOGGER.info("Licença s/ vencimento expirada: {} candidatos", expiradas.size());

    for (var licenca : expiradas) {
      if (semContrato(licenca)) continue;
      if (alertaRepository.existsByReferenciaIdAndTipoAlerta(licenca.getId(), TIPO_LICENCA_SV)) continue;

      var alerta = novoAlertaLicenca(licenca);
      alerta.setDescricao("O Colaborador " + licenca.getContrVinculoId().getFunId().getNome()
          + ", tem uma licença sem vencimento expirada"
          + " (Data início: " + licenca.getDataInicio()
          + " e Data fim: " + licenca.getDataFim() + ")");
      alertaRepository.save(alerta);
    }
  }

  private AlertaEntity novoAlerta() {
    var alerta = new AlertaEntity();
    alerta.setEstado(ESTADO_PENDENTE);
    alerta.setUuid(UUID.randomUUID());
    alerta.setFlgNotificacao("N");
    alerta.setFlgTratamento("N");
    return alerta;
  }

  private AlertaEntity novoAlertaLicenca(SituacaoLaboralEntity licenca) {
    var alerta = novoAlerta();
    alerta.setFunId(licenca.getContrVinculoId().getFunId());
    alerta.setReferencia("LICENCA");
    alerta.setReferenciaName("RH_T_SITUACAO_LABORAL");
    alerta.setReferenciaId(licenca.getId());
    alerta.setReferenciaUuid(licenca.getUuid());
    alerta.setTipoAlerta(TIPO_LICENCA_SV);
    alerta.setPrioridade(PRIORIDADE_MEDIA);
    return alerta;
  }

  private boolean semContrato(SituacaoLaboralEntity licenca) {
    return licenca.getContrVinculoId() == null || licenca.getContrVinculoId().getFunId() == null;
  }

  private String descricaoRenovacao(ContratoEntity contrato) {
    return "O Colaborador " + contrato.getFunId().getNome()
        + ", tem um contrato " + contrato.getTpContratoId().getNome()
        + ", cuja duração é " + contrato.getDuracao()
        + " mês(es), com data início em " + contrato.getDataInicio()
        + ", próximo a ser renovado.";
  }

  private String descricaoConversao(ContratoEntity contrato) {
    long anos = ChronoUnit.YEARS.between(contrato.getDataInicio(), LocalDate.now());
    return "O Colaborador " + contrato.getFunId().getNome()
        + ", tem um contrato " + contrato.getTpContratoId().getNome()
        + ", cuja data início do primeiro contrato é " + contrato.getDataInicio()
        + ". Já completou " + anos + " ano(s) no vínculo, o seu contrato pode ser convertido.";
  }

  private int getPrazo(String referencia) {
    var domains = domainRepository.findByDominioAndReferenciaAndEstado(DOMINIO_PRAZO, referencia, Estado.A);
    if (domains.isEmpty()) return DEFAULT_PRAZO_DIAS;
    try {
      return Integer.parseInt(domains.getFirst().getValor());
    } catch (NumberFormatException e) {
      LOGGER.warn("CONFIGURACAO_PRAZO {} tem valor inválido, usando padrão {}d", referencia, DEFAULT_PRAZO_DIAS);
      return DEFAULT_PRAZO_DIAS;
    }
  }
}
