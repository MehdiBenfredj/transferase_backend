package com.mehdi.oauth.model;

import jakarta.persistence.*;

@Entity
@IdClass(SubscriptionPK.class)
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "service")
    private String service;

    @Id
    @Column(name = "integration_user_uuid")
    private String integrationUserUUID;

    public Subscription() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getIntegrationUserUUID() {
        return integrationUserUUID;
    }

    public void setIntegrationUserUUID(String integrationUserUUID) {
        this.integrationUserUUID = integrationUserUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Subscription that)) return false;
        return getUserId().equals(that.getUserId()) && getIntegrationUserUUID().equals(that.getIntegrationUserUUID());
    }

    @Override
    public int hashCode() {
        return getUserId().hashCode() + getIntegrationUserUUID().hashCode();
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "userId='" + userId + '\'' +
                ", service='" + service + '\'' +
                ", integrationUserUUID='" + integrationUserUUID + '\'' +
                '}';
    }
}
