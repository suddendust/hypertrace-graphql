package org.hypertrace.graphql.service;

import com.typesafe.config.Config;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Value;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

@Value
class DefaultGraphQlServiceConfig implements HypertraceGraphQlServiceConfig {

  private static final Long DEFAULT_CLIENT_TIMEOUT_SECONDS = 10L;

  private static final String SERVICE_NAME_CONFIG = "service.name";
  private static final String SERVICE_PORT_CONFIG = "service.port";

  private static final String GRAPHQL_URL_PATH = "graphql.urlPath";
  private static final String GRAPHQL_CORS_ENABLED = "graphql.corsEnabled";
  private static final String GRAPHQL_TIMEOUT = "graphql.timeout";

  private static final String DEFAULT_TENANT_ID = "defaultTenantId";

  private static final String MAX_IO_THREADS_PROPERTY = "threads.io.max";
  private static final String MAX_REQUEST_THREADS_PROPERTY = "threads.request.max";

  private static final String ATTRIBUTE_SERVICE_HOST_PROPERTY = "attribute.service.host";
  private static final String ATTRIBUTE_SERVICE_PORT_PROPERTY = "attribute.service.port";
  private static final String ATTRIBUTE_SERVICE_CLIENT_TIMEOUT = "attribute.service.timeout";

  private static final String GATEWAY_SERVICE_HOST_PROPERTY = "gateway.service.host";
  private static final String GATEWAY_SERVICE_PORT_PROPERTY = "gateway.service.port";
  private static final String GATEWAY_SERVICE_CLIENT_TIMEOUT = "gateway.service.timeout";

  private static final String ENTITY_SERVICE_HOST_PROPERTY = "entity.service.host";
  private static final String ENTITY_SERVICE_PORT_PROPERTY = "entity.service.port";
  private static final String ENTITY_SERVICE_CLIENT_TIMEOUT = "entity.service.timeout";

  private static final String CONFIG_SERVICE_HOST_PROPERTY = "config.service.host";
  private static final String CONFIG_SERVICE_PORT_PROPERTY = "config.service.port";
  private static final String CONFIG_SERVICE_CLIENT_TIMEOUT = "config.service.timeout";

  String serviceName;
  int servicePort;
  String graphQlUrlPath;
  boolean corsEnabled;
  Duration graphQlTimeout;
  Optional<String> defaultTenantId;
  int maxIoThreads;
  int maxRequestThreads;
  String attributeServiceHost;
  int attributeServicePort;
  Duration attributeServiceTimeout;
  String gatewayServiceHost;
  int gatewayServicePort;
  Duration gatewayServiceTimeout;
  String entityServiceHost;
  int entityServicePort;
  Duration entityServiceTimeout;
  String configServiceHost;
  int configServicePort;
  Duration configServiceTimeout;

  DefaultGraphQlServiceConfig(Config untypedConfig) {
    this.serviceName = untypedConfig.getString(SERVICE_NAME_CONFIG);
    this.servicePort = untypedConfig.getInt(SERVICE_PORT_CONFIG);
    this.graphQlUrlPath = untypedConfig.getString(GRAPHQL_URL_PATH);
    this.corsEnabled = untypedConfig.getBoolean(GRAPHQL_CORS_ENABLED);
    this.graphQlTimeout = untypedConfig.getDuration(GRAPHQL_TIMEOUT);
    this.defaultTenantId = optionallyGet(() -> untypedConfig.getString(DEFAULT_TENANT_ID));
    this.maxIoThreads = untypedConfig.getInt(MAX_IO_THREADS_PROPERTY);
    this.maxRequestThreads = untypedConfig.getInt(MAX_REQUEST_THREADS_PROPERTY);

    this.attributeServiceHost = untypedConfig.getString(ATTRIBUTE_SERVICE_HOST_PROPERTY);
    this.attributeServicePort = untypedConfig.getInt(ATTRIBUTE_SERVICE_PORT_PROPERTY);
    this.gatewayServiceHost = untypedConfig.getString(GATEWAY_SERVICE_HOST_PROPERTY);
    this.gatewayServicePort = untypedConfig.getInt(GATEWAY_SERVICE_PORT_PROPERTY);
    this.entityServiceHost = untypedConfig.getString(ENTITY_SERVICE_HOST_PROPERTY);
    this.entityServicePort = untypedConfig.getInt(ENTITY_SERVICE_PORT_PROPERTY);
    this.configServiceHost = untypedConfig.getString(CONFIG_SERVICE_HOST_PROPERTY);
    this.configServicePort = untypedConfig.getInt(CONFIG_SERVICE_PORT_PROPERTY);

    this.gatewayServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(GATEWAY_SERVICE_CLIENT_TIMEOUT));
    this.attributeServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(ATTRIBUTE_SERVICE_CLIENT_TIMEOUT));
    this.configServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(CONFIG_SERVICE_CLIENT_TIMEOUT));
    this.entityServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(ENTITY_SERVICE_CLIENT_TIMEOUT));
  }

  private Duration getSuppliedDurationOrFallback(Supplier<Duration> durationSupplier) {
    return optionallyGet(durationSupplier)
        .orElse(Duration.ofSeconds(DEFAULT_CLIENT_TIMEOUT_SECONDS));
  }

  private <T> Optional<T> optionallyGet(Supplier<T> valueSupplier) {
    try {
      return Optional.ofNullable(valueSupplier.get());
    } catch (Throwable unused) {
      return Optional.empty();
    }
  }
}
