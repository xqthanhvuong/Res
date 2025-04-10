package com.manager.restaurant.constant;

public class EndPoint {
    public static final String[] PUBLIC_ENDPOINTS = {"bills/payment-all-bill/{idTable}", "restaurant/get-all-restaurant-of-table/{idTable}","bills/get-all-order-client/{idTable}", "bills/check-bill/{idTable}","bills/order","food/get-by-id-table-client/{idTable}","food/get/{idMenu}","menu/get/*","otp/sendOTP"
            ,"account/login","account/register","document/","auth/log-in", "auth/introspect", "users"
            , "/document/**","departments/download-template",
            "courses/download-template","classes/download-template"
            ,"course/download-template", "/ws/notifications"};
}
