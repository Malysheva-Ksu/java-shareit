package ru.practicum.user;

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
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserUpdateDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(RestTemplate restTemplate) {
        super(restTemplate, API_PREFIX);
    }

    @Override
    protected String getApiPath() {
        return API_PREFIX;
    }

    @Configuration
    static class RestTemplateConfig {
        @Autowired
        private RestTemplateBuilder builder;

        @Bean
        public ClientHttpRequestFactory clientHttpRequestFactory() {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            return factory;
        }

        @Bean
        public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory, @Value("${shareit-server.url}") String serverUrl) {
            return builder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                    .requestFactory(() -> clientHttpRequestFactory())
                    .build();
        }
    }

    public ResponseEntity<Object> getAllUsers(int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/{userId}", null, Map.of("userId", userId));
    }

    public ResponseEntity<Object> createUser(UserCreateDto createDto) {
        return post("", createDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto updateDto) {
        return patch("/{userId}", null, Map.of("userId", userId), updateDto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/{userId}", null, Map.of("userId", userId));
    }
}