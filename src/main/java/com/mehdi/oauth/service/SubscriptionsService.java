package com.mehdi.oauth.service;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.repository.SubscriptionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionsService {
    private final SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsService(SubscriptionsRepository subscriptionsRepository) {
        this.subscriptionsRepository = subscriptionsRepository;
    }

    // get all subscriptions by user id
    public List<Subscription> getSubscriptionsByUserId(String userId) {
        return subscriptionsRepository.findByUserId(userId);
    }

    public Subscription getSubscriptionsByUserIdAndService(String userId,String service) {
        return subscriptionsRepository.findByUserIdAndService(userId, service).orElse(null);
    }

    // create new subscription
    public Subscription createSubscription(Subscription subscription) {
        return  subscriptionsRepository.save(subscription);
    }



}
