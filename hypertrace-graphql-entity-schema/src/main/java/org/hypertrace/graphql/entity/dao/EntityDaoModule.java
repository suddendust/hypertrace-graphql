package org.hypertrace.graphql.entity.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.CallCredentials;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.TimeAggregation;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.entity.Entity;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricSeriesRequest;
import org.hypertrace.graphql.metric.schema.MetricAggregationContainer;
import org.hypertrace.graphql.metric.schema.MetricContainer;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

public class EntityDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EntityDao.class).to(GatewayServiceEntityDao.class);
    requireBinding(CallCredentials.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GraphQlRequestContextBuilder.class);
    requireBinding(GrpcChannelRegistry.class);

    requireBinding(
        Key.get(new TypeLiteral<Converter<Collection<AttributeRequest>, Set<Expression>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<
                    List<AttributeAssociation<AggregatableOrderArgument>>,
                    List<OrderByExpression>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<Collection<AttributeAssociation<FilterArgument>>, Filter>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<Collection<MetricAggregationRequest>, Set<Expression>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<Collection<MetricSeriesRequest>, Set<TimeAggregation>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                BiConverter<
                    Collection<MetricRequest>, Entity, Map<String, MetricContainer>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                BiConverter<
                    Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>>() {}));
    requireBinding(
        Key.get(
            new TypeLiteral<
                BiConverter<
                    Collection<MetricAggregationRequest>,
                    Map<String, AggregatedMetricValue>,
                    Map<String, MetricAggregationContainer>>>() {}));

    requireBinding(Key.get(new TypeLiteral<Converter<String, EntityType>>() {}));
    requireBinding(Key.get(new TypeLiteral<Converter<EntityType, String>>() {}));
  }
}