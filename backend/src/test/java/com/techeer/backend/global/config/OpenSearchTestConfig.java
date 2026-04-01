package com.techeer.backend.global.config;

import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration
public class OpenSearchTestConfig {

    private static final int OPENSEARCH_PORT = 9200;

    private static final String OPENSEARCH_IMAGE =
            "opensearchproject/opensearch:2.11.0";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ElasticsearchContainer openSearchContainer() {
        ElasticsearchContainer container = new ElasticsearchContainer(
                DockerImageName.parse(OPENSEARCH_IMAGE)
                        .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
                .withEnv("discovery.type", "single-node")
                .withEnv("plugins.security.disabled", "true")
                .withEnv("OPENSEARCH_INITIAL_ADMIN_PASSWORD", "")
                .withExposedPorts(OPENSEARCH_PORT)
                .waitingFor(
                        new HttpWaitStrategy()
                                .forPort(OPENSEARCH_PORT)
                                .forPath("/_cluster/health")
                                .withStartupTimeout(Duration.ofSeconds(120)));
        return container;
    }

    @Bean
    @Primary
    public OpenSearchClient openSearchClient(ElasticsearchContainer openSearchContainer) {
        String host = openSearchContainer.getHost();
        int port = openSearchContainer.getMappedPort(OPENSEARCH_PORT);

        RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }
}
