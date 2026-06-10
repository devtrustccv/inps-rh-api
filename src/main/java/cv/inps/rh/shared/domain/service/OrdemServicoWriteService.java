package cv.inps.rh.shared.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OrdemServicoWriteService {

    private final OrdemServicoEntityRepository ordemServicoRepository;

    public OrdemServicoEntity criar(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprel, String tipoOrdemServico) {
        if (!StringUtils.hasText(tipoOrdemServico)) return null;
        var os = new OrdemServicoEntity();
        os.setFunId(funcionario);
        os.setTiprelId(tiprel);
        os.setReferente(tipoOrdemServico);
        os.setDescricao(tipoOrdemServico + " - " + (funcionario.getNome() != null ? funcionario.getNome() : ""));
        os.setNuOrdem("1"); // TODO: usar sequência real
        os.setEstado(Estado.A);
        os.setUuid(UuidCreator.getTimeOrderedEpoch());
        return ordemServicoRepository.save(os);
    }

    public OrdemServicoEntity criar(FuncionarioEntity funcionario, TiposRelacionamentoEntity tiprel,
                                    String tipoOrdemServico, ValidacaoEntity validacao, String descricao) {
        var os = criar(funcionario, tiprel, tipoOrdemServico);
        if (os == null) return null;
        if (validacao != null) os.setValidacaoId(validacao);
        if (StringUtils.hasText(descricao)) os.setDescricao(descricao);
        return ordemServicoRepository.save(os);
    }
}