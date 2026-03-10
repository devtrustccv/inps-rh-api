package cv.inps.rh.progressaopromocao.domain.service.engine.rule;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.MediaResultado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

  private final AvaliacaoEntityRepository repository;

  public MediaResultado calcularMedia(FuncionarioEntity fun, int anos) {

    var evaluations = repository.findUltimasAvaliacoes(
        fun.getId(),
        PageRequest.of(0, anos)
    );
    if (evaluations.size() < anos)
      return MediaResultado.invalido();

    var media = evaluations.stream()
        .mapToDouble(AvaliacaoEntity::getAvaliacaoFinal)
        .average()
        .orElse(0);

    var abaixo50 = evaluations.stream().anyMatch(a -> a.getAvaliacaoFinal() < 50);

    var elegivelProgressao = media >= 60 && !abaixo50;

    var elegivelPromocao = media >= 90;

    return new MediaResultado(media, elegivelProgressao, elegivelPromocao);
  }
}
