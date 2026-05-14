package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.PccsRequestDTO;
import cv.inps.rh.configuracao.application.dto.PccsResponseDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

@Transactional
@Service("pccs_type")
public class PccsService extends ConfigurationProcess<PccsRequestDTO> {

    private final ParamPccsEntityRepository pccsRepository;
    private final ParamCarreiraEntityRepository carreiraRepository;
    private final ParamCargoEntityRepository cargoRepository;
    private final ParamEscalaoEntityRepository escalaoRepository;
    private final ParamCategoriaEntityRepository categoriaRepository;

    public PccsService(
        Validator validator,
        ObjectMapper jsonMapper,
        ParamPccsEntityRepository pccsRepository,
        ParamCarreiraEntityRepository carreiraRepository,
        ParamCargoEntityRepository cargoRepository,
        ParamEscalaoEntityRepository escalaoRepository,
        ParamCategoriaEntityRepository categoriaRepository
    ) {
        super(validator, jsonMapper, PccsRequestDTO.class);
        this.pccsRepository = pccsRepository;
        this.carreiraRepository = carreiraRepository;
        this.cargoRepository = cargoRepository;
        this.escalaoRepository = escalaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Object create(PccsRequestDTO dto) {
        // busca o PCCS ativo antes de criar o novo para evitar auto-referência
        var anteriorOpt = pccsRepository.findFirstByEstadoOrderByDataInicioDesc(Estado.A);

        var pccs = new ParamPccsEntity();
        pccs.setUuid(UuidCreator.getTimeOrderedEpoch());
        pccs.setEstado(Estado.A);
        pccs.setDescricao(dto.getDescricao().trim());
        pccs.setDataInicio(dto.getDataInicio());
        pccs.setDataFim(dto.getDataFim());
        pccs.setFlgCopiaAnterior(dto.getFlgCopiaAnterior());
        pccs.setFlgFecharAnterior(dto.getFlgFecharAnterior());
        pccsRepository.save(pccs);

        if (anteriorOpt.isPresent()) {
            var anterior = anteriorOpt.get();

            if (Integer.valueOf(1).equals(dto.getFlgCopiaAnterior())) {
                copiarDadosDoPccsAnterior(anterior, pccs);
            }

            if (Integer.valueOf(1).equals(dto.getFlgFecharAnterior())) {
                fecharPccsAnterior(anterior);
            }
        }

        return new ConfigurationResponseIdDTO(pccs.getUuid().toString());
    }

    @Override
    public Object update(String uuid, PccsRequestDTO dto) {
        var pccs = pccsRepository.findByUuidOrThrow(UUID.fromString(uuid));
        pccs.setDescricao(dto.getDescricao().trim());
        pccs.setDataInicio(dto.getDataInicio());
        pccs.setDataFim(dto.getDataFim());
        if (StringUtils.hasText(dto.getEstado())) {
            pccs.setEstado(Estado.valueOf(dto.getEstado()));
        }
        pccsRepository.save(pccs);
        return "";
    }

    @Override
    public Object read(String uuid) {
        var pccs = pccsRepository.findByUuidOrThrow(UUID.fromString(uuid));
        return buildResponse(pccs);
    }

    @Override
    public Object list(Map<String, String> filters) {
        var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

        var data = pccsRepository.findAll(pageable);

        var response = new WrapperListDTO();
        PageMapper.fillPagination(data, response);
        response.setContent(data.getContent().stream()
            .map(this::buildResponse)
            .toList());
        return response;
    }

    @Override
    public void delete(String uuid) {
        var pccs = pccsRepository.findByUuidOrThrow(UUID.fromString(uuid));
        pccs.setEstado(Estado.I);
        pccsRepository.save(pccs);

        var carreiras = carreiraRepository.findAllByPccsId(pccs);
        for (var carreira : carreiras) {
            carreira.setEstado(Estado.I);
            carreiraRepository.save(carreira);

            cargoRepository.findAllByParamCarrId(carreira).forEach(cargo -> {
                cargo.setEstado(Estado.I);
                cargoRepository.save(cargo);
            });

            escalaoRepository.findAllByParamCarrId(carreira).forEach(escalao -> {
                escalao.setEstado(Estado.I);
                escalaoRepository.save(escalao);
            });
        }
    }

    private void fecharPccsAnterior(ParamPccsEntity anterior) {
        anterior.setEstado(Estado.I);
        pccsRepository.save(anterior);

        var carreiras = carreiraRepository.findAllByPccsId(anterior);
        for (var carreira : carreiras) {
            carreira.setEstado(Estado.I);
            carreiraRepository.save(carreira);

            cargoRepository.findAllByParamCarrId(carreira).forEach(cargo -> {
                cargo.setEstado(Estado.I);
                cargoRepository.save(cargo);
            });

            escalaoRepository.findAllByParamCarrId(carreira).forEach(escalao -> {
                escalao.setEstado(Estado.I);
                escalaoRepository.save(escalao);
            });
        }
    }

    private void copiarDadosDoPccsAnterior(ParamPccsEntity anterior, ParamPccsEntity novopccs) {
        var carreiras = carreiraRepository.findAllByPccsId(anterior);
        for (var carreiraOrigem : carreiras) {
            var novaCarreira = new ParamCarreiraEntity();
            novaCarreira.setUuid(UuidCreator.getTimeOrderedEpoch());
            novaCarreira.setEstado(Estado.A);
            novaCarreira.setNome(carreiraOrigem.getNome());
            novaCarreira.setCodigo(carreiraOrigem.getCodigo());
            novaCarreira.setPccsId(novopccs);
            carreiraRepository.save(novaCarreira);

            copiarCategorias(carreiraOrigem, novaCarreira);
            copiarCargos(carreiraOrigem, novaCarreira);
            copiarEscaloes(carreiraOrigem, novaCarreira);
        }
    }

    private void copiarCategorias(ParamCarreiraEntity origem, ParamCarreiraEntity destino) {
        var categorias = categoriaRepository.findAllByEstadoAndParamCarrId(Estado.A, origem);
        for (var cat : categorias) {
            var novaCategoria = new ParamCategoriaEntity();
            novaCategoria.setUuid(UuidCreator.getTimeOrderedEpoch());
            novaCategoria.setEstado(Estado.A);
            novaCategoria.setParamCarrId(destino);
            novaCategoria.setNome(cat.getNome());
            novaCategoria.setCodigo(cat.getCodigo());
            categoriaRepository.save(novaCategoria);
        }
    }

    private void copiarCargos(ParamCarreiraEntity origem, ParamCarreiraEntity destino) {
        var cargos = cargoRepository.findAllByParamCarrId(origem);
        for (var cargo : cargos) {
            var novoCargo = new ParamCargoEntity();
            novoCargo.setUuid(UuidCreator.getTimeOrderedEpoch());
            novoCargo.setEstado(Estado.A);
            novoCargo.setNome(cargo.getNome());
            novoCargo.setNomeNormalizado(cargo.getNomeNormalizado());
            novoCargo.setDirigente(cargo.getDirigente());
            novoCargo.setParamCarrId(destino);
            cargoRepository.save(novoCargo);
        }
    }

    private void copiarEscaloes(ParamCarreiraEntity origem, ParamCarreiraEntity destino) {
        var escaloes = escalaoRepository.findAllByParamCarrId(origem);
        for (var escalao : escaloes) {
            var novoEscalao = new ParamEscalaoEntity();
            novoEscalao.setUuid(UuidCreator.getTimeOrderedEpoch());
            novoEscalao.setEstado(Estado.A);
            novoEscalao.setCodigo(escalao.getCodigo());
            novoEscalao.setParamCarrId(destino);
            novoEscalao.setParamCategoriaId(escalao.getParamCategoriaId());
            novoEscalao.setNivelReferencia(escalao.getNivelReferencia());
            novoEscalao.setEscalao(escalao.getEscalao());
            novoEscalao.setValor(escalao.getValor());
            novoEscalao.setDataInicio(escalao.getDataInicio());
            escalaoRepository.save(novoEscalao);
        }
    }

    private Object buildResponse(ParamPccsEntity pccs) {
        var dto = new PccsResponseDTO();
        dto.setId(pccs.getUuid() != null ? pccs.getUuid().toString() : String.valueOf(pccs.getId()));
        dto.setDescricao(pccs.getDescricao());
        dto.setDataInicio(pccs.getDataInicio());
        dto.setDataFim(pccs.getDataFim());
        dto.setFlgCopiaAnterior(pccs.getFlgCopiaAnterior());
        dto.setFlgFecharAnterior(pccs.getFlgFecharAnterior());
        dto.setEstado(pccs.getEstado().getCode());
        dto.setEstadoDescricao(pccs.getEstado().getDescription());
        return dto;
    }
}
