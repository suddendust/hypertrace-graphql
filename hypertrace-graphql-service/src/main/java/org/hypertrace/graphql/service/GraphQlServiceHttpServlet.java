package org.hypertrace.graphql.service;

import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GraphQlServiceHttpServlet extends GraphQLHttpServlet {

  private static final Logger LOG = LoggerFactory.getLogger(GraphQlServiceHttpServlet.class);

  private final GraphQLConfiguration configuration;

  GraphQlServiceHttpServlet(GraphQLConfiguration configuration) {
    LOG.info("Async timeout set in graphql config is: {}", configuration.getAsyncTimeout());
    this.configuration = configuration;
  }

  @Override
  protected GraphQLConfiguration getConfiguration() {
    return configuration;
  }
}
