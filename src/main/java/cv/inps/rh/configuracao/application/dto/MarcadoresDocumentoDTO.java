package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Marcadores ${nome} usados no texto de um Documento de Saída (título, corpo e "Assinado por"). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MarcadoresDocumentoDTO {
  private String tipoDocumento;
  private List<String> marcadores;
}
