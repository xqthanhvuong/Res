package com.manager.restaurant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.manager.restaurant.dto.request.MessageSocketRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SocketService {

    final RestTemplate restTemplate;

    @Value("${socket.socket-api-url}")
    String socketApiUrl;

    // Service to send message to socker server
    public ResponseEntity<String> sendMessage(MessageSocketRequest request) {
        return restTemplate.postForEntity(socketApiUrl, request, String.class);
    }

}
