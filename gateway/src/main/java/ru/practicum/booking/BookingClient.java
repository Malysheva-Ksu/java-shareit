package ru.practicum.booking;

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
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate, API_PREFIX);
    }

    @Override
    protected String getApiPath() {
        return API_PREFIX;
    }

    public ResponseEntity<Object> bookItem(long userId, BookingRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsForUser(long userId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(long ownerId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
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