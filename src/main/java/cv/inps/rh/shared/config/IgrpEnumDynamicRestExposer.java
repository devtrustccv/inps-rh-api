package cv.inps.rh.shared.config;

import cv.igrp.framework.core.data.EnumItem;
import cv.igrp.framework.core.domain.IgrpEnum;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

@Component
@ConditionalOnProperty(name = "igrp.enum.exposer.enabled", havingValue = "true")
public class IgrpEnumDynamicRestExposer implements OpenApiCustomizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(IgrpEnumDynamicRestExposer.class);
  private static final String ENUM_BASE_PACKAGE = "cv.inps.rh";

  private final RequestMappingHandlerMapping handlerMapping;

  private Map<String, RegisteredEnum> enumsByName = Map.of();

  @Value("${igrp.enum.exposer.path}")
  private String igrpEnumExposerPath;

  private String normalizedExposerPath;

  public IgrpEnumDynamicRestExposer(
      @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping
  ) {
    this.handlerMapping = handlerMapping;
  }

  @PostConstruct
  public void registerEnumEndpoints() throws Exception {

    normalizedExposerPath = normalizePath(igrpEnumExposerPath);

    var scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(IgrpEnum.class));

    var handlerMethod = IgrpEnumDynamicRestExposer.class
        .getDeclaredMethod("handleEnumRequest", HttpServletRequest.class);

    var registeredEnums = new HashMap<String, RegisteredEnum>();
    var enumClassNames = scanner.findCandidateComponents(ENUM_BASE_PACKAGE).stream()
        .map(bean -> bean.getBeanClassName())
        .filter(Objects::nonNull)
        .sorted()
        .toList();

    for (var enumClassName : enumClassNames) {

      var clazz = Class.forName(enumClassName);

      if (!clazz.isEnum() || !IgrpEnum.class.isAssignableFrom(clazz)) continue;

      var endpointName = clazz.getSimpleName();
      if (registeredEnums.containsKey(endpointName)) {
        LOGGER.warn("[IgrpEnum] Duplicate enum name '{}' found ({}). Skipping registration.", endpointName, clazz.getName());
        continue;
      }

      @SuppressWarnings("unchecked")
      var enumClass = (Class<? extends Enum<?>>) clazz;

      registeredEnums.put(endpointName, new RegisteredEnum(enumClass, buildEnumItems(enumClass)));
      registerHandlerForEnum(endpointName, handlerMethod);

      LOGGER.debug("[IgrpEnum] Registered dynamic enum endpoint for {}", clazz.getSimpleName());
    }

    enumsByName = Map.copyOf(registeredEnums);
    LOGGER.info("[IgrpEnum] Total enums registered: {}", enumsByName.size());
  }

  private void registerHandlerForEnum(String endpointName, Method handlerMethod) {

    var mappingInfo = RequestMappingInfo
        .paths(endpointPath(endpointName))
        .methods(RequestMethod.GET)
        .build();

    handlerMapping.registerMapping(mappingInfo, this, handlerMethod);
  }

  @Override
  public void customise(OpenAPI openAPI) {
    enumsByName.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> registerSwaggerPathForEnum(openAPI, entry.getKey(), entry.getValue().enumClass()));
  }

  private void registerSwaggerPathForEnum(
      OpenAPI openAPI,
      String endpointName,
      Class<? extends Enum<?>> enumClass
  ) {
    try {

      var operation = buildOperation(enumClass);

      openAPI.path(endpointPath(endpointName), new PathItem().get(operation));

    } catch (Exception e) {
      LOGGER.warn("Failed to register enum '{}' in OpenAPI", endpointName, e);
    }
  }

  private Operation buildOperation(Class<? extends Enum<?>> enumClass) {
    return new Operation()
        .summary("Get values for enum `%s`".formatted(enumClass.getSimpleName()))
        .addTagsItem("iGRP Enums")
        .description("Returns all possible values of the enum `%s` with `code` and `description` fields.".formatted(enumClass.getName()))
        .responses(new ApiResponses()
            .addApiResponse("200", new ApiResponse().description("Successful operation"))
            .addApiResponse("404", new ApiResponse().description("Enum not found"))
        );
  }

  /**
   * Generic handler for all dynamically registered enum endpoints.
   */
  @ResponseBody
  public List<EnumItem<String>> handleEnumRequest(HttpServletRequest request) {

    var path = request.getRequestURI();
    var enumName = path.substring(path.lastIndexOf('/') + 1);

    var registeredEnum = enumsByName.get(enumName);
    if (registeredEnum == null)
      throw IgrpResponseStatusException.notFound("Enum not found for name: %s".formatted(enumName));

    return registeredEnum.items();
  }

  private List<EnumItem<String>> buildEnumItems(Class<? extends Enum<?>> enumClass) {
    return Arrays.stream(enumClass.getEnumConstants())
        .map(constant -> {
          var igrpEnum = (IgrpEnum<?>) constant;
          var code = igrpEnum.getCode();
          if (!(code instanceof String stringCode)) {
            throw new IllegalStateException(
                "Enum %s must implement IgrpEnum<String>".formatted(enumClass.getName())
            );
          }
          return new EnumItem<>(stringCode, igrpEnum.getDescription());
        })
        .toList();
  }

  private String normalizePath(String path) {
    var normalizedPath = path.trim();
    if (normalizedPath.isEmpty())
      throw new IllegalStateException("Property 'igrp.enum.exposer.path' must not be blank");

    if (!normalizedPath.startsWith("/")) normalizedPath = "/" + normalizedPath;
    while (normalizedPath.length() > 1 && normalizedPath.endsWith("/"))
      normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);

    return normalizedPath;
  }

  private String endpointPath(String endpointName) {
    return ("/".equals(normalizedExposerPath) ? "" : normalizedExposerPath) + "/" + endpointName;
  }

  private record RegisteredEnum(
      Class<? extends Enum<?>> enumClass,
      List<EnumItem<String>> items
  ) {
  }
}
