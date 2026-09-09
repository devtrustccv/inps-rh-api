package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/** Importação do mapa de férias a partir do Excel-modelo do RH. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportarMapaFeriaCommand implements Command {

  @NotNull(message = "The field <ficheiro> is required")
  private MultipartFile ficheiro;

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
}
