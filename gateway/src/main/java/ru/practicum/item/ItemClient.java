package ru.practicum.item;

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
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.ItemCreateDto;
import ru.practicum.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate, API_PREFIX);
    }

    @Override
    protected String getApiPath() {
        return API_PREFIX;
    }

    public ResponseEntity<Object> addItem(Long ownerId, ItemCreateDto createDto) {
        return post("", ownerId, createDto);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemUpdateDto updateDto) {
        return patch("/" + itemId, ownerId, updateDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(Long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchAvailableItems(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long authorId, Long itemId, CommentRequestDto commentDto) {
        return post("/" + itemId + "/comment", authorId, commentDto);
    }
}