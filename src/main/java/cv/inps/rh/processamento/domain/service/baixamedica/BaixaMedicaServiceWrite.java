package cv.inps.rh.processamento.domain.service.baixamedica;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.processamento.application.dto.BaixaMedicaCalculoDTO;
import cv.inps.rh.processamento.application.dto.BaixaMedicaFaltaMensalDTO;
import cv.inps.rh.processamento.application.dto.BaixaMedicaReqDTO;
import cv.inps.rh.processamento.application.dto.PeriodoLicensaRowDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.internal.OracleCallableStatement;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BaixaMedicaServiceWrite {

  private final FuncionarioEntityRepository funcionarioRepository;
  private final FuncionarioRules funcionarioRules;
  private final AbonosBeneficiosEntityRepository abonosRepository;
  private final AbonosBeneficiosDetalheEntityRepository abonosBeneficiosDetalheEntityRepository;
  private final AusenciaEntityRepository ausenciaRepository;
  private final ParamSituacaoEntityRepository paramSituacaoRepository;
  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheRepository;
  private final ValidacaoEntityRepository validacaoRepository;
  private final FaltaEntityRepository faltaRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DocumentoMapper documentoMapper;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final JdbcTemplate jdbcTemplate;

  // ----------------------------------------------------------------
  // Preview: calcula sem gravar
  // ----------------------------------------------------------------
  @Transactional(readOnly = true)
  public BaixaMedicaCalculoDTO calcular(BaixaMedicaReqDTO req) {
    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (tiprel == null)
      throw IgrpResponseStatusException.badRequest("Colaborador sem tipo de relacionamento activo");

    return chamarProcedure(tiprel.getId(), req.getDataInicio(), req.getDataFim(),
        req.getTipoLicenca(), req.getDataInicioFalta());
  }

  // ----------------------------------------------------------------
  // Criar baixa médica
  // ----------------------------------------------------------------
  @Transactional
  public Map<String, Object> criar(BaixaMedicaReqDTO req) {

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (tiprel == null)
      throw IgrpResponseStatusException.badRequest("Colaborador sem tipo de relacionamento activo");

    var paramSit = paramSituacaoRepository.findByIdOrThrow(req.getTipoLicenca());

    var abono = new AbonosBeneficiosEntity();
    abono.setUuid(UuidCreator.getTimeOrderedEpoch());
    abono.setFunId(funcionario);
    abono.setParamSitId(paramSit);
    abono.setDataInicio(req.getDataInicio());
    abono.setDataFim(req.getDataFim());
    abono.setObs(req.getObservacao());
    abono.setEstado(Estado.P);
    if (req.getMotivo() != null)
      paramSituacaoDetalheRepository.findById(req.getMotivo()).ifPresent(abono::setParamSitDetId);
    abono = abonosRepository.save(abono);

    saveUpdatePeriodos(req.getPeriodos(), abono, Estado.P);

    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
      List<DocumentoEntity> docs = new ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d, Estado.P,
            TableName.RH_T_ABONOS_BENEFICIOS.name(),
            abono.getId(), abono.getUuid(), 1L, funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        docs.add(doc);
      }
      documentoEntityRepository.saveAll(docs);
    }

    if (Objects.equals(paramSit.getFlgAusencia(), 1)) {
      var ausencia = new AusenciaEntity();
      ausencia.setUuid(UuidCreator.getTimeOrderedEpoch());
      ausencia.setParamSitId(paramSit);
      ausencia.setFunId(funcionario);
      ausencia.setReferenciaName(TableName.RH_T_ABONOS_BENEFICIOS.name());
      ausencia.setReferenciaId(abono.getId());
      ausencia.setDataInicio(req.getDataInicio());
      ausencia.setDataFim(req.getDataFim());
      ausencia.setObs(req.getObservacao());
      ausencia.setEstado(Estado.P);
      ausenciaRepository.save(ausencia);
    }

    var calculo = chamarProcedure(tiprel.getId(), req.getDataInicio(), req.getDataFim(),
        req.getTipoLicenca(), req.getDataInicioFalta());

    if (calculo.getMsgError() != null && !calculo.getMsgError().isBlank())
      throw IgrpResponseStatusException.badRequest(calculo.getMsgError());

    for (var item : calculo.getFaltasMensais()) {
      var falta = new FaltaEntity();
      falta.setUuid(UuidCreator.getTimeOrderedEpoch());
      falta.setTiprelId(tiprel);
      falta.setParamSitId(paramSit);
      falta.setHorasAusencia("+0 00:00:00");
      // O procedure retorna datas em formato dd/MM/yyyy
      var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      if (item.getDataInicioFalta() != null && !item.getDataInicioFalta().isBlank())
        falta.setDataInicio(java.time.LocalDate.parse(item.getDataInicioFalta(), fmt).atStartOfDay());
      if (item.getDataFimFalta() != null && !item.getDataFimFalta().isBlank())
        falta.setDataFim(java.time.LocalDate.parse(item.getDataFimFalta(), fmt).atStartOfDay());
      if (item.getValorDesc() != null && !item.getValorDesc().isBlank())
        falta.setValor(new java.math.BigDecimal(item.getValorDesc()));
      falta.setEstado(Estado.P);
      faltaRepository.save(falta);
    }

    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.BAIXA_MEDICA.name(),
        Estado.P
    );

    validacao.setReferenciaId(abono.getId());
    validacao.setReferenciaUuid(abono.getUuid());
    validacaoRepository.save(validacao);

    return Map.of(
        "abonoId", abono.getId(),
        "abonoIdUuid", abono.getUuid().toString()
    );
  }

  private void saveUpdatePeriodos(@Valid List<PeriodoLicensaRowDTO> periodos, AbonosBeneficiosEntity abono, Estado estado) {

    var rows = new ArrayList<AbonosBeneficiosDetalheEntity>();

    for (var periodo : periodos) {

      final AbonosBeneficiosDetalheEntity periodoRow;

      if (StringUtils.hasText(periodo.getId()))
        periodoRow = abonosBeneficiosDetalheEntityRepository.findByUuidOrThrow(periodo.getId());
      else {
        periodoRow = new AbonosBeneficiosDetalheEntity();
        periodoRow.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      }

      periodoRow.setDataInicio(periodo.getDataInicio());
      periodoRow.setDataFim(periodo.getDataFim());
      periodoRow.setAbonoBenef(abono);
      periodoRow.setEstado(estado.name());
      rows.add(periodoRow);
    }

    abonosBeneficiosDetalheEntityRepository.saveAll(rows);
  }

  // ----------------------------------------------------------------
  // Validar / desvalidar — suporta ajuste de campos e documento (edit durante validação)
  // ----------------------------------------------------------------
  @Transactional
  public Map<String, Object> validar(String abonoId, EstadoValidacao validar, BaixaMedicaReqDTO ajuste) {

    var abonoUuid = UUID.fromString(abonoId);

    var abono = abonosRepository.findByUuidOrThrow(abonoUuid);

    final Estado estado = validar == EstadoValidacao.SIM ? Estado.A : Estado.I;

    // Actualizar estado dos documentos existentes
    var docsExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_ABONOS_BENEFICIOS.name(), abonoUuid);
    if (docsExistentes != null && !docsExistentes.isEmpty()) {
      docsExistentes.forEach(d -> d.setEstado(estado));
      documentoEntityRepository.saveAll(docsExistentes);
    }

    // Novos docs enviados na validação
    if (ajuste.getDocumentos() != null && !ajuste.getDocumentos().isEmpty()) {
      FuncionarioEntity funId = abono.getFunId();
      List<DocumentoEntity> novos = new ArrayList<>();
      for (var d : ajuste.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d, estado,
            TableName.RH_T_ABONOS_BENEFICIOS.name(),
            abono.getId(), abono.getUuid(), 1L, funId);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        novos.add(doc);
      }
      documentoEntityRepository.saveAll(novos);
    }

    funcionarioRules.getValidacaoPendenteByReferenciaUuid(abono.getUuid(), TipoAcao.INSERT, Referencia.BAIXA_MEDICA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoRepository.save(v);
        });

    saveUpdatePeriodos(ajuste.getPeriodos(), abono, estado);

    return Map.of(
        "abonoId", abono.getId(),
        "abonoIdUuid", abono.getUuid().toString()
    );
  }

  // ----------------------------------------------------------------
  // Chamada JDBC ao procedure CALCULO_FALTA_LICENCA
  // ----------------------------------------------------------------
  public BaixaMedicaCalculoDTO chamarProcedure(
      Long tiprelId, java.time.LocalDate dataInicio, java.time.LocalDate dataFim,
      Long tipoLicenca, java.time.LocalDate dataInicioFalta) {

    var result = new BaixaMedicaCalculoDTO();
    var faltasMensais = new ArrayList<BaixaMedicaFaltaMensalDTO>();

    var sql = """
        BEGIN
            INPSRH.RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_LICENCA(
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?,
                ?
            );
        END;
        """;

    jdbcTemplate.execute((ConnectionCallback<Void>) con -> {
      var cs = con.prepareCall(sql).unwrap(OracleCallableStatement.class);

      cs.setLong(1, tiprelId);
      cs.setDate(2, Date.valueOf(dataInicio));
      cs.setDate(3, Date.valueOf(dataFim));
      cs.setLong(4, tipoLicenca);

      cs.registerOutParameter(5, Types.VARCHAR);   // p_desc_sobre
      cs.registerOutParameter(6, Types.NUMERIC);   // p_dias_Direito
      cs.registerOutParameter(7, Types.NUMERIC);   // p_dias_desc_rh
      cs.registerOutParameter(8, Types.NUMERIC);   // p_dias_ndesc_rh

      cs.registerIndexTableOutParameter(9, 100, Types.VARCHAR, 4000); // p_meses
      cs.registerIndexTableOutParameter(10, 100, Types.VARCHAR, 4000); // p_dias_falta
      cs.registerIndexTableOutParameter(11, 100, Types.VARCHAR, 4000); // p_valor_desc
      cs.registerIndexTableOutParameter(12, 100, Types.VARCHAR, 4000); // p_valor_salario
      cs.registerIndexTableOutParameter(13, 100, Types.VARCHAR, 4000); // p_data_ini_falta
      cs.registerIndexTableOutParameter(14, 100, Types.VARCHAR, 4000); // p_data_fim_falta

      cs.registerOutParameter(15, Types.VARCHAR);  // p_msg_error

      cs.execute();

      result.setDescSobre(cs.getString(5));
      result.setDiasDireito(cs.getString(6));
      result.setDiasDescRh(cs.getString(7));
      result.setDiasNdescRh(cs.getString(8));
      result.setMsgError(cs.getString(15));

      var meses = (String[]) cs.getPlsqlIndexTable(9);
      var diasFalta = (String[]) cs.getPlsqlIndexTable(10);
      var valorDesc = (String[]) cs.getPlsqlIndexTable(11);
      var valorSalario = (String[]) cs.getPlsqlIndexTable(12);
      var dataIniFalta = (String[]) cs.getPlsqlIndexTable(13);
      var dataFimFalta = (String[]) cs.getPlsqlIndexTable(14);

      if (meses != null) {
        for (int i = 0; i < meses.length; i++) {
          faltasMensais.add(new BaixaMedicaFaltaMensalDTO(
              meses[i],
              safeGet(dataIniFalta, i),
              safeGet(dataFimFalta, i),
              safeGet(diasFalta, i),
              safeGet(valorSalario, i),
              safeGet(valorDesc, i)
          ));
        }
      }
      return null;
    });

    result.setFaltasMensais(faltasMensais);

    return result;
  }

  public Long getTiprelId(UUID colaboradorUuid) {
    var funcionario = funcionarioRepository.findByUuidOrThrow(colaboradorUuid);
    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (tiprel == null)
      throw IgrpResponseStatusException.badRequest("Colaborador sem tipo de relacionamento activo");
    return tiprel.getId();
  }

  private String safeGet(String[] arr, int i) {
    return (arr != null && i < arr.length) ? arr[i] : null;
  }
}
