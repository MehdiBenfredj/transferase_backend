package com.mehdi.oauth.repository;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.model.SubscriptionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscription, SubscriptionPK> {
    List<Subscription> findByUserId(String userId);
    List<Subscription> findByIntegrationUserUUID(String integrationUserUUID);
    Optional<Subscription> findByUserIdAndIntegrationUserUUID(String userId, String integrationUserUUID);
}
