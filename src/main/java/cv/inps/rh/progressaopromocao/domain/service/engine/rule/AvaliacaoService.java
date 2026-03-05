package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

  private final AvaliacaoRepository repository;

  public MediaResultado calcularMedia(FuncionarioEntity fun, int anos) {

    var avaliacoes = repository.findUltimasAvaliacoes(
        fun.getId(),
        PageRequest.of(0, anos)
    );

    if (avaliacoes.size() < anos)
      return MediaResultado.invalido();

    var media = avaliacoes.stream()
        .mapToDouble(AvaliacaoEntity::getAvaliacaoFinal)
        .average()
        .orElse(0);

    var abaixo50 = avaliacoes.stream()
        .anyMatch(a -> a.getAvaliacaoFinal() < 50);

    var elegivelProg = media >= 60 && !abaixo50;

    var elegivelPromo = media >= 90;

    return new MediaResultado(media, elegivelProg, elegivelPromo);
  }
}
