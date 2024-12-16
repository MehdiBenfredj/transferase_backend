package com.mehdi.oauth.model;

import java.io.Serializable;
import java.util.Objects;

public class SubscriptionPK implements Serializable {
    private String userId;
    private String integrationUserUUID;

    public SubscriptionPK() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIntegrationUserUUID() {
        return integrationUserUUID;
    }

    public void setIntegrationUserUUID(String integrationUserUUID) {
        this.integrationUserUUID = integrationUserUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubscriptionPK that)) return false;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getIntegrationUserUUID(), that.getIntegrationUserUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getIntegrationUserUUID());
    }
}
