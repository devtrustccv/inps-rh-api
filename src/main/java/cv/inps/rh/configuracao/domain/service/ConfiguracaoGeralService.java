package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.configuracao.application.dto.ConfiguracaoGeralDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeParametroEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FusoHorarioUpsEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FusoHorarioUpsEntityRepository;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service("configuracao_geral_type")
public class ConfiguracaoGeralService extends ConfigurationProcess<ConfiguracaoGeralDTO> {

  private final AssiduidadeParametroEntityRepository repository;
  private final FusoHorarioUpsEntityRepository fusoHorarioUpsEntityRepository;

  public ConfiguracaoGeralService(
      AssiduidadeParametroEntityRepository repository,
      Validator validator,
      ObjectMapper jsonMapper, FusoHorarioUpsEntityRepository fusoHorarioUpsEntityRepository
  ) {
    super(validator, jsonMapper, ConfiguracaoGeralDTO.class);
    this.repository = repository;
    this.fusoHorarioUpsEntityRepository = fusoHorarioUpsEntityRepository;
  }

  @Override
  public Object create(ConfiguracaoGeralDTO dto) {

    var active = repository.findAllByEstado(Estado.A.getCode());
    active.forEach(e -> {
      e.setEstado(Estado.E.getCode());
      e.setDtFim(LocalDate.now());
      e.setUsrFim(1L);
    });
    repository.saveAll(active);

    var e = new AssiduidadeParametroEntity();
    e.setDiaria(dto.getJornadaDiaria());
    e.setHInicio(dto.getJornadaDiariaInicio());
    e.setHFim(dto.getJornadaDiariaFim());
    e.setAlHoraInicio(dto.getAlmocoInicio());
    e.setAlHoraFim(dto.getAlmocoFim());
    e.setAlDuracao(dto.getAlmocoDuracao());
    e.setTAtraso(dto.getPrimeiroAtrazo());
    e.setTAtrasoApli1(dto.getFaltaAplicadaPrimeiroAtrazo());
    e.setTAtraso2(dto.getSegundoAtrazo());
    e.setTAtrasoApli2(dto.getFaltaAplicadaSegundoAtrazo());
    e.setTMovIrregular(dto.getMovimentoIrregular());
    e.setHePartirDe(dto.getHoraAPartirDe());
    e.setHeDiaria(dto.getLimiteDiario());
    e.setHeValorDutil(dto.getPercentagemDiasUteis());
    e.setHeValorDnutil(dto.getPercentagemDiasNaoUteis());
    e.setPrazoJustifFalta(dto.getPeriodoLimiteJustFalta());
    e.setPrazoJustifAusencia(dto.getPrazoLimiteJustAusencia());

    // TODO 15/01/2026 20:30 verify this fields
    //e.setTDispensa();
    //e.setCAtraso();
    //e.setTaCompensacao();
    //e.setHeMensal();
    //e.setHeAnual();

    e.setFaltaMaxMarcacao(dto.getNumeroMaximoMarcAno());
    e.setFaltaDireitoAnula(dto.getDireitoAnual());
    e.setFaltaDataVencimento(dto.getDataVencimentoFerias());
    e.setFaltaMesMaximoAno1(dto.getNumeroMesesLimiteTrabalho());
    e.setMaxAcumulacao(dto.getMaximoAcumulacao());
    e.setEstado(Estado.A.getCode());
    e.setDtRegisto(LocalDate.now());
    e.setUsrRegisto(1L);

    var saved = repository.save(e);

    var timeZones = dto.getFusoHorario();
    if (timeZones != null && !timeZones.isEmpty()) {

      var tz = timeZones.stream().map(obj -> {
        var fuso = new FusoHorarioUpsEntity();
        fuso.setIdParametrizacao(saved.getId());
        fuso.setIdUps(obj.upsId());
        fuso.setFuso(obj.fuso());
        return fuso;
      }).toList();

      fusoHorarioUpsEntityRepository.saveAll(tz);
    }

    return buildResponse(saved);
  }

  @Override
  public Object update(String id, ConfiguracaoGeralDTO dto) {
    return create(dto);
  }

  @Override
  public Object read(String id) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    return buildResponse(entity);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {
    var data = repository.findAll();
    return data.stream().map(this::buildResponse).collect(Collectors.toList());
  }

  @Override
  public void delete(String id) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    entity.setEstado(Estado.E.getCode());
    entity.setDtFim(LocalDate.now());
    entity.setUsrFim(1L);
    repository.save(entity);
  }

  @NotNull
  private ConfiguracaoGeralDTO buildResponse(AssiduidadeParametroEntity e) {
    var r = new ConfiguracaoGeralDTO();
    r.setId(e.getId().toString());
    r.setJornadaDiaria(e.getDiaria());
    r.setJornadaDiariaInicio(e.getHInicio());
    r.setJornadaDiariaFim(e.getHFim());
    r.setAlmocoInicio(e.getAlHoraInicio());
    r.setAlmocoFim(e.getAlHoraFim());
    r.setAlmocoDuracao(e.getAlDuracao());
    r.setPrimeiroAtrazo(e.getTAtraso());
    r.setFaltaAplicadaPrimeiroAtrazo(e.getTAtrasoApli1());
    r.setSegundoAtrazo(e.getTAtraso2());
    r.setFaltaAplicadaSegundoAtrazo(e.getTAtrasoApli2());
    r.setMovimentoIrregular(e.getTMovIrregular());
    r.setHoraAPartirDe(e.getHePartirDe());
    r.setLimiteDiario(e.getHeDiaria());
    r.setPercentagemDiasUteis(e.getHeValorDutil());
    r.setPercentagemDiasNaoUteis(e.getHeValorDnutil());
    r.setPeriodoLimiteJustFalta(e.getPrazoJustifFalta());
    r.setPrazoLimiteJustAusencia(e.getPrazoJustifAusencia());
    r.setNumeroMaximoMarcAno(e.getFaltaMaxMarcacao());
    r.setDireitoAnual(e.getFaltaDireitoAnula());
    r.setDataVencimentoFerias(e.getFaltaDataVencimento());
    r.setNumeroMesesLimiteTrabalho(e.getFaltaMesMaximoAno1());
    r.setMaximoAcumulacao(e.getMaxAcumulacao());
    r.setEstado(e.getEstado());
    r.setDataRegisto(e.getDtRegisto());
    r.setUtilizadoRegisto(e.getUsrRegisto()+"");
    return r;
  }

}
