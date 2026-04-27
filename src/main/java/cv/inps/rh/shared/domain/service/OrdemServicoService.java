package cv.inps.rh.shared.domain.service;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrdemServicoService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules rules;
  private final ParamDocOutputEntityRepository paramDocOutputEntityRepository;

  public Context fimComissaoServico(String funcionarioId) {

    var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var tipoDoc = getByDocType("os-fim-comissao-servico");

    var ctx = new Context();

    var currentContract = rules.getContratoComMaiorVersao(fun.getUuid());
    var values = Map.of(
        "nomeColaborador", fun.getNome(),
        "cargoColaborador", currentContract.getVinculoId().getNome(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(currentContract.getDataInicio())
    );

    ctx.setVariable("numeroOrdem", "1");
    ctx.setVariable("assunto", tipoDoc.getTitulo());
    ctx.setVariable("conteudo", StringSubstitutor.replace(tipoDoc.getCorpo(), values));
    ctx.setVariable("dataEmissao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()));
    ctx.setVariable("nomePresidente", "Mário Rui Fernandes"); // TODO 22/04/2026 21:33 set this in BD

    return ctx;
  }

  private ParamDocOutputEntity getByDocType(String type) {
    return paramDocOutputEntityRepository.findByTipoDocumentoAndEstado(type, Estado.A.name()).orElseThrow();
  }

}
