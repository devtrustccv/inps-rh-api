package cv.inps.rh.processamento.domain.service.baixamedica;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.processamento.application.dto.BaixaMedicaCalculoDTO;
import cv.inps.rh.processamento.application.dto.BaixaMedicaFaltaMensalDTO;
import cv.inps.rh.processamento.application.dto.BaixaMedicaReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaixaMedicaServiceWrite {

    private final FuncionarioEntityRepository funcionarioRepository;
    private final FuncionarioRules funcionarioRules;
    private final AbonosBeneficiosEntityRepository abonosRepository;
    private final AusenciaEntityRepository ausenciaRepository;
    private final FaltaEntityRepository faltaRepository;
    private final ParamSituacaoEntityRepository paramSituacaoRepository;
    private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheRepository;
    private final PedidoEntityRepository pedidoRepository;
    private final ValidacaoEntityRepository validacaoRepository;
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

        var paramSit = paramSituacaoRepository.findById(req.getTipoLicenca())
                .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo de licença inválido"));

        // 1. Calcular via procedure
        var calculo = chamarProcedure(tiprel.getId(), req.getDataInicio(), req.getDataFim(),
                req.getTipoLicenca(), req.getDataInicioFalta());

        if (calculo.getMsgError() != null && !calculo.getMsgError().isBlank())
            throw IgrpResponseStatusException.badRequest(calculo.getMsgError());

        // 2. Pedido
        var pedido = new PedidoEntity();
        pedido.setEstado(Estado.P.name());
        pedido.setTipoPedido(Referencia.BAIXA_MEDICA.name());
        pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
        pedido.setOrigem("RH");
        pedido.setEtapa("VALIDACAO");
        pedido = pedidoRepository.saveAndFlush(pedido);

        // 3. Gravar RH_T_ABONOS_BENEFICIOS
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

        // 4. Documentos comprovativos (array — padrão Dispensa/Ferias)
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

        // 5. Gravar RH_T_AUSENCIA se FLG_AUSENCIA = 1
        if (paramSit.getFlgAusencia() != null && paramSit.getFlgAusencia() == 1) {
            var ausencia = new AusenciaEntity();
            ausencia.setUuid(UuidCreator.getTimeOrderedEpoch());
            ausencia.setParamSitId(paramSit);
            ausencia.setFunId(funcionario);
            ausencia.setReferenciaName("RH_T_ABONOS_BENEFICIOS");
            ausencia.setReferenciaId(abono.getId());
            ausencia.setDataInicio(req.getDataInicio());
            ausencia.setDataFim(req.getDataFim());
            ausencia.setObs(req.getObservacao());
            ausencia.setEstado(Estado.P);
            ausenciaRepository.save(ausencia);
        }

        // 6. Gravar RH_T_FALTA — um registo por mês (dados dos arrays do procedure)
        final var pedidoFinal = pedido;
        final var tiprelFinal = tiprel;
        final var paramSitFinal = paramSit;
        for (var item : calculo.getFaltasMensais()) {
            var falta = new FaltaEntity();
            falta.setUuid(UuidCreator.getTimeOrderedEpoch());
            falta.setTiprelId(tiprelFinal);
            falta.setParamSitId(paramSitFinal);
            falta.setPedidoId(pedidoFinal);
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

        // 7. RH_T_VALIDACAO — via dadosContratuaisMapper (padrão do projecto)
        var validacao = dadosContratuaisMapper.toValidacaoInsert(
                TipoAcao.INSERT.name(), Referencia.BAIXA_MEDICA.name(), Estado.P);
        validacao.setReferenciaId(pedido.getId());
        validacao.setReferenciaUuid(pedido.getUuid());
        validacaoRepository.save(validacao);

        return Map.of(
                "pedidoId", pedido.getId(),
                "pedidoUuid", pedido.getUuid().toString(),
                "totalRegistos", calculo.getFaltasMensais().size()
        );
    }

    // ----------------------------------------------------------------
    // Validar / desvalidar — suporta ajuste de campos e documento (edit durante validação)
    // ----------------------------------------------------------------
    @Transactional
    public Map<String, Object> validar(String pedidoUuidStr, EstadoValidacao validar, BaixaMedicaReqDTO ajuste) {
        if (!StringUtils.hasText(pedidoUuidStr))
            throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");

        var pedidoUuid = UUID.fromString(pedidoUuidStr);
        var pedido = pedidoRepository.findByUuid(pedidoUuid)
                .orElseThrow(() -> IgrpResponseStatusException.badRequest("Pedido não encontrado"));

        var faltas = faltaRepository.findAllByPedidoId(pedido);
        if (faltas.isEmpty())
            throw IgrpResponseStatusException.badRequest("Pedido já validado ou sem registos pendentes");

        Estado estado = validar == EstadoValidacao.SIM ? Estado.A : Estado.I;

        // Actualizar estado dos documentos existentes
        var docsExistentes = documentoEntityRepository
                .findAllByReferenciaNameAndReferenciaUuid(
                        TableName.RH_T_ABONOS_BENEFICIOS.name(), pedido.getUuid());
        if (docsExistentes != null && !docsExistentes.isEmpty()) {
            docsExistentes.forEach(d -> d.setEstado(estado));
            documentoEntityRepository.saveAll(docsExistentes);
        }

        // Edit durante validação: ajuste de campos e novos documentos
        if (ajuste != null) {
            // Novos docs enviados na validação
            if (ajuste.getDocumentos() != null && !ajuste.getDocumentos().isEmpty()) {
                // Buscar abono pelo pedido (via referencia_id nas faltas)
                faltas.stream().findFirst().ifPresent(f -> {
                    var fun = f.getTiprelId() != null ? f.getTiprelId().getFunId() : null;
                    List<DocumentoEntity> novos = new ArrayList<>();
                    for (var d : ajuste.getDocumentos()) {
                        // Guardar referenciado ao pedido (UUID do pedido como referencia_uuid)
                        var doc = documentoMapper.toEntity(
                                d, estado,
                                TableName.RH_T_ABONOS_BENEFICIOS.name(),
                                pedido.getId(), pedido.getUuid(), 1L, fun);
                        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
                        novos.add(doc);
                    }
                    documentoEntityRepository.saveAll(novos);
                });
            }
        }

        // Actualizar estado das faltas
        faltas.forEach(f -> f.setEstado(estado));
        faltaRepository.saveAll(faltas);

        // Actualizar validacao
        funcionarioRules.getValidacaoPendenteByReferenciaUuid(pedido.getUuid(), TipoAcao.INSERT, Referencia.BAIXA_MEDICA)
                .ifPresent(v -> {
                    v.setEstado(estado);
                    validacaoRepository.save(v);
                });

        pedido.setEstado(estado.name());
        pedidoRepository.save(pedido);

        return Map.of(
                "pedidoId", pedido.getId(),
                "pedidoUuid", pedido.getUuid().toString(),
                "estado", estado.name(),
                "totalRegistos", faltas.size()
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

            cs.registerIndexTableOutParameter(9,  100, Types.VARCHAR, 4000); // p_meses
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

            var meses        = (String[]) cs.getPlsqlIndexTable(9);
            var diasFalta    = (String[]) cs.getPlsqlIndexTable(10);
            var valorDesc    = (String[]) cs.getPlsqlIndexTable(11);
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
