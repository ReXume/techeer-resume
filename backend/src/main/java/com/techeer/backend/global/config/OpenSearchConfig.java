package com.techeer.backend.global.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

	@Value("${opensearch.host:localhost}")
	private String host;

	@Value("${opensearch.port:9200}")
	private int port;

	@Bean
	public RestClient openSearchRestClient() {
		return RestClient.builder(new HttpHost(host, port, "http")).build();
	}

	@Bean
	public OpenSearchClient openSearchClient(RestClient restClient) {
		RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		return new OpenSearchClient(transport);
	}

}
