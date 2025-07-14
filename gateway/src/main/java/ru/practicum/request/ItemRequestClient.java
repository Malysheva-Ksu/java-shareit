package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private final Class<? extends ClientHttpRequestFactory> HttpComponentsClientHttpRequestFactory;

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, Class<? extends ClientHttpRequestFactory> httpComponentsClientHttpRequestFactory) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(httpComponentsClientHttpRequestFactory)
                        .build()
        );
        HttpComponentsClientHttpRequestFactory = httpComponentsClientHttpRequestFactory;
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
}