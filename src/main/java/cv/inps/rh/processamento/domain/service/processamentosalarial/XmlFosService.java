package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.shared.infrastructure.persistence.repository.DetalheXmlFosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FosEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class XmlFosService {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final FosEntityRepository fosEntityRepository;
  private final DetalheXmlFosEntityRepository detalheXmlFosEntityRepository;

  public String buildXml(Long idXml) {

    var header = fosEntityRepository.findByIdOrThrow(idXml);

    var details = detalheXmlFosEntityRepository.findDtosByIdXmlFos(idXml)
        .stream()
        .map(det -> "<linha nu_segurado=\"%s\" nome_segurado=\"%s\" nu_dias_trabalho=\"%s\" vl_remuneracao=\"%s\" tp_remuneracao=\"%s\" />".formatted(
            safe(det.nuSegurado()),
            safe(det.nomeFuncionario()),
            safe(det.nuTrabMan()),
            safe(det.vlRemunMan()),
            safe(det.tipo())))
        .collect(Collectors.joining("\n"));

    return """
        <?xml version="1.0" encoding="UTF-8" ?>
        <fos xmlns="urn:inps:fos:v1" versao="1.0">
           <tp_edicao>%s</tp_edicao>
           <ano_ref>%s</ano_ref>
           <mes_ref>%s</mes_ref>
           <dt_entrega>%s</dt_entrega>
           <nu_contribuinte>%s</nu_contribuinte>
           <nome_contribuinte>INSTITUTO NACIONAL DE PREVIDENCIA SOCIAL</nome_contribuinte>
           <linhas>
        %s
           </linhas>
           <total_remuneracoes>%s</total_remuneracoes>
           <total_contribuicoes>%s</total_contribuicoes>
           <obs>%s</obs>
        </fos>
        """.formatted(
        safe(header.getTpEntrega()),
        safe(header.getAno()),
        safe(header.getMes()),
        LocalDate.now().format(DATE_FORMATTER),
        safe(header.getNuContribuinte()),
        details,
        safe(header.getTtRemuneracao()),
        safe(header.getTtContribuicao()),
        safe(header.getObs())
    );
  }

  private String safe(String value) {
    return value != null ? value : "";
  }
}
