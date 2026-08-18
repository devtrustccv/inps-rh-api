package cv.inps.rh.shared.application.service;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoDetalheEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Alimenta a grelha "Detalhe de alterações".
 *
 * <p>Vive em {@code shared} e não num módulo porque a tabela é transversal: além do DOSSIÊ, é
 * referenciada pelas specs de GESTÃO ASSIDUIDADE e PROCESSAMENTO SALARIAL. Um único endpoint serve
 * qualquer ecrã que tenha um uuid de validação.
 */
@Service
@RequiredArgsConstructor
public class ValidacaoDetalheReadService {

  private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

  private final ValidacaoDetalheEntityRepository validacaoDetalheEntityRepository;

  /**
   * Regra da spec (linha 1612): VALIDACAO_ID = ID de RH_T_VALIDACAO. Aceita o uuid em vez do id
   * porque é o identificador que os endpoints deste projeto expõem para fora.
   */
  @Transactional(readOnly = true)
  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    return validacaoDetalheEntityRepository
        .findByValidacaoId_UuidOrderByTabelaNameAscIdAsc(validacaoUuid)
        .stream()
        .map(this::toDto)
        .toList();
  }

  private ValidacaoDetalheDTO toDto(ValidacaoDetalheEntity entidade) {
    var dto = new ValidacaoDetalheDTO();
    dto.setCampoAlterado(entidade.getCampoAlterado());
    dto.setValorAnterior(entidade.getValorAnterior());
    dto.setValorNovo(entidade.getValorNovo());
    dto.setAlteradoPor(entidade.getCreatedBy());
    dto.setDataAlteracao(entidade.getCreatedDate() == null ? null : entidade.getCreatedDate().format(DATA_HORA));
    dto.setTabelaName(entidade.getTabelaName());
    return dto;
  }
}
