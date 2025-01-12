package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(SpanProcessingLogicalFilter.TYPE_NAME)
public interface SpanProcessingLogicalFilter {
  String TYPE_NAME = "SpanProcessingLogicalFilter";

  String SPAN_PROCESSING_FILTERS_KEY = "spanFilters";
  String SPAN_PROCESSING_LOGICAL_OPERATOR_KEY = "logicalOperator";

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_LOGICAL_OPERATOR_KEY)
  @GraphQLNonNull
  SpanProcessingLogicalOperator logicalOperator();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTERS_KEY)
  @GraphQLNonNull
  List<SpanProcessingRuleFilter> spanFilters();
}
