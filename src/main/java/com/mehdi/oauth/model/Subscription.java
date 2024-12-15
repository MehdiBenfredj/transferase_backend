package com.mehdi.oauth.model;

import jakarta.persistence.Column;

//@Entity
//@Table(name = "subscriptions")
public class Subscription {


    @Column(name = "user_id")
    private String userId;

    @Column(name = "service")
    private String service;

    @Column(name = "integrationUserUUID")
    private String integrationUserUUID;
}
