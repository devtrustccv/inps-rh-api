package cv.inps.rh.funcionario.application.service.carreira;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoCarreiraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CarreiraWriteService {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final CarreiraMapper carreiraMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final FuncionarioRules funcionarioRules;

  public void novaCarreira(String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var currentDate = LocalDate.now();
    var currentDateMinusOneDay = currentDate.minusDays(1);

    // TODO 23/11/2025 17:39 criar novo contrato ?
    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario);

    var relacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    relacionamentoAtual.setDataFim(currentDateMinusOneDay);
    relacionamentoAtual.setEstActAdm(0);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defRemuneracao.forEach(obj -> {
      obj.setDataFim(currentDateMinusOneDay);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defPagamento.forEach(obj -> {
      obj.setDataFim(currentDateMinusOneDay);
      defPagamentoEntityRepository.save(obj);
    });

    var carreiraAtual = relacionamentoAtual.getCarreiraId();
    carreiraAtual.setDataFim(currentDate);
    carreiraEntityRepository.save(carreiraAtual);

    var novaCarreira = Objects.requireNonNull(carreiraMapper.toCarreira(dto, Estado.P));
    novaCarreira.setFunId(funcionario);
    novaCarreira.setObs("CARREIRA");
    carreiraEntityRepository.save(novaCarreira);

    var novoRelacionamento = contratuaisEntityMapper.toRelacionamento(dto, Estado.P);
    novoRelacionamento.setObs("MOBILIDADE- || TIPO_CARREIRA");
    novoRelacionamento.setDataInicio(currentDate);
    novoRelacionamento.setContratoId(contratoAtual);
    novoRelacionamento.setCarreiraId(novaCarreira);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoRelacionamento);

    if (dto.getSubsidios() != null && !dto.getSubsidios().isEmpty()) {
      var remList = dto.getSubsidios().stream()
          .map(s -> {
            var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
            obj.setObs("MOBILIDADE- || TIPO_CARREIRA");
            //obj.setCarreira id todo: nao tem campo na tabela
            return obj;
          })
          .toList();
      definicaoRemuneracaoEntityRepository.saveAll(remList);
    }

    var salario = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario);
    definicaoRemuneracaoEntityRepository.save(salario);

    if (dto.getEncargosDescontos() != null && !dto.getEncargosDescontos().isEmpty()) {
      var pagList = dto.getEncargosDescontos().stream()
          .map(e -> {
            var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
            def.setObs("MOBILIDADE- || TIPO_CARREIRA");
            return def;
          })
          .toList();
      defPagamentoEntityRepository.saveAll(pagList);
    }

    // TODO 23/11/2025 18:14 2 registo de IUR e INPS de DefPagamentoEntity

    var remun = new RemuneracaoTiprelEntity();
    remun.setEstado(Estado.P.name());
    remun.setUuid(UuidCreator.getTimeOrderedEpoch());
    remun.setRemId(salario);
    remun.setTiprelId(novoRelacionamento);
    remuneracaoTiprelEntityRepository.save(remun);

    var mobilidade = mobilidadeEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    // TODO 23/11/2025 18:24 use this mobilidade or create new one ?

    var validation = new ValidacaoEntity();
    validation.setTipoAccao("INSERT");
    validation.setReferenciaName("CARREIRA");
    validation.setReferenciaId(mobilidade.getId());
    validation.setTiprelId(novoRelacionamento);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    // TODO 23/11/2025 18:25 save log e log detalhe ?
  }

  @NotNull
  private DefinicaoRemuneracaoEntity getSalarioDefinicaoRemuneracaoEntity(DadosContratuaisReqDTO dto, FuncionarioEntity funcionario) {
    var salario = new DefinicaoRemuneracaoEntity();
    salario.setValor(dto.getSalario());
    salario.setEstado(Estado.P);
    salario.setObs("MOBILIDADE- || TIPO_CARREIRA");
    salario.setDataInicio(dto.getDataInicio());
    salario.setDataFim(dto.getDataFim());
    salario.setFunId(funcionario);
    salario.setUuid(UuidCreator.getTimeOrderedEpoch());
    return salario;
  }

  public void validarCarreira(String funcionarioId, ValidacaoCarreiraDTO dto) {

    var validacao = dto.getValidacao();
    if (!List.of("S", "N").contains(validacao))
      throw IgrpResponseStatusException.badRequest("Código de validação inválida: " + validacao + ". Deve ser S ou N.");

    var dados = dto.getDados();

    var estado = validacao.equals("S") ? Estado.A : Estado.I;

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var carreira = carreiraEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    carreira.setEstado(estado);
    carreiraEntityRepository.save(carreira);

    var relacionamento = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    relacionamento.setEstado(estado);
    tiposRelacionamentoEntityRepository.save(relacionamento);

    var definicoesRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesRemuneracao.forEach(obj -> {
      obj.setEstado(estado);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var definicoesPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesPagamento.forEach(obj -> {
      obj.setEstado(estado);
      defPagamentoEntityRepository.save(obj);
    });

    var remuneracoes = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(relacionamento, Estado.P.name());
    remuneracoes.forEach(obj -> {
      obj.setEstado(estado.name());
      remuneracaoTiprelEntityRepository.save(obj);
    });

    var validation = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamento, Estado.P, "CARREIRA");
    validation.setEstado(estado);
    validacaoEntityRepository.save(validation);
  }
}
