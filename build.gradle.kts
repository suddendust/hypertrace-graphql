plugins {
  id("org.hypertrace.repository-plugin") version "0.2.0"
  id("org.hypertrace.ci-utils-plugin") version "0.1.1"
  id("org.hypertrace.jacoco-report-plugin") version "0.1.0" apply false
  id("org.hypertrace.docker-java-application-plugin") version "0.3.0" apply false
  id("org.hypertrace.docker-publish-plugin") version "0.3.0" apply false
}

subprojects {
  group = "org.hypertrace.graphql"
}