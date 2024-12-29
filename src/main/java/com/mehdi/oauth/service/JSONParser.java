package com.mehdi.oauth.service;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.model.User;
import org.json.JSONObject;

public class JSONParser {

    public static Subscription toSubscription(String json) {
        Subscription subscription = new Subscription();
        JSONObject jsonObject = new JSONObject(json);
        subscription.setUserId("1");
        subscription.setService(jsonObject.getJSONObject("integration").getString("type"));
        subscription.setIntegrationUserUUID(jsonObject.getString("integrationUserUUID"));
        return subscription;
    }

    public static String userToJsonString(User user) {
        JSONObject userJson = new JSONObject();
        userJson.put("user_name", user.getUsername());
        userJson.put("email", user.getEmail());
        userJson.put("photo_url", user.getPhotoUrl());

        return userJson.toString();
    }
}
