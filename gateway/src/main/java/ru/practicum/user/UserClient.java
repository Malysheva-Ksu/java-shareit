package ru.practicum.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserUpdateDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, Class<? extends ClientHttpRequestFactory> HttpComponentsClientHttpRequestFactory) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUsers(int from, int size) {
        return get("?from={from}&size={size}", null, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> createUser(UserCreateDto createDto) {
        return post("", createDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto updateDto) {
        return patch("/" + userId, updateDto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }
}