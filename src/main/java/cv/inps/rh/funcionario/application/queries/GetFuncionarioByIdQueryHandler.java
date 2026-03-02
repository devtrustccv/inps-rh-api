package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

@Component
public class GetFuncionarioByIdQueryHandler
    implements QueryHandler<GetFuncionarioByIdQuery, ResponseEntity<FuncionarioResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncionarioByIdQueryHandler.class);

  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DocumentoMapper documentoMapper;

  public GetFuncionarioByIdQueryHandler(FuncionarioMapper funcionarioMapper,
                                        FuncionarioEntityRepository funcionarioEntityRepository, DadosContratuaisMapper dadosContratuaisMapper,
                                        FuncionarioRules funcionarioRules, DocumentoEntityRepository documentoEntityRepository, DocumentoMapper documentoMapper) {

    this.funcionarioMapper = funcionarioMapper;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.dadosContratuaisMapper = dadosContratuaisMapper;
    this.funcionarioRules = funcionarioRules;
    this.documentoEntityRepository = documentoEntityRepository;
    this.documentoMapper = documentoMapper;
  }

  @Transactional(readOnly = true)
  @IgrpQueryHandler
  public ResponseEntity<FuncionarioResponseDTO> handle(GetFuncionarioByIdQuery query) {

    LOGGER.info("Handling GetFuncionarioByIdQuery: {}", query);

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getId()).valor());

    var funcionarioResponseDTO = funcionarioMapper.toResponseDTO(funcionario);

    var documentos = documentoEntityRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_FUNCIONARIOS.name(), funcionario.getUuid());

    if (!CollectionUtils.isEmpty(documentos)) {
      var documentosDTO = documentoMapper.toAnexoRespDTOList(documentos);
      funcionarioResponseDTO.setAnexos(documentosDTO);
    }

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId());

    var dcr = dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos,
        remuneracoes);
    funcionarioResponseDTO.setDadosContratuais(dcr);

    return ResponseEntity.ok(funcionarioResponseDTO);

  }

}
