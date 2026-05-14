package cv.inps.rh.shared.application.service;

import cv.igrp.platform.filemanager.StorageService;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.openpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class OrdemServicoPdfService {

  private final TemplateEngine templateEngine;
  private final StorageService storageService;

  @Value("${igrp.minio.endpoint}")
  private String endpoint;

  @Value("${igrp.minio.port}")
  private int port;

  @Value("${igrp.minio.bucket-name}")
  private String bucketName;

  public OrdemServicoPdfService(TemplateEngine templateEngine, StorageService storageService) {
    this.templateEngine = templateEngine;
    this.storageService = storageService;
  }

  @SneakyThrows
  public String generate(Context context) {

    var htmlContent = templateEngine.process("os-general", context);

    try (var outputStream = new ByteArrayOutputStream()) {

      var renderer = new ITextRenderer();
      renderer.setDocumentFromString(htmlContent);
      renderer.layout();
      renderer.createPDF(outputStream);

      byte[] bytes = outputStream.toByteArray();

      var uniqueFilename = buildUniqueFilename("ordem-servico.pdf");

      storageService.uploadFile(
          bytes,
          uniqueFilename,
          "application/pdf"
      );

      return uniqueFilename;

    } catch (DocumentException | IOException e) {
      throw new IllegalStateException("Erro ao gerar PDF da Ordem de Serviço", e);
    }
  }

  @SneakyThrows
  public String generate(Map<String, Object> model) {

    var context = new Context();
    context.setVariables(model);

    var htmlContent = templateEngine.process("ordem-servico", context);

    try (var outputStream = new ByteArrayOutputStream()) {

      var renderer = new ITextRenderer();
      renderer.setDocumentFromString(htmlContent);
      renderer.layout();
      renderer.createPDF(outputStream);

      byte[] bytes = outputStream.toByteArray();

      var uniqueFilename = buildUniqueFilename("demo.pdf");

      storageService.uploadFile(
          bytes,
          uniqueFilename,
          "application/pdf"
      );

      return uniqueFilename;

    } catch (DocumentException | IOException e) {
      throw new IllegalStateException("Erro ao gerar PDF da Ordem de Serviço", e);
    }
  }

  private String buildUniqueFilename(String name) {

    var baseName = FilenameUtils.getBaseName(name);

    var extension = FilenameUtils.getExtension(name);

    return "%s_%s.%s".formatted(
        baseName,
        System.currentTimeMillis(),
        extension
    );
  }
}
