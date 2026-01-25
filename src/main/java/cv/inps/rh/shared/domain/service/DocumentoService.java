package cv.inps.rh.shared.domain.service;

import cv.igrp.framework.filemanager.minio.IGRPFileStorageException;
import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentoService {

  private static final String PATH_SEPARATOR = "/";
  private final StorageService storageService;

  @Value("${igrp.minio.endpoint}")
  private String endpoint;

  @Value("${igrp.minio.port}")
  private int port;

  @Value("${igrp.minio.bucket-name}")
  private String bucketName;

  public DocumentoService(StorageService storageService) {
    this.storageService = storageService;
  }

  @SneakyThrows
  public ResponseEntity<FileResponseDTO> save(MultipartFile file) {

    if (file == null || file.isEmpty())
      throw IgrpResponseStatusException.badRequest("Invalid file submitted");

    var uniqueFilename = buildUniqueFilename(file.getOriginalFilename());

    storageService.uploadFile(file.getBytes(), uniqueFilename, file.getContentType());

    var fileResponse = new FileResponseDTO(uniqueFilename, file.getOriginalFilename());

    return ResponseEntity.ok(fileResponse);
  }

  @SneakyThrows
  public ResponseEntity<FileResponseDTO> savePublicFile(String path, MultipartFile file) {

    if (file == null || file.isEmpty())
      throw IgrpResponseStatusException.badRequest("Invalid file submitted");

    var pathProcessed = path.replace(".", "/");

    var uniqueFilename = pathProcessed + PATH_SEPARATOR + buildUniqueFilename(file.getOriginalFilename());

    storageService.uploadPublicFile(file.getBytes(), uniqueFilename, file.getContentType());

    var fileId = "%s:%s/%s/%s".formatted(endpoint, port, bucketName, uniqueFilename);

    var fileResponse = new FileResponseDTO(uniqueFilename, file.getOriginalFilename());

    return ResponseEntity.ok(fileResponse);
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

  @SneakyThrows
  public ResponseEntity<FileUrl> getPresignedLink(String fileId) {
    try {

      var url = storageService.getFileUrl(fileId);

      var fileUrl = new FileUrl(url);

      return ResponseEntity.ok(fileUrl);

    } catch (IGRPFileStorageException e) {
      throw IgrpResponseStatusException.badRequest(e.getMessage());
    }
  }

  public record FileUrl(
      String fileId
  ) {

  }

  public record FileResponseDTO(
      String fileId, String fileDescription
  ) {

  }

}
