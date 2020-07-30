package org.hypertrace.graphql.entity.request;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.graphql.entity.schema.Entity.ENTITY_INCOMING_EDGES_KEY;
import static org.hypertrace.graphql.entity.schema.Entity.ENTITY_OUTGOING_EDGES_KEY;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeArgument;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricRequestBuilder;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

class DefaultEntityRequestBuilder implements EntityRequestBuilder {

  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final MetricRequestBuilder metricRequestBuilder;
  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;
  private final Converter<EntityType, AttributeModelScope> scopeConverter;
  private final EdgeRequestBuilder edgeRequestBuilder;

  @Inject
  DefaultEntityRequestBuilder(
      ResultSetRequestBuilder resultSetRequestBuilder,
      MetricRequestBuilder metricRequestBuilder,
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder,
      Converter<EntityType, AttributeModelScope> scopeConverter,
      EdgeRequestBuilder edgeRequestBuilder) {
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.metricRequestBuilder = metricRequestBuilder;
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
    this.scopeConverter = scopeConverter;
    this.edgeRequestBuilder = edgeRequestBuilder;
  }

  @Override
  public Single<EntityRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {
    EntityType entityType =
        this.argumentDeserializer
            .deserializePrimitive(arguments, EntityTypeArgument.class)
            .orElseThrow();

    return this.scopeConverter
        .convert(entityType)
        .flatMap(scope -> this.build(context, arguments, entityType, scope, selectionSet));
  }

  private Single<EntityRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      EntityType entityType,
      AttributeModelScope scope,
      DataFetchingFieldSelectionSet selectionSet) {
    return zip(
        this.resultSetRequestBuilder.build(
            context, scope, arguments, selectionSet, AggregatableOrderArgument.class),
        this.metricRequestBuilder.build(context, scope, this.getResultSets(selectionSet)),
        this.edgeRequestBuilder.buildIncomingEdgeRequest(
            context, this.timeRange(arguments), this.getIncomingEdges(selectionSet)),
        this.edgeRequestBuilder.buildOutgoingEdgeRequest(
            context, this.timeRange(arguments), this.getOutgoingEdges(selectionSet)),
        (resultSetRequest, metricRequestList, incomingEdges, outgoingEdges) ->
            new DefaultEntityRequest(
                entityType, resultSetRequest, metricRequestList, incomingEdges, outgoingEdges));
  }

  private Stream<SelectedField> getResultSets(DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_RESULTS_NAME));
  }

  private Stream<SelectedField> getIncomingEdges(DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet,
        SelectionQuery.builder()
            .selectionPath(List.of(ResultSet.RESULT_SET_RESULTS_NAME, ENTITY_INCOMING_EDGES_KEY))
            .build());
  }

  private Stream<SelectedField> getOutgoingEdges(DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet,
        SelectionQuery.builder()
            .selectionPath(List.of(ResultSet.RESULT_SET_RESULTS_NAME, ENTITY_OUTGOING_EDGES_KEY))
            .build());
  }

  private TimeRangeArgument timeRange(Map<String, Object> arguments) {
    return this.argumentDeserializer
        .deserializeObject(arguments, TimeRangeArgument.class)
        .orElseThrow();
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEntityRequest implements EntityRequest {
    EntityType entityType;
    ResultSetRequest<AggregatableOrderArgument> resultSetRequest;
    List<MetricRequest> metricRequests;
    EdgeSetGroupRequest incomingEdgeRequests;
    EdgeSetGroupRequest outgoingEdgeRequests;
  }
}