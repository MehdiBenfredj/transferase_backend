package com.mehdi.oauth.service;

import com.mehdi.oauth.model.Subscription;
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
}
