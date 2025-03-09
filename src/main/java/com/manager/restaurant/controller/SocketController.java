package com.manager.restaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manager.restaurant.dto.request.MessageSocketRequest;
import com.manager.restaurant.service.SocketService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/socket")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketController {

    // Inject Socket Service
    SocketService socketService;

    // APi to test socket controller
    @PostMapping("/test")
    public ResponseEntity<String> sendMessage(@RequestBody MessageSocketRequest request) {
        return socketService.sendMessage(request);
    }

}
