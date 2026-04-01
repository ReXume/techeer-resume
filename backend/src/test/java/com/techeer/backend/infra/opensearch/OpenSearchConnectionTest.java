package com.techeer.backend.infra.opensearch;

import static org.assertj.core.api.Assertions.assertThat;

import com.techeer.backend.global.config.OpenSearchTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(OpenSearchTestConfig.class)
class OpenSearchConnectionTest {

    @Autowired
    private OpenSearchClient openSearchClient;

    @Test
    @DisplayName("OpenSearch 클라이언트가 컨테이너에 연결되고 인덱스를 생성할 수 있다")
    void opensearch_connection_and_create_index() throws Exception {
        // Given
        String indexName = "test-connection-index";

        // When
        CreateIndexResponse response = openSearchClient.indices().create(c -> c.index(indexName));

        // Then
        assertThat(response.acknowledged()).isTrue();
        assertThat(response.index()).isEqualTo(indexName);

        // Cleanup
        openSearchClient.indices().delete(d -> d.index(indexName));
    }
}
