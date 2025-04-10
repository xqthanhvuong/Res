package com.manager.restaurant.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.NonFinal;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotiClientConfig {

    public ObjectMapper mapper = new ObjectMapper();

    @Value("${pushy.secret}")
    @NonFinal
    public String SECRET_API_KEY;

    public  void sendPush(PushyPushRequest req) throws Exception {
        // Get custom HTTP client
        HttpClient client = new DefaultHttpClient();

        // Create POST request
        HttpPost request = new HttpPost("https://api.pushy.me/push?api_key=" + SECRET_API_KEY);

        // Set content type to JSON
        request.addHeader("Content-Type", "application/json");

        // Convert post data to JSON
        byte[] json = mapper.writeValueAsBytes(req);

        // Send post data as byte array
        request.setEntity(new ByteArrayEntity(json));

        // Execute the request
        HttpResponse response = client.execute(request, new BasicHttpContext());

        // Get response JSON as string
        String responseJSON = EntityUtils.toString(response.getEntity());

        // Convert JSON response into HashMap
        Map<String, Object> map = mapper.readValue(responseJSON, Map.class);

        // Got an error?
        if (map.containsKey("error")) {
            // Throw it
            throw new Exception(map.get("error").toString());
        }
    }

    public static class PushyPushRequest {
        public Object to;
        public Object data;
        public Object notification;

        public PushyPushRequest(Object data, Object to, Object notification) {
            this.to = to;
            this.data = data;
            this.notification = notification;
        }
    }
}

