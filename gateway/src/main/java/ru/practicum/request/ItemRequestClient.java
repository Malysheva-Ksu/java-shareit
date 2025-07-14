package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate, API_PREFIX);
    }

    @Override
    protected String getApiPath() {
        return API_PREFIX;
    }

    public ResponseEntity<Object> create(Long requesterId, ItemRequestCreateDto createDto) {
        return post("", requesterId, createDto);
    }

    public ResponseEntity<Object> findOwn(Long requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> findAll(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    @Configuration
    static class RestTemplateConfig {
        @Autowired
        private RestTemplateBuilder builder;

        @Bean
        public ClientHttpRequestFactory clientHttpRequestFactory() {
            return new HttpComponentsClientHttpRequestFactory();
        }

        @Bean
        public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory, @Value("${shareit-server.url}") String serverUrl) {
            return builder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                    .requestFactory(() -> clientHttpRequestFactory())
                    .build();
        }
    }
}