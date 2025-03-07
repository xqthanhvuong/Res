package com.manager.restaurant.constant;

public class EndPoint {
    public static final String[] PUBLIC_ENDPOINTS = {
            "document/",
            "auth/log-in",
            "auth/introspect",
            "users",
            "/document/**",
            "departments/download-template",
            "courses/download-template",
            "classes/download-template",
            "course/download-template",
            "/ws/notifications",
            "/account/register",
            "/account/login"
    };
}
